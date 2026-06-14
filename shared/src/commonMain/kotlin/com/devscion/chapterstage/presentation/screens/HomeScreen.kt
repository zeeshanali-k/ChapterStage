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
import androidx.compose.ui.tooling.preview.Preview
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.StageScreen
import com.devscion.chapterstage.presentation.components.StageTopBar
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.RecentJobUiModel

@Composable
fun HomeScreen(
    recentJobs: List<RecentJobUiModel>,
    onCreateChapter: () -> Unit,
    onOpenRecentJob: (RecentJobUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    StageScreen(modifier = modifier) { layout ->
        StageTopBar(
            showLogo = true,
            trailing = { HomeProfileBadge() },
        )

        Spacer(modifier = Modifier.height(spacing.large))
        Text(
            text = "Good evening, Aria",
            color = MaterialTheme.stageColors.textSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "Let's stage a chapter.",
            color = MaterialTheme.stageColors.textPrimary,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(spacing.large))

        if (layout.isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.large),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.large),
                ) {
                    NewExperienceCard(onCreateChapter = onCreateChapter)
                    HowItWorksSection()
                }
                RecentJobsSection(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = spacing.maxPaneWidth),
                    recentJobs = recentJobs,
                    onOpenRecentJob = onOpenRecentJob,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.large)) {
                NewExperienceCard(onCreateChapter = onCreateChapter)
                HowItWorksSection()
                RecentJobsSection(
                    recentJobs = recentJobs,
                    onOpenRecentJob = onOpenRecentJob,
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val content = ChapterStageDemoContent()

    ChapterStageTheme {
        HomeScreen(
            recentJobs = content.recentJobs,
            onCreateChapter = {},
            onOpenRecentJob = {},
        )
    }
}
