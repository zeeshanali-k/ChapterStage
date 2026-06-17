package com.devscion.chapterstage.presentation.state

import androidx.compose.runtime.Immutable
import com.devscion.chapterstage.presentation.model.AgentUiModel
import com.devscion.chapterstage.presentation.model.ChapterSourceDraft
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft
import com.devscion.chapterstage.presentation.model.GenerationSnapshot
import com.devscion.chapterstage.presentation.model.PickedChapterFile
import com.devscion.chapterstage.presentation.model.RecentJobUiModel
import com.devscion.chapterstage.presentation.model.SourceMode
import com.devscion.chapterstage.presentation.model.ViewerLoadState

@Immutable
data class ChapterStageState(
    val sourceMode: SourceMode = SourceMode.PasteText,
    val sourceDraft: ChapterSourceDraft = ChapterSourceDraft(),
    val settings: GenerationSettingsDraft = GenerationSettingsDraft(),
    val recentJobs: List<RecentJobUiModel> = emptyList(),
    val agents: List<AgentUiModel> = emptyList(),
    val snapshot: GenerationSnapshot = GenerationSnapshot(),
    val sampleText: String = "",
    val sampleChapterTitle: String = "",
    val sampleUrl: String = "",
    val viewerState: ViewerLoadState = ViewerLoadState.Loaded,
    val isHomeLoading: Boolean = false,
    val isPickingFile: Boolean = false,
    val isStartingWorkflow: Boolean = false,
    val homeErrorMessage: String? = null,
    val createErrorMessage: String? = null,
    val settingsErrorMessage: String? = null,
    val progressErrorMessage: String? = null,
    val traceErrorMessage: String? = null,
    val viewerErrorMessage: String? = null,
)

sealed interface ChapterStageAction {
    data object LoadHome : ChapterStageAction
    data object NavigateCreateChapter : ChapterStageAction
    data object NavigateBack : ChapterStageAction
    data object ContinueToSettings : ChapterStageAction
    data object StartWorkflow : ChapterStageAction
    data object StartDemoFlow : ChapterStageAction
    data object OpenViewer : ChapterStageAction
    data object RetryViewer : ChapterStageAction
    data object ViewTrace : ChapterStageAction
    data object RetryProgress : ChapterStageAction
    data object RetryTrace : ChapterStageAction
    data object PickChapterFile : ChapterStageAction
    data object CancelChapterFilePicker : ChapterStageAction
    data object RemoveChapterFile : ChapterStageAction
    data object ExternalViewerOpenFailed : ChapterStageAction
    data object DismissError : ChapterStageAction
    data class ChangeSourceMode(val sourceMode: SourceMode) : ChapterStageAction
    data class UpdateSourceDraft(val draft: ChapterSourceDraft) : ChapterStageAction
    data class UpdateSettings(val settings: GenerationSettingsDraft) : ChapterStageAction
    data class OpenRecentJob(val job: RecentJobUiModel) : ChapterStageAction
    data class SelectChapterFile(val file: PickedChapterFile) : ChapterStageAction
    data class FilePickerFailed(val message: String) : ChapterStageAction
}

sealed interface ChapterStageEvent {
    data object NavigateHome : ChapterStageEvent
    data object NavigateCreateChapter : ChapterStageEvent
    data object NavigateGenerationSettings : ChapterStageEvent
    data object NavigateGenerationProgress : ChapterStageEvent
    data object NavigateAgentTrace : ChapterStageEvent
    data object NavigateExperienceViewer : ChapterStageEvent
    data object NavigateBack : ChapterStageEvent
}
