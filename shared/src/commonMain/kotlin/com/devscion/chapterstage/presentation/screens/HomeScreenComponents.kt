package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.ChapterStageLogo
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageCard
import com.devscion.chapterstage.presentation.components.StageEmptyState
import com.devscion.chapterstage.presentation.components.StageIconBadge
import com.devscion.chapterstage.presentation.components.StageInlineError
import com.devscion.chapterstage.presentation.components.StageLabel
import com.devscion.chapterstage.presentation.components.StageLoadingNotice
import com.devscion.chapterstage.presentation.components.StatusDot
import com.devscion.chapterstage.presentation.model.AgentStatus
import com.devscion.chapterstage.presentation.model.RecentJobUiModel

@Composable
internal fun NewExperienceCard(
    onCreateChapter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StageCard(
        modifier = modifier.fillMaxWidth(),
        accent = MaterialTheme.stageColors.primary,
        onClick = onCreateChapter,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                StageLabel(text = "NEW EXPERIENCE", dotColor = MaterialTheme.stageColors.primary)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Text(
                    text = "Create a new\nchapter experience",
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                    text = "Turn dense chapters into visual learning in one link.",
                    color = MaterialTheme.stageColors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                StageButton(
                    text = "Start",
                    onClick = onCreateChapter,
                    trailingText = "->",
                )
            }
            ChapterStageLogo(compact = true)
        }
    }
}

@Composable
internal fun HowItWorksSection(
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.stageColors
    val items = listOf(
        HomeStep("UP", "Upload a chapter", "Paste text or drop a PDF / TXT", colors.cyan),
        HomeStep("AG", "Agents collaborate", "Six specialists analyze, build and verify", colors.primary),
        HomeStep("GO", "Open interactive link", "A polished web experience, ready to share", colors.success),
    )

    Column(modifier = modifier.fillMaxWidth()) {
        StageLabel(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium),
            text = "HOW IT WORKS",
        )
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            items.forEachIndexed { index, item ->
                StageCard(contentPadding = PaddingValues(MaterialTheme.spacing.medium)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    ) {
                        StageIconBadge(text = item.iconText, color = item.color)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = MaterialTheme.stageColors.textPrimary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = item.subtitle,
                                color = MaterialTheme.stageColors.textSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Text(
                            text = "0${index + 1}",
                            color = MaterialTheme.stageColors.textTertiary,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun RecentJobsSection(
    recentJobs: List<RecentJobUiModel>,
    isLoading: Boolean,
    errorMessage: String?,
    onOpenRecentJob: (RecentJobUiModel) -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StageLabel(text = "RECENT JOBS")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${recentJobs.size} total",
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            if (errorMessage != null) {
                StageInlineError(
                    title = "Recent jobs did not update",
                    message = errorMessage,
                    onRetry = onRetry,
                    onDismiss = onDismissError,
                )
            }
            when {
                isLoading -> StageLoadingNotice(message = "Refreshing recent chapter jobs...")
                recentJobs.isEmpty() -> StageEmptyState(
                    title = "No chapter jobs yet",
                    message = "Your generated chapter experiences will appear here.",
                )
                else -> recentJobs.forEach { job ->
                    RecentJobCard(
                        job = job,
                        onClick = { onOpenRecentJob(job) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentJobCard(
    job: RecentJobUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ready = job.status == "ready"
    val failed = job.status == "failed"
    val statusColor = when {
        ready -> MaterialTheme.stageColors.success
        failed -> MaterialTheme.stageColors.error
        else -> MaterialTheme.stageColors.warning
    }
    val statusText = when {
        ready -> "READY"
        failed -> "FAILED"
        else -> "${job.progress}%"
    }

    StageCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(MaterialTheme.spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = MaterialTheme.shapes.medium,
                color = Color.White.copy(alpha = 0.04f),
                border = BorderStroke(1.dp, MaterialTheme.stageColors.line),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "BK",
                        color = MaterialTheme.stageColors.textSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.title,
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = job.book,
                    color = MaterialTheme.stageColors.textTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            statusColor.copy(alpha = 0.12f),
                        )
                        .padding(
                            horizontal = MaterialTheme.spacing.small,
                            vertical = MaterialTheme.spacing.xSmall,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    StatusDot(
                        status = if (ready) AgentStatus.Completed else AgentStatus.Active,
                        color = statusColor,
                        size = 6.dp,
                    )
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                    )
                }
                Text(
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xSmall),
                    text = job.currentStep ?: job.updatedAt,
                    color = MaterialTheme.stageColors.textTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
internal fun HomeProfileBadge(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(34.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, MaterialTheme.stageColors.line),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "AK",
                color = MaterialTheme.stageColors.textSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private data class HomeStep(
    val iconText: String,
    val title: String,
    val subtitle: String,
    val color: Color,
)
