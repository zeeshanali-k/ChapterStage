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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.SegmentedControl
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageInlineError
import com.devscion.chapterstage.presentation.components.StageScreen
import com.devscion.chapterstage.presentation.components.StageTopBar
import com.devscion.chapterstage.presentation.model.ChapterSourceDraft
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.PickedChapterFile
import com.devscion.chapterstage.presentation.model.SourceMode
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.startAccessingSecurityScopedResource
import io.github.vinceglb.filekit.stopAccessingSecurityScopedResource
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch

@Composable
fun CreateChapterScreen(
    sourceMode: SourceMode,
    draft: ChapterSourceDraft,
    sampleText: String,
    isPickingFile: Boolean,
    errorMessage: String?,
    onSourceModeChange: (SourceMode) -> Unit,
    onDraftChange: (ChapterSourceDraft) -> Unit,
    onPickFile: () -> Unit,
    onFileSelected: (PickedChapterFile) -> Unit,
    onFilePickCancelled: () -> Unit,
    onFilePickFailed: (String) -> Unit,
    onRemoveFile: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val pickerScope = rememberCoroutineScope()
    val canContinue = when (sourceMode) {
        SourceMode.PasteText -> draft.text.trim().length >= 500
        SourceMode.UploadFile -> draft.selectedFileName != null
    }
    val launchFilePicker: () -> Unit = {
        if (!isPickingFile) {
            onPickFile()
            pickerScope.launch {
                runCatching {
                    FileKit.openFilePicker(type = FileKitType.File(SupportedChapterFileExtensions))
                }.onSuccess { platformFile ->
                    if (platformFile == null) {
                        onFilePickCancelled()
                    } else {
                        runCatching {
                            platformFile.toPickedChapterFile()
                        }.onSuccess(onFileSelected)
                            .onFailure { throwable -> onFilePickFailed(throwable.toPickerMessage()) }
                    }
                }.onFailure { throwable ->
                    onFilePickFailed(throwable.toPickerMessage())
                }
            }
        }
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
        if (errorMessage != null) {
            StageInlineError(
                modifier = Modifier.fillMaxWidth(),
                title = "Source content needs attention",
                message = errorMessage,
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
                SourceFields(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = spacing.maxPaneWidth),
                    sourceMode = sourceMode,
                    draft = draft,
                    sampleText = sampleText,
                    isPickingFile = isPickingFile,
                    onDraftChange = onDraftChange,
                    onPickFile = launchFilePicker,
                    onRemoveFile = onRemoveFile,
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
                    isPickingFile = isPickingFile,
                    onDraftChange = onDraftChange,
                    onPickFile = launchFilePicker,
                    onRemoveFile = onRemoveFile,
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
        StageButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Continue",
            onClick = onContinue,
            enabled = canContinue && !isPickingFile,
            large = true,
            trailingText = "->",
            sharedKey = "chapterstage-primary-action",
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
            isPickingFile = false,
            errorMessage = null,
            onSourceModeChange = {},
            onDraftChange = {},
            onPickFile = {},
            onFileSelected = {},
            onFilePickCancelled = {},
            onFilePickFailed = {},
            onRemoveFile = {},
            onBack = {},
            onContinue = {},
            onDismissError = {},
        )
    }
}

private suspend fun PlatformFile.toPickedChapterFile(): PickedChapterFile {
    val accessGranted = startAccessingSecurityScopedResource()
    return try {
        val resolvedExtension = extension.lowercase()
        PickedChapterFile(
            fileName = name.ifBlank { "chapter-source.${resolvedExtension.ifBlank { "txt" }}" },
            bytes = readBytes(),
            contentType = mimeType()?.toString() ?: resolvedExtension.toFallbackContentType(),
        )
    } finally {
        if (accessGranted) {
            stopAccessingSecurityScopedResource()
        }
    }
}

private fun String.toFallbackContentType(): String =
    when (this) {
        "pdf" -> "application/pdf"
        "txt" -> "text/plain"
        else -> "application/octet-stream"
    }

private fun Throwable.toPickerMessage(): String =
    message ?: "We could not read that file. Try another PDF or TXT."

private val SupportedChapterFileExtensions = listOf("pdf", "txt")
