package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.FieldLabel
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageButtonVariant
import com.devscion.chapterstage.presentation.components.StageCard
import com.devscion.chapterstage.presentation.components.StageIconBadge
import com.devscion.chapterstage.presentation.components.StageLabel
import com.devscion.chapterstage.presentation.components.StageTextField
import com.devscion.chapterstage.presentation.model.ChapterSourceDraft
import com.devscion.chapterstage.presentation.model.SourceMode

@Composable
internal fun SourceFields(
    sourceMode: SourceMode,
    draft: ChapterSourceDraft,
    sampleText: String,
    isPickingFile: Boolean,
    onDraftChange: (ChapterSourceDraft) -> Unit,
    onPickFile: () -> Unit,
    onRemoveFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            FieldLabel(label = "Book title", optional = true)
            StageTextField(
                value = draft.bookTitle,
                onValueChange = { onDraftChange(draft.copy(bookTitle = it)) },
                placeholder = "Living Systems",
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            FieldLabel(label = "Chapter title", optional = true)
            StageTextField(
                value = draft.chapterTitle,
                onValueChange = { onDraftChange(draft.copy(chapterTitle = it)) },
                placeholder = "Ch. 4 - Photosynthesis",
            )
        }

        when (sourceMode) {
            SourceMode.PasteText -> PasteTextSection(
                draft = draft,
                sampleText = sampleText,
                onDraftChange = onDraftChange,
            )
            SourceMode.UploadFile -> UploadFileSection(
                draft = draft,
                isPickingFile = isPickingFile,
                onPickFile = onPickFile,
                onRemoveFile = onRemoveFile,
            )
        }
    }
}

@Composable
private fun PasteTextSection(
    draft: ChapterSourceDraft,
    sampleText: String,
    onDraftChange: (ChapterSourceDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        FieldLabel(label = "Chapter text")
        Box {
            StageTextField(
                value = draft.text,
                onValueChange = { onDraftChange(draft.copy(text = it)) },
                placeholder = "Paste your chapter here...",
                singleLine = false,
                minLines = 9,
                maxLines = 12,
            )
            if (draft.text.isBlank()) {
                UseSampleChip(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(MaterialTheme.spacing.medium),
                    onClick = {
                        onDraftChange(
                            draft.copy(
                                bookTitle = draft.bookTitle.ifBlank { "Living Systems" },
                                chapterTitle = draft.chapterTitle.ifBlank { "Ch. 4 - Photosynthesis" },
                                text = sampleText,
                            ),
                        )
                    },
                )
            }
        }
        Text(
            text = if (draft.text.isBlank()) {
                "Paste at least a few paragraphs for the best results."
            } else {
                "${draft.text.trim().split(Regex("\\s+")).size} words - ${draft.text.length}/500 chars"
            },
            color = MaterialTheme.stageColors.textTertiary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun UploadFileSection(
    draft: ChapterSourceDraft,
    isPickingFile: Boolean,
    onPickFile: () -> Unit,
    onRemoveFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        FieldLabel(label = "Upload file")
        if (draft.selectedFileName == null) {
            UploadDropZone(
                isPickingFile = isPickingFile,
                onClick = onPickFile,
            )
        } else {
            SelectedFileCard(
                fileName = draft.selectedFileName,
                fileSizeLabel = draft.selectedFileSizeLabel,
                fileExtension = draft.selectedFileExtension,
                onRemove = onRemoveFile,
            )
        }
        Text(
            text = "Accepted: PDF or TXT, up to 20 MB. No login or personal data needed.",
            color = MaterialTheme.stageColors.textTertiary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
internal fun CreateGuidanceCard(
    sourceMode: SourceMode,
    draft: ChapterSourceDraft,
    canContinue: Boolean,
    modifier: Modifier = Modifier,
) {
    StageCard(modifier = modifier, accent = MaterialTheme.stageColors.cyan) {
        StageLabel(text = "SOURCE CHECK", dotColor = if (canContinue) MaterialTheme.stageColors.success else null)
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            text = if (sourceMode == SourceMode.PasteText) "Chapter text" else "Chapter upload",
            color = MaterialTheme.stageColors.textPrimary,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.small),
            text = when {
                sourceMode == SourceMode.PasteText && draft.text.isBlank() ->
                    "Paste source content or use the sample to shape the experience."
                sourceMode == SourceMode.PasteText ->
                    "The source is long enough for a realistic agent workflow preview."
                draft.selectedFileName == null ->
                    "Tap the upload well to choose a supported chapter file."
                else ->
                    "The selected file is ready for the settings step."
            },
            color = MaterialTheme.stageColors.textSecondary,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun UseSampleChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.stageColors.primary.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, MaterialTheme.stageColors.primary.copy(alpha = 0.3f)),
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.small,
                vertical = MaterialTheme.spacing.small,
            ),
            text = "Use sample",
            color = MaterialTheme.stageColors.primaryText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun UploadDropZone(
    isPickingFile: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.stageColors.surface)
            .border(BorderStroke(1.dp, MaterialTheme.stageColors.lineHigh), MaterialTheme.shapes.large)
            .clickable(enabled = !isPickingFile, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            StageIconBadge(text = "UP", color = MaterialTheme.stageColors.primary)
            Text(
                text = if (isPickingFile) "Opening file picker..." else "Drop a file or tap to browse",
                color = MaterialTheme.stageColors.textPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "We'll extract the text for you",
                color = MaterialTheme.stageColors.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                FileTypeChip(text = "PDF")
                FileTypeChip(text = "TXT")
            }
        }
    }
}

@Composable
private fun SelectedFileCard(
    fileName: String,
    fileSizeLabel: String?,
    fileExtension: String?,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extensionLabel = fileExtension
        ?: fileName.substringAfterLast('.', missingDelimiterValue = "FILE").uppercase()
    val detailText = fileSizeLabel?.let { "$it - selected" } ?: "Ready to upload"

    StageCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(MaterialTheme.spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            StageIconBadge(
                text = extensionLabel,
                color = if (extensionLabel == "PDF") MaterialTheme.stageColors.error else MaterialTheme.stageColors.cyan,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileName,
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = detailText,
                    color = MaterialTheme.stageColors.textTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
            StageButton(
                text = "Remove",
                onClick = onRemove,
                variant = StageButtonVariant.Ghost,
            )
        }
    }
}

@Composable
private fun FileTypeChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.stageColors.surfaceHigh,
        border = BorderStroke(1.dp, MaterialTheme.stageColors.line),
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.small,
                vertical = MaterialTheme.spacing.xSmall,
            ),
            text = text,
            color = MaterialTheme.stageColors.textSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
        )
    }
}
