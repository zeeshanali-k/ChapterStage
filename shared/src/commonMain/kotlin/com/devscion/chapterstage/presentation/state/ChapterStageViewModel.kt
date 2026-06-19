package com.devscion.chapterstage.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscion.chapterstage.core.common.DomainException
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.usecase.CreateTextChapterUseCase
import com.devscion.chapterstage.domain.usecase.GetAgentTraceUseCase
import com.devscion.chapterstage.domain.usecase.GetExperienceMetadataUseCase
import com.devscion.chapterstage.domain.usecase.GetRecentJobsUseCase
import com.devscion.chapterstage.domain.usecase.ObserveGenerationEventsUseCase
import com.devscion.chapterstage.domain.usecase.StartGenerationJobUseCase
import com.devscion.chapterstage.domain.usecase.UploadChapterUseCase
import com.devscion.chapterstage.presentation.mapper.toChapterInput
import com.devscion.chapterstage.presentation.mapper.toDomain
import com.devscion.chapterstage.presentation.mapper.toUiModel
import com.devscion.chapterstage.presentation.mapper.toUiSnapshot
import com.devscion.chapterstage.presentation.mapper.toUserMessage
import com.devscion.chapterstage.presentation.model.AgentStatus
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.ChapterSourceDraft
import com.devscion.chapterstage.presentation.model.GenerationSnapshot
import com.devscion.chapterstage.presentation.model.RecentJobUiModel
import com.devscion.chapterstage.presentation.model.SourceMode
import com.devscion.chapterstage.presentation.model.ViewerLoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ChapterStageViewModel(
    private val demoContent: ChapterStageDemoContent,
    private val getRecentJobs: GetRecentJobsUseCase,
    private val getAgentTrace: GetAgentTraceUseCase,
    private val getExperienceMetadata: GetExperienceMetadataUseCase,
    private val createTextChapter: CreateTextChapterUseCase,
    private val uploadChapter: UploadChapterUseCase,
    private val startGenerationJob: StartGenerationJobUseCase,
    private val observeGenerationEvents: ObserveGenerationEventsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(
        ChapterStageState(
            recentJobs = demoContent.recentJobs,
            agents = demoContent.agents,
            sampleText = demoContent.sampleText,
            sampleChapterTitle = demoContent.sampleChapterTitle,
            sampleUrl = demoContent.sampleUrl,
            snapshot = GenerationSnapshot(
                statuses = demoContent.agents.associate { it.id to com.devscion.chapterstage.presentation.model.AgentStatus.Waiting },
            ),
        ),
    )
    val state: StateFlow<ChapterStageState> = _state.asStateFlow()

    private val _events = Channel<ChapterStageEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // These handles keep non-Compose workflow state out of the immutable UI snapshot.
    private var generationJob: Job? = null
    private var selectedChapterFile: ChapterFile? = null
    private var currentJobId: String? = null

    init {
        onAction(ChapterStageAction.LoadHome)
    }

    fun onAction(action: ChapterStageAction) {
        when (action) {
            ChapterStageAction.LoadHome -> loadRecentJobs()
            ChapterStageAction.NavigateCreateChapter -> sendEvent(ChapterStageEvent.NavigateCreateChapter)
            ChapterStageAction.NavigateBack -> sendEvent(ChapterStageEvent.NavigateBack)
            ChapterStageAction.ContinueToSettings -> sendEvent(ChapterStageEvent.NavigateGenerationSettings)
            ChapterStageAction.StartWorkflow -> startWorkflow(useSampleContent = false)
            ChapterStageAction.StartDemoFlow -> startWorkflow(useSampleContent = true)
            ChapterStageAction.OpenViewer -> openViewer()
            ChapterStageAction.RetryViewer -> retryViewer()
            ChapterStageAction.ViewTrace -> openTrace()
            ChapterStageAction.RetryProgress -> retryProgress()
            ChapterStageAction.RetryTrace -> refreshTrace()
            ChapterStageAction.PickChapterFile -> startChapterFilePicker()
            ChapterStageAction.CancelChapterFilePicker -> cancelChapterFilePicker()
            ChapterStageAction.RemoveChapterFile -> removeChapterFile()
            ChapterStageAction.ExternalViewerOpenFailed -> setError(
                throwable = DomainException.Validation(
                    code = "VIEWER_OPEN_FAILED",
                    message = "Could not open the chapter link on this device.",
                ),
                surface = ErrorSurface.Viewer,
            ).also {
                _state.update { state -> state.copy(viewerState = ViewerLoadState.Error) }
            }
            ChapterStageAction.DismissError -> clearErrors()
            is ChapterStageAction.ChangeSourceMode -> _state.update {
                it.copy(sourceMode = action.sourceMode, createErrorMessage = null)
            }
            is ChapterStageAction.UpdateSourceDraft -> {
                if (action.draft.selectedFileName == null) selectedChapterFile = null
                _state.update { it.copy(sourceDraft = action.draft, createErrorMessage = null) }
            }
            is ChapterStageAction.UpdateSettings -> _state.update {
                it.copy(settings = action.settings, settingsErrorMessage = null)
            }
            is ChapterStageAction.OpenRecentJob -> openRecentJob(action.job)
            is ChapterStageAction.SelectChapterFile -> selectChapterFile(action.file.toDomain())
            is ChapterStageAction.FilePickerFailed -> failChapterFilePicker(action.message)
        }
    }

    private fun loadRecentJobs() {
        viewModelScope.launch {
            _state.update { it.copy(isHomeLoading = true, homeErrorMessage = null) }
            getRecentJobs()
                .onSuccess { jobs ->
                    _state.update { state ->
                        state.copy(
                            isHomeLoading = false,
                            recentJobs = jobs.map { it.toUiModel() }.ifEmpty { demoContent.recentJobs },
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isHomeLoading = false) }
                    setError(throwable = throwable, surface = ErrorSurface.Home)
                }
        }
    }

    private fun startWorkflow(useSampleContent: Boolean) {
        if (_state.value.isStartingWorkflow) return

        viewModelScope.launch {
            generationJob?.cancel()

            val current = _state.value
            val sourceDraft = if (useSampleContent) {
                ChapterSourceDraft(
                    bookTitle = demoContent.sampleBookTitle,
                    chapterTitle = demoContent.sampleChapterTitle,
                    text = demoContent.sampleText,
                )
            } else {
                current.sourceDraft
            }

            _state.update {
                it.copy(
                    sourceDraft = sourceDraft,
                    isStartingWorkflow = true,
                    settingsErrorMessage = null,
                    progressErrorMessage = null,
                    traceErrorMessage = null,
                    snapshot = GenerationSnapshot(
                        statuses = demoContent.agents.associate { agent ->
                            agent.id to AgentStatus.Waiting
                        },
                    ),
                )
            }

            val chapter = when {
                useSampleContent || current.sourceMode == SourceMode.PasteText -> {
                    createTextChapter(sourceDraft.toChapterInput(fallbackText = demoContent.sampleText))
                }
                else -> {
                    val file = selectedChapterFile
                    if (file == null) {
                        Result.failure(
                            DomainException.Validation(
                                code = "FILE_PICKER_CANCELLED",
                                message = "No file was selected.",
                            ),
                        )
                    } else {
                        uploadChapter(file)
                    }
                }
            }
                .getOrElse { throwable ->
                    _state.update { it.copy(isStartingWorkflow = false) }
                    setError(throwable = throwable, surface = ErrorSurface.Settings)
                    return@launch
                }

            val job = startGenerationJob(chapter.id, current.settings.toDomain())
                .getOrElse { throwable ->
                    _state.update { it.copy(isStartingWorkflow = false) }
                    setError(throwable = throwable, surface = ErrorSurface.Settings)
                    return@launch
                }

            currentJobId = job.id
            _state.update {
                it.copy(
                    isStartingWorkflow = false,
                    snapshot = job.toUiSnapshot(agents = demoContent.agents).copy(
                        title = chapter.title ?: sourceDraft.chapterTitle.ifBlank { demoContent.sampleChapterTitle },
                        subtitle = sourceDraft.bookTitle.ifBlank { demoContent.sampleBookTitle },
                    ),
                )
            }
            _events.send(ChapterStageEvent.NavigateGenerationProgress)
            observeGeneration(job.id)
        }
    }

    private fun observeGeneration(jobId: String) {
        generationJob = viewModelScope.launch {
            val elapsedSeconds = MutableStateFlow(0)

            val tickerJob = launch {
                while (isActive) {
                    delay(1_000)
                    if (_state.value.snapshot.isComplete || _state.value.snapshot.jobId != jobId) break
                    elapsedSeconds.value += 1
                    _state.update { state ->
                        if (state.snapshot.isComplete || state.snapshot.jobId != jobId) return@update state
                        state.copy(snapshot = state.snapshot.copy(elapsedSeconds = elapsedSeconds.value))
                    }
                }
            }

            observeGenerationEvents(jobId)
                .catch { throwable ->
                    setError(throwable = throwable, surface = ErrorSurface.Progress)
                    setError(throwable = throwable, surface = ErrorSurface.Trace)
                }
                .collect { job ->
                    _state.update { state ->
                        state.copy(
                            snapshot = job.toUiSnapshot(agents = demoContent.agents)
                                .copy(elapsedSeconds = elapsedSeconds.value),
                            progressErrorMessage = job.errorMessage,
                        )
                    }
                }

            tickerJob.cancel()
        }
    }

    private fun openRecentJob(job: RecentJobUiModel) {
        currentJobId = job.id
        when (job.status) {
            "generating" -> {
                _state.update {
                    it.copy(
                        snapshot = job.toSnapshot(agents = demoContent.agents),
                        progressErrorMessage = null,
                    )
                }
                _events.trySend(ChapterStageEvent.NavigateGenerationProgress)
                observeGeneration(job.id)
            }
            "ready" -> {
                _state.update {
                    it.copy(
                        snapshot = job.toSnapshot(agents = demoContent.agents),
                        viewerState = ViewerLoadState.Loaded,
                        viewerErrorMessage = null,
                    )
                }
                sendEvent(ChapterStageEvent.NavigateExperienceViewer)
            }
            "failed" -> {
                _state.update {
                    it.copy(
                        snapshot = job.toSnapshot(agents = demoContent.agents),
                        progressErrorMessage = job.errorMessage ?: "This generation job failed.",
                    )
                }
                _events.trySend(ChapterStageEvent.NavigateGenerationProgress)
            }
            else -> {
                _state.update {
                    it.copy(snapshot = job.toSnapshot(agents = demoContent.agents))
                }
                _events.trySend(ChapterStageEvent.NavigateGenerationProgress)
            }
        }
    }

    private fun openViewer() {
        _state.update { it.copy(viewerState = ViewerLoadState.Loading, viewerErrorMessage = null) }
        sendEvent(ChapterStageEvent.NavigateExperienceViewer)
        viewModelScope.launch {
            loadViewerExperience()
        }
    }

    private fun retryViewer() {
        _state.update { it.copy(viewerState = ViewerLoadState.Loading, viewerErrorMessage = null) }
        viewModelScope.launch {
            loadViewerExperience()
        }
    }

    private suspend fun loadViewerExperience() {
        val snapshot = _state.value.snapshot
        when {
            snapshot.publicUrl != null -> {
                delay(ViewerLoadDelayMillis)
                _state.update { it.copy(viewerState = ViewerLoadState.Loaded) }
            }
            snapshot.experienceId != null -> {
                getExperienceMetadata(snapshot.experienceId)
                    .onSuccess { metadata ->
                        _state.update { state ->
                            state.copy(
                                snapshot = state.snapshot.copy(publicUrl = metadata.publicUrl),
                                viewerState = ViewerLoadState.Loaded,
                                viewerErrorMessage = null,
                            )
                        }
                    }
                    .onFailure { throwable ->
                        _state.update { it.copy(viewerState = ViewerLoadState.Error) }
                        setError(throwable = throwable, surface = ErrorSurface.Viewer)
                    }
            }
            else -> {
                _state.update { it.copy(viewerState = ViewerLoadState.Error) }
                setError(
                    throwable = DomainException.NotFound("Generated chapter URL"),
                    surface = ErrorSurface.Viewer,
                )
            }
        }
    }

    private fun retryProgress() {
        val jobId = currentJobId ?: _state.value.snapshot.jobId
        if (jobId == null) {
            startWorkflow(useSampleContent = false)
            return
        }
        _state.update { it.copy(progressErrorMessage = null) }
        observeGeneration(jobId)
    }

    private fun openTrace() {
        sendEvent(ChapterStageEvent.NavigateAgentTrace)
        refreshTrace()
    }

    private fun refreshTrace() {
        val jobId = currentJobId ?: _state.value.snapshot.jobId ?: return
        viewModelScope.launch {
            _state.update { it.copy(traceErrorMessage = null) }
            getAgentTrace(jobId)
                .onSuccess { events ->
                    _state.update { state ->
                        state.copy(
                            snapshot = state.snapshot.copy(events = events.map { it.toUiModel() }),
                            traceErrorMessage = null,
                        )
                    }
                }
                .onFailure { throwable -> setError(throwable = throwable, surface = ErrorSurface.Trace) }
        }
    }

    private fun startChapterFilePicker() {
        if (_state.value.isPickingFile) return

        _state.update { it.copy(isPickingFile = true, createErrorMessage = null) }
    }

    private fun cancelChapterFilePicker() {
        _state.update { it.copy(isPickingFile = false) }
    }

    private fun failChapterFilePicker(message: String) {
        _state.update { it.copy(isPickingFile = false) }
        setError(
            throwable = DomainException.Validation(
                code = "FILE_PICKER_FAILED",
                message = message,
            ),
            surface = ErrorSurface.Create,
        )
    }

    private fun selectChapterFile(file: ChapterFile) {
        if (file.extension !in SupportedChapterFileExtensions) {
            selectedChapterFile = null
            _state.update { state ->
                state.copy(
                    isPickingFile = false,
                    sourceDraft = state.sourceDraft.withoutSelectedFile(),
                )
            }
            setError(
                throwable = DomainException.Validation(
                    code = "INVALID_FILE_TYPE",
                    message = "Only PDF and TXT files are supported right now.",
                ),
                surface = ErrorSurface.Create,
            )
            return
        }

        if (file.bytes.size > MaxChapterFileBytes) {
            selectedChapterFile = null
            _state.update { state ->
                state.copy(
                    isPickingFile = false,
                    sourceDraft = state.sourceDraft.withoutSelectedFile(),
                )
            }
            setError(
                throwable = DomainException.Validation(
                    code = "FILE_TOO_LARGE",
                    message = "This file is too large for the MVP. Try a smaller chapter.",
                ),
                surface = ErrorSurface.Create,
            )
            return
        }

        selectedChapterFile = file
        _state.update { state ->
            state.copy(
                isPickingFile = false,
                sourceDraft = state.sourceDraft.copy(
                    selectedFileName = file.fileName,
                    selectedFileSizeLabel = file.bytes.size.toLong().toFileSizeLabel(),
                    selectedFileExtension = file.extension.uppercase(),
                ),
                createErrorMessage = null,
            )
        }
    }

    private fun removeChapterFile() {
        selectedChapterFile = null
        _state.update {
            it.copy(
                sourceDraft = it.sourceDraft.withoutSelectedFile(),
                createErrorMessage = null,
            )
        }
    }

    private fun sendEvent(event: ChapterStageEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    private fun setError(throwable: Throwable, surface: ErrorSurface) {
        val message = (throwable as? DomainException)?.toUserMessage()
            ?: "Something unexpected happened. Try again."
        _state.update { state ->
            when (surface) {
                ErrorSurface.Home -> state.copy(homeErrorMessage = message)
                ErrorSurface.Create -> state.copy(createErrorMessage = message)
                ErrorSurface.Settings -> state.copy(settingsErrorMessage = message)
                ErrorSurface.Progress -> state.copy(progressErrorMessage = message)
                ErrorSurface.Trace -> state.copy(traceErrorMessage = message)
                ErrorSurface.Viewer -> state.copy(viewerErrorMessage = message)
            }
        }
    }

    private fun clearErrors() {
        _state.update {
            it.copy(
                homeErrorMessage = null,
                createErrorMessage = null,
                settingsErrorMessage = null,
                progressErrorMessage = null,
                traceErrorMessage = null,
                viewerErrorMessage = null,
            )
        }
    }

    private companion object {
        const val ViewerLoadDelayMillis = 900L
        const val MaxChapterFileBytes = 20 * 1024 * 1024
        val SupportedChapterFileExtensions = setOf("pdf", "txt")
    }

    private enum class ErrorSurface {
        Home,
        Create,
        Settings,
        Progress,
        Trace,
        Viewer,
    }
}

private fun ChapterSourceDraft.withoutSelectedFile(): ChapterSourceDraft =
    copy(
        selectedFileName = null,
        selectedFileSizeLabel = null,
        selectedFileExtension = null,
    )

private fun RecentJobUiModel.toSnapshot(
    agents: List<com.devscion.chapterstage.presentation.model.AgentUiModel>,
): GenerationSnapshot {
    val isReady = status == "ready"
    val statuses = if (isReady) {
        agents.associate { it.id to AgentStatus.Completed }
    } else {
        agents.associate { it.id to AgentStatus.Waiting }
    }
    return GenerationSnapshot(
        jobId = id,
        experienceId = experienceId,
        title = title,
        subtitle = book,
        statuses = statuses,
        activeAgentId = null,
        progress = if (isReady) 100 else progress,
        isComplete = isReady,
        publicUrl = publicUrl,
    )
}

private fun Long.toFileSizeLabel(): String =
    when {
        this < FileSizeKilobyte -> "$this B"
        this < FileSizeMegabyte -> "${this / FileSizeKilobyte} KB"
        else -> {
            val tenths = this * 10 / FileSizeMegabyte
            val whole = tenths / 10
            val fraction = tenths % 10
            if (fraction == 0L) "$whole MB" else "$whole.$fraction MB"
        }
    }

private const val FileSizeKilobyte = 1024L
private const val FileSizeMegabyte = FileSizeKilobyte * 1024L
