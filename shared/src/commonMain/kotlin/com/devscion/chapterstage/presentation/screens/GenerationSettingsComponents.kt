package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.PillGroup
import com.devscion.chapterstage.presentation.components.SegmentedControl
import com.devscion.chapterstage.presentation.components.StageCard
import com.devscion.chapterstage.presentation.components.StageIconBadge
import com.devscion.chapterstage.presentation.components.StageLabel
import com.devscion.chapterstage.presentation.components.StageSwitch
import com.devscion.chapterstage.presentation.model.AudienceLevelOption
import com.devscion.chapterstage.presentation.model.ExperienceStyleOption
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft

@Composable
internal fun SettingsPreviewCard(
    settings: GenerationSettingsDraft,
    modifier: Modifier = Modifier,
) {
    val style = settings.experienceStyle

    StageCard(
        modifier = modifier.fillMaxWidth(),
        accent = MaterialTheme.stageColors.primary,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StageIconBadge(text = style.shortLabel.take(2).uppercase(), color = MaterialTheme.stageColors.primary)
            Column(modifier = Modifier.weight(1f)) {
                StageLabel(text = "PREVIEW", dotColor = MaterialTheme.stageColors.primary)
                Text(
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xSmall),
                    text = style.label,
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            text = style.description,
            color = MaterialTheme.stageColors.textSecondary,
            style = MaterialTheme.typography.bodyLarge,
        )
        SceneCountPreview(settings = settings)
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.small),
            text = "${settings.targetScreenCount} SCENES - ${settings.audienceLevel.label.uppercase()}" +
                if (settings.autoBrainstorm) " - AUTO-BRAINSTORM" else "",
            color = MaterialTheme.stageColors.textTertiary,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
internal fun SettingsControls(
    settings: GenerationSettingsDraft,
    onSettingsChange: (GenerationSettingsDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
    ) {
        SettingBlock(label = "AUDIENCE LEVEL") {
            PillGroup(
                options = AudienceLevelOption.entries.map { it.label },
                selected = settings.audienceLevel.label,
                onSelected = { selected ->
                    onSettingsChange(
                        settings.copy(audienceLevel = AudienceLevelOption.entries.first { it.label == selected }),
                    )
                },
            )
        }

        SettingBlock(label = "EXPERIENCE STYLE") {
            PillGroup(
                options = ExperienceStyleOption.entries.map { it.label },
                selected = settings.experienceStyle.label,
                onSelected = { selected ->
                    onSettingsChange(
                        settings.copy(experienceStyle = ExperienceStyleOption.entries.first { it.label == selected }),
                    )
                },
            )
        }

        SettingBlock(label = "SCREEN COUNT") {
            SegmentedControl(
                options = listOf("6", "8", "10"),
                selected = settings.targetScreenCount.toString(),
                onSelected = { selected -> onSettingsChange(settings.copy(targetScreenCount = selected.toInt())) },
            )
        }

        AutoBrainstormCard(
            checked = settings.autoBrainstorm,
            onCheckedChange = { onSettingsChange(settings.copy(autoBrainstorm = it)) },
        )
    }
}

@Composable
private fun SceneCountPreview(
    settings: GenerationSettingsDraft,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        repeat(settings.targetScreenCount) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .padding(vertical = MaterialTheme.spacing.xxSmall),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraSmall,
                    color = if (index == 0) {
                        MaterialTheme.stageColors.primary.copy(alpha = 0.36f)
                    } else {
                        Color.White.copy(alpha = 0.06f)
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (index == 0) {
                            MaterialTheme.stageColors.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.stageColors.line
                        },
                    ),
                    content = {},
                )
            }
        }
    }
}

@Composable
private fun AutoBrainstormCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    StageCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(MaterialTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            StageIconBadge(text = "AI", color = MaterialTheme.stageColors.warning)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Auto-Brainstorm",
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Let agents test 5 formats and pick the best.",
                    color = MaterialTheme.stageColors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            StageSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun SettingBlock(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StageLabel(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium),
            text = label,
        )
        content()
    }
}
