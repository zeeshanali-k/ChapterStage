package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.presentation.model.ViewerLoadState

@Composable
fun ExperienceViewerScreen(
    state: ViewerLoadState,
    publicUrl: String,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.stageColors.backgroundHigh, MaterialTheme.stageColors.background),
                ),
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        ViewerTopBar(
            title = "Photosynthesis",
            subtitle = "Living Systems - Ch.4",
            onBack = onBack,
        )
        BrowserChrome(publicUrl = publicUrl)
        when (state) {
            ViewerLoadState.Loading -> ViewerLoadingState()
            ViewerLoadState.Error -> ViewerErrorState(onRetry = onRetry)
            ViewerLoadState.Loaded -> GeneratedExperiencePreview(publicUrl = publicUrl)
        }
    }
}

@Preview
@Composable
private fun ExperienceViewerScreenPreview() {
    ChapterStageTheme {
        ExperienceViewerScreen(
            state = ViewerLoadState.Loaded,
            publicUrl = "chapterstage.app/c/photosynthesis",
            onBack = {},
            onRetry = {},
        )
    }
}

