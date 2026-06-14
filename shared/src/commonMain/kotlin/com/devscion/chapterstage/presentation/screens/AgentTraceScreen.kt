package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.StageScreen
import com.devscion.chapterstage.presentation.components.StageTopBar
import com.devscion.chapterstage.presentation.model.AgentUiModel
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft
import com.devscion.chapterstage.presentation.model.GenerationSnapshot

@Composable
fun AgentTraceScreen(
    agents: List<AgentUiModel>,
    snapshot: GenerationSnapshot,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val agentById = agents.associateBy { it.id }

    StageScreen(modifier = modifier) { layout ->
        StageTopBar(
            title = "Agent trace",
            subtitle = "Band collaboration timeline",
            onBack = onBack,
        )
        Spacer(modifier = Modifier.height(spacing.medium))

        if (layout.isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.large),
                verticalAlignment = Alignment.Top,
            ) {
                TraceRoomCard(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = spacing.maxPaneWidth),
                    snapshot = snapshot,
                )
                TraceTimeline(
                    modifier = Modifier.weight(1f),
                    events = snapshot.events,
                    agentById = agentById,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.large)) {
                TraceRoomCard(snapshot = snapshot)
                TraceTimeline(
                    events = snapshot.events,
                    agentById = agentById,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AgentTraceScreenPreview() {
    val content = ChapterStageDemoContent()
    val settings = GenerationSettingsDraft()

    ChapterStageTheme {
        AgentTraceScreen(
            agents = content.agents,
            snapshot = content.completedSnapshot(settings),
            onBack = {},
        )
    }
}

