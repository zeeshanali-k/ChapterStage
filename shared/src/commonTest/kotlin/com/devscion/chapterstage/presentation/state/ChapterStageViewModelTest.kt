package com.devscion.chapterstage.presentation.state

import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.model.ChapterInput
import com.devscion.chapterstage.domain.model.ExperienceMetadata
import com.devscion.chapterstage.domain.model.GenerationAgentStatus
import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.model.GenerationSettings
import com.devscion.chapterstage.domain.model.RecentGenerationJob
import com.devscion.chapterstage.domain.repository.ChapterRepository
import com.devscion.chapterstage.domain.repository.GenerationRepository
import com.devscion.chapterstage.domain.usecase.CreateTextChapterUseCase
import com.devscion.chapterstage.domain.usecase.GetAgentTraceUseCase
import com.devscion.chapterstage.domain.usecase.GetExperienceMetadataUseCase
import com.devscion.chapterstage.domain.usecase.GetRecentJobsUseCase
import com.devscion.chapterstage.domain.usecase.ObserveGenerationEventsUseCase
import com.devscion.chapterstage.domain.usecase.StartGenerationJobUseCase
import com.devscion.chapterstage.domain.usecase.UploadChapterUseCase
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.PickedChapterFile
import com.devscion.chapterstage.presentation.model.RecentJobUiModel
import com.devscion.chapterstage.presentation.model.ViewerLoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChapterStageViewModelTest {
    @Test
    fun `given selected text file, when action arrives, then draft contains file metadata`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(ChapterStageAction.PickChapterFile)
            assertTrue(viewModel.state.value.isPickingFile)

            viewModel.onAction(
                ChapterStageAction.SelectChapterFile(
                    PickedChapterFile(
                        fileName = "photosynthesis.txt",
                        bytes = ByteArray(2_048),
                        contentType = "text/plain",
                    ),
                ),
            )

            val draft = viewModel.state.value.sourceDraft
            assertFalse(viewModel.state.value.isPickingFile)
            assertEquals("photosynthesis.txt", draft.selectedFileName)
            assertEquals("2 KB", draft.selectedFileSizeLabel)
            assertEquals("TXT", draft.selectedFileExtension)
            assertNull(viewModel.state.value.createErrorMessage)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `given unsupported file, when selected, then create error is shown`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(ChapterStageAction.PickChapterFile)
            viewModel.onAction(
                ChapterStageAction.SelectChapterFile(
                    PickedChapterFile(
                        fileName = "chapter.docx",
                        bytes = ByteArray(128),
                        contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    ),
                ),
            )

            assertFalse(viewModel.state.value.isPickingFile)
            assertNull(viewModel.state.value.sourceDraft.selectedFileName)
            assertEquals(
                "Only PDF and TXT files are supported right now.",
                viewModel.state.value.createErrorMessage,
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `given completed recent job, when opened, then viewer uses backend url`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(
                ChapterStageAction.OpenRecentJob(
                    RecentJobUiModel(
                        id = "job-1",
                        title = "Generated",
                        book = "ChapterStage",
                        status = "ready",
                        style = "Visual Story",
                        updatedAt = "now",
                        progress = 100,
                        experienceId = "exp-1",
                        publicUrl = "http://localhost:8000/public/experiences/exp-1/index.html",
                    ),
                ),
            )
            advanceUntilIdle()

            assertEquals("job-1", viewModel.state.value.snapshot.jobId)
            assertEquals("exp-1", viewModel.state.value.snapshot.experienceId)
            assertEquals(
                "http://localhost:8000/public/experiences/exp-1/index.html",
                viewModel.state.value.snapshot.publicUrl,
            )
            assertTrue(viewModel.state.value.snapshot.isComplete)
            assertEquals(ViewerLoadState.Loaded, viewModel.state.value.viewerState)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun createViewModel(): ChapterStageViewModel {
        val chapterRepository = FakeChapterRepository()
        val generationRepository = FakeGenerationRepository()

        return ChapterStageViewModel(
            demoContent = ChapterStageDemoContent(),
            getRecentJobs = GetRecentJobsUseCase(generationRepository),
            getAgentTrace = GetAgentTraceUseCase(generationRepository),
            getExperienceMetadata = GetExperienceMetadataUseCase(generationRepository),
            createTextChapter = CreateTextChapterUseCase(chapterRepository),
            uploadChapter = UploadChapterUseCase(chapterRepository),
            startGenerationJob = StartGenerationJobUseCase(generationRepository),
            observeGenerationEvents = ObserveGenerationEventsUseCase(generationRepository),
        )
    }
}

private class FakeChapterRepository : ChapterRepository {
    override suspend fun createTextChapter(input: ChapterInput): Result<Chapter> =
        Result.success(input.toChapter(sourceType = "text"))

    override suspend fun uploadChapter(file: ChapterFile): Result<Chapter> =
        Result.success(
            Chapter(
                id = "chapter-upload",
                bookId = "book-1",
                title = file.fileName,
                sourceType = "file",
                createdAt = "now",
            ),
        )

    private fun ChapterInput.toChapter(sourceType: String): Chapter =
        Chapter(
            id = "chapter-text",
            bookId = "book-1",
            title = chapterTitle,
            sourceType = sourceType,
            createdAt = "now",
        )
}

private class FakeGenerationRepository : GenerationRepository {
    override suspend fun getRecentJobs(): Result<List<RecentGenerationJob>> = Result.success(emptyList())

    override suspend fun startGeneration(
        chapterId: String,
        settings: GenerationSettings,
    ): Result<GenerationJob> =
        Result.success(
            GenerationJob(
                id = "job-1",
                chapterId = chapterId,
                status = "running",
                progress = 0,
                currentStep = "Starting",
                activeAgentId = null,
                agentStatuses = mapOf("coordinator" to GenerationAgentStatus.Waiting),
                events = emptyList(),
                elapsedSeconds = 0,
            ),
        )

    override fun observeGeneration(jobId: String): Flow<GenerationJob> = emptyFlow()

    override suspend fun getTrace(jobId: String): Result<List<AgentTraceEvent>> = Result.success(emptyList())

    override suspend fun getExperienceMetadata(experienceId: String): Result<ExperienceMetadata> =
        Result.success(
            ExperienceMetadata(
                id = experienceId,
                title = "Generated chapter",
                publicUrl = "https://chapterstage.app/c/$experienceId",
                status = "ready",
            ),
        )
}
