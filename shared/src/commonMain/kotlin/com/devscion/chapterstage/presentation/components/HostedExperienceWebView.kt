package com.devscion.chapterstage.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Loads a hosted ChapterStage experience in an embedded WebView when the platform
 * supports it. Platforms without an embedded WebView dependency show a fallback
 * placeholder and rely on the external browser action in the viewer screen.
 *
 * @param url The public URL returned by the backend (e.g. the generated mini-site).
 * @param modifier Modifier applied to the platform WebView or placeholder container.
 */
@Composable
expect fun HostedExperienceWebView(
    url: String,
    modifier: Modifier = Modifier,
)
