package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.StageInlineError
import com.devscion.chapterstage.presentation.components.StageScreen
import com.devscion.chapterstage.presentation.components.StageTopBar
import com.devscion.chapterstage.presentation.model.AgentUiModel
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft
import com.devscion.chapterstage.presentation.model.GenerationSnapshot

@Composable
fun GenerationProgressScreen(
    agents: List<AgentUiModel>,
    snapshot: GenerationSnapshot,
    settings: GenerationSettingsDraft,
    chapterTitle: String,
    errorMessage: String?,
    onBack: () -> Unit,
    onViewTrace: () -> Unit,
    onOpenViewer: () -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val agentById = agents.associateBy { it.id }

    StageScreen(modifier = modifier) { layout ->
        StageTopBar(
            title = if (snapshot.isComplete) "Workflow complete" else "Agents collaborating",
            subtitle = chapterTitle.ifBlank { "Ch. 4 - Photosynthesis" },
            onBack = onBack,
            trailing = {
                Text(
                    text = formatElapsed(snapshot.elapsedSeconds),
                    color = if (snapshot.isComplete) MaterialTheme.stageColors.success else MaterialTheme.stageColors.textSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                )
            },
        )
        Spacer(modifier = Modifier.height(spacing.medium))
        if (errorMessage != null) {
            StageInlineError(
                modifier = Modifier.fillMaxWidth(),
                title = "Workflow updates paused",
                message = errorMessage,
                onRetry = onRetry,
                onDismiss = onDismissError,
            )
            Spacer(modifier = Modifier.height(spacing.medium))
        }

        if (layout.isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.large),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = spacing.maxPaneWidth),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                ) {
                    ProgressOverviewCard(agents = agents, snapshot = snapshot)
                    GenerationStageCardSwitcher(
                        agents = agents,
                        snapshot = snapshot,
                        settings = settings,
                        publicUrl = snapshot.publicUrl,
                        onOpenViewer = onOpenViewer,
                        onViewTrace = onViewTrace,
                    )
                }
                TraceFeed(
                    modifier = Modifier.weight(1f),
                    events = snapshot.events,
                    agentById = agentById,
                    isComplete = snapshot.isComplete,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                ProgressOverviewCard(agents = agents, snapshot = snapshot)
                GenerationStageCardSwitcher(
                    agents = agents,
                    snapshot = snapshot,
                    settings = settings,
                    publicUrl = snapshot.publicUrl,
                    onOpenViewer = onOpenViewer,
                    onViewTrace = onViewTrace,
                )
                TraceFeed(
                    events = snapshot.events,
                    agentById = agentById,
                    isComplete = snapshot.isComplete,
                )
            }
        }
    }
}

@Preview
@Composable
private fun GenerationProgressScreenPreview() {
    val content = ChapterStageDemoContent()
    val settings = GenerationSettingsDraft()

    ChapterStageTheme {
        GenerationProgressScreen(
            agents = content.agents,
            snapshot = content.snapshotFor(
                revealedEventCount = 7,
                elapsedSeconds = 12,
                settings = settings,
            ),
            settings = settings,
            chapterTitle = content.sampleChapterTitle,
            onBack = {},
            onViewTrace = {},
            onOpenViewer = {},
            errorMessage = null,
            onRetry = {},
            onDismissError = {},
        )
    }
}
