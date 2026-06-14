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
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageScreen
import com.devscion.chapterstage.presentation.components.StageTopBar
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft

@Composable
fun GenerationSettingsScreen(
    settings: GenerationSettingsDraft,
    onSettingsChange: (GenerationSettingsDraft) -> Unit,
    onBack: () -> Unit,
    onStartWorkflow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    StageScreen(modifier = modifier) { layout ->
        StageTopBar(
            title = "Generation settings",
            subtitle = "Step 2 of 2 - Shape the experience",
            onBack = onBack,
        )
        Spacer(modifier = Modifier.height(spacing.medium))

        if (layout.isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.large),
                verticalAlignment = Alignment.Top,
            ) {
                SettingsPreviewCard(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = spacing.maxPaneWidth),
                    settings = settings,
                )
                SettingsControls(
                    modifier = Modifier.weight(1f),
                    settings = settings,
                    onSettingsChange = onSettingsChange,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.large)) {
                SettingsPreviewCard(settings = settings)
                SettingsControls(
                    settings = settings,
                    onSettingsChange = onSettingsChange,
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
        StageButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Start Agent Workflow",
            onClick = onStartWorkflow,
            large = true,
            leadingText = "AG",
        )
    }
}

@Preview
@Composable
private fun GenerationSettingsScreenPreview() {
    ChapterStageTheme {
        GenerationSettingsScreen(
            settings = GenerationSettingsDraft(),
            onSettingsChange = {},
            onBack = {},
            onStartWorkflow = {},
        )
    }
}

