package com.devscion.chapterstage.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.AgentAvatar
import com.devscion.chapterstage.presentation.components.AgentStrip
import com.devscion.chapterstage.presentation.components.BandRoomBadge
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageButtonVariant
import com.devscion.chapterstage.presentation.components.StageCard
import com.devscion.chapterstage.presentation.components.StageIconBadge
import com.devscion.chapterstage.presentation.components.StageLabel
import com.devscion.chapterstage.presentation.components.StageProgressBar
import com.devscion.chapterstage.presentation.components.TraceEventRow
import com.devscion.chapterstage.presentation.model.AgentStatus
import com.devscion.chapterstage.presentation.model.AgentUiModel
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft
import com.devscion.chapterstage.presentation.model.GenerationSnapshot
import com.devscion.chapterstage.presentation.model.TraceEventUiModel

@Composable
internal fun ProgressOverviewCard(
    agents: List<AgentUiModel>,
    snapshot: GenerationSnapshot,
    modifier: Modifier = Modifier,
) {
    val progressColor = if (snapshot.isComplete) {
        MaterialTheme.stageColors.success
    } else {
        MaterialTheme.stageColors.primary
    }

    StageCard(
        modifier = modifier.fillMaxWidth(),
        sharedKey = "chapterstage-primary-panel",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BandRoomBadge(live = !snapshot.isComplete)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${snapshot.progress}%",
                color = progressColor,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
        }
        StageProgressBar(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            progress = snapshot.progress,
            color = progressColor,
        )
        AgentStrip(
            modifier = Modifier.padding(top = MaterialTheme.spacing.large),
            agents = agents,
            statuses = snapshot.statuses,
            activeAgentId = snapshot.activeAgentId,
        )
    }
}

@Composable
internal fun GenerationStageCardSwitcher(
    agents: List<AgentUiModel>,
    snapshot: GenerationSnapshot,
    settings: GenerationSettingsDraft,
    publicUrl: String?,
    onOpenViewer: () -> Unit,
    onViewTrace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = snapshot.isComplete,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = {
            ((
                fadeIn(animationSpec = tween(durationMillis = 180)) +
                    slideInVertically(animationSpec = tween(durationMillis = 240)) { it / 7 }
                ) togetherWith fadeOut(animationSpec = tween(durationMillis = 140))).using(SizeTransform(clip = false))
        },
        label = "GenerationStageCardSwitcher",
    ) { isComplete ->
        if (isComplete) {
            CompletedExperienceCard(
                settings = settings,
                publicUrl = publicUrl,
                onOpenViewer = onOpenViewer,
                onViewTrace = onViewTrace,
            )
        } else {
            ActiveAgentCard(
                agents = agents,
                snapshot = snapshot,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ActiveAgentCard(
    agents: List<AgentUiModel>,
    snapshot: GenerationSnapshot,
    modifier: Modifier = Modifier,
) {
    val activeAgent = agents.firstOrNull { it.id == snapshot.activeAgentId } ?: return

    SharedTransitionLayout(modifier = modifier.fillMaxWidth()) {
        AnimatedContent(
            targetState = activeAgent.id,
            transitionSpec = {
                ((
                    fadeIn(animationSpec = tween(durationMillis = 180)) +
                        slideInHorizontally(animationSpec = tween(durationMillis = 240)) { it / 8 }
                    ) togetherWith (
                    fadeOut(animationSpec = tween(durationMillis = 120)) +
                        slideOutHorizontally(animationSpec = tween(durationMillis = 180)) { -it / 12 }
                    )).using(SizeTransform(clip = false))
            },
            label = "ActiveAgentHandoff",
        ) { activeAgentId ->
            val animatedAgent = agents.firstOrNull { it.id == activeAgentId } ?: activeAgent
            val latest = snapshot.events.lastOrNull { it.agentId == animatedAgent.id }

            StageCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "active-agent-card"),
                        animatedVisibilityScope = this@AnimatedContent,
                    )
                    .clip(MaterialTheme.shapes.extraLarge),
                accent = animatedAgent.color,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    verticalAlignment = Alignment.Top,
                ) {
                    AgentAvatar(
                        agent = animatedAgent,
                        status = AgentStatus.Active,
                        active = true,
                        avatarSize = 48.dp,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        ) {
                            Text(
                                text = animatedAgent.name,
                                color = MaterialTheme.stageColors.textPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "WORKING",
                                color = animatedAgent.color,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        Text(
                            modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                            text = if (latest != null) "${latest.title} - ${latest.message}" else animatedAgent.role,
                            color = MaterialTheme.stageColors.textSecondary,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CompletedExperienceCard(
    settings: GenerationSettingsDraft,
    publicUrl: String?,
    onOpenViewer: () -> Unit,
    onViewTrace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StageCard(
        modifier = modifier.fillMaxWidth(),
        accent = MaterialTheme.stageColors.success,
        sharedKey = "chapterstage-result-panel",
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StageIconBadge(text = "OK", color = MaterialTheme.stageColors.success)
            Column {
                StageLabel(
                    text = "COMPLETE",
                    color = MaterialTheme.stageColors.success,
                    dotColor = MaterialTheme.stageColors.success,
                )
                Text(
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xSmall),
                    text = "Your chapter is ready",
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        PublishedUrlCard(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            publicUrl = publicUrl,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            MetricCell(label = "SCENES", value = settings.targetScreenCount.toString(), modifier = Modifier.weight(1f))
            MetricCell(label = "FAITHFUL", value = "0.96", modifier = Modifier.weight(1f))
            MetricCell(label = "SAFETY", value = "PASS", modifier = Modifier.weight(1f))
        }

        StageButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.large),
            text = "Open Interactive Chapter",
            onClick = onOpenViewer,
            large = true,
            trailingText = "->",
            sharedKey = "chapterstage-primary-action",
        )
        StageButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.small),
            text = "View Agent Trace",
            onClick = onViewTrace,
            variant = StageButtonVariant.Ghost,
            leadingText = "AG",
        )
    }
}

@Composable
internal fun TraceFeed(
    events: List<TraceEventUiModel>,
    agentById: Map<String, AgentUiModel>,
    isComplete: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StageLabel(
                text = "LIVE TRACE",
                dotColor = if (isComplete) MaterialTheme.stageColors.success else MaterialTheme.stageColors.primary,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${events.size} events",
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
            )
        }

        if (events.isEmpty()) {
            Text(
                text = "Waiting for the first hand-off...",
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                events.asReversed().forEachIndexed { index, event ->
                    val agent = agentById[event.agentId]
                    if (agent != null) {
                        key(event.id) {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
                                    slideInVertically(animationSpec = tween(durationMillis = 220)) { it / 4 },
                            ) {
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
        }
    }
}

@Composable
private fun PublishedUrlCard(
    publicUrl: String?,
    modifier: Modifier = Modifier,
) {
    StageCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(MaterialTheme.spacing.medium),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = publicUrl ?: "chapterstage.app/c/photosynthesis",
                color = MaterialTheme.stageColors.textSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "LIVE",
                color = MaterialTheme.stageColors.success,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun MetricCell(
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
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            color = MaterialTheme.stageColors.textTertiary,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

internal fun formatElapsed(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return minutes.toString().padStart(2, '0') + ":" + remainingSeconds.toString().padStart(2, '0')
}
