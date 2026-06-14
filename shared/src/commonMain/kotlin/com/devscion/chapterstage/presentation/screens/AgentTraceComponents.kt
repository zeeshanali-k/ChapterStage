package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.presentation.components.BandRoomBadge
import com.devscion.chapterstage.presentation.components.StageCard
import com.devscion.chapterstage.presentation.components.StageIconBadge
import com.devscion.chapterstage.presentation.components.StageLabel
import com.devscion.chapterstage.presentation.components.TraceEventRow
import com.devscion.chapterstage.presentation.model.AgentUiModel
import com.devscion.chapterstage.presentation.model.GenerationSnapshot
import com.devscion.chapterstage.presentation.model.TraceEventUiModel

@Composable
internal fun TraceRoomCard(
    snapshot: GenerationSnapshot,
    modifier: Modifier = Modifier,
) {
    StageCard(
        modifier = modifier.fillMaxWidth(),
        accent = if (snapshot.isComplete) MaterialTheme.stageColors.success else MaterialTheme.stageColors.primary,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StageIconBadge(
                text = "BR",
                color = if (snapshot.isComplete) MaterialTheme.stageColors.success else MaterialTheme.stageColors.primary,
            )
            Column(modifier = Modifier.weight(1f)) {
                StageLabel(
                    text = if (snapshot.isComplete) "PUBLISHED TRACE" else "LIVE TRACE",
                    dotColor = if (snapshot.isComplete) MaterialTheme.stageColors.success else MaterialTheme.stageColors.primary,
                )
                Text(
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xSmall),
                    text = "Band room PX-4471",
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            text = "Every visible step from delegation through verification is preserved here for inspection.",
            color = MaterialTheme.stageColors.textSecondary,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            TraceStatCell(label = "EVENTS", value = snapshot.events.size.toString(), modifier = Modifier.weight(1f))
            TraceStatCell(label = "ROOM", value = "PX", modifier = Modifier.weight(1f))
            TraceStatCell(label = "STATE", value = if (snapshot.isComplete) "DONE" else "LIVE", modifier = Modifier.weight(1f))
        }
        BandRoomBadge(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            live = !snapshot.isComplete,
        )
    }
}

@Composable
internal fun TraceTimeline(
    events: List<TraceEventUiModel>,
    agentById: Map<String, AgentUiModel>,
    modifier: Modifier = Modifier,
) {
    StageCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(MaterialTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StageLabel(text = "AGENT EVENTS")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${events.size} total",
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            events.forEachIndexed { index, event ->
                val agent = agentById[event.agentId]
                if (agent != null) {
                    TraceEventRow(
                        event = event,
                        agent = agent,
                        dense = index == events.lastIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun TraceStatCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    StageCard(
        modifier = modifier,
        contentPadding = PaddingValues(
            vertical = MaterialTheme.spacing.small,
            horizontal = MaterialTheme.spacing.xSmall,
        ),
    ) {
        Text(
            text = value,
            color = MaterialTheme.stageColors.textPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = MaterialTheme.stageColors.textTertiary,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
        )
    }
}

