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
import com.devscion.chapterstage.presentation.components.SegmentedControl
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageScreen
import com.devscion.chapterstage.presentation.components.StageTopBar
import com.devscion.chapterstage.presentation.model.ChapterSourceDraft
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.SourceMode

@Composable
fun CreateChapterScreen(
    sourceMode: SourceMode,
    draft: ChapterSourceDraft,
    sampleText: String,
    onSourceModeChange: (SourceMode) -> Unit,
    onDraftChange: (ChapterSourceDraft) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val canContinue = when (sourceMode) {
        SourceMode.PasteText -> draft.text.trim().length >= 500
        SourceMode.UploadFile -> draft.selectedFileName != null
    }

    StageScreen(modifier = modifier) { layout ->
        StageTopBar(
            title = "Create chapter",
            subtitle = "Step 1 of 2 - Source content",
            onBack = onBack,
        )
        Spacer(modifier = Modifier.height(spacing.medium))
        SegmentedControl(
            options = SourceMode.entries.map { it.label },
            selected = sourceMode.label,
            onSelected = { selected ->
                onSourceModeChange(SourceMode.entries.first { it.label == selected })
            },
        )
        Spacer(modifier = Modifier.height(spacing.large))

        if (layout.isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.large),
                verticalAlignment = Alignment.Top,
            ) {
                SourceFields(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = spacing.maxPaneWidth),
                    sourceMode = sourceMode,
                    draft = draft,
                    sampleText = sampleText,
                    onDraftChange = onDraftChange,
                )
                CreateGuidanceCard(
                    modifier = Modifier.weight(1f),
                    sourceMode = sourceMode,
                    draft = draft,
                    canContinue = canContinue,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                SourceFields(
                    sourceMode = sourceMode,
                    draft = draft,
                    sampleText = sampleText,
                    onDraftChange = onDraftChange,
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
        StageButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Continue",
            onClick = onContinue,
            enabled = canContinue,
            large = true,
            trailingText = "->",
        )
    }
}

@Preview
@Composable
private fun CreateChapterScreenPreview() {
    val content = ChapterStageDemoContent()

    ChapterStageTheme {
        CreateChapterScreen(
            sourceMode = SourceMode.PasteText,
            draft = ChapterSourceDraft(),
            sampleText = content.sampleText,
            onSourceModeChange = {},
            onDraftChange = {},
            onBack = {},
            onContinue = {},
        )
    }
}

