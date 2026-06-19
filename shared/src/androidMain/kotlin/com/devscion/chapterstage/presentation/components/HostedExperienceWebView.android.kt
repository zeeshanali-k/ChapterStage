package com.devscion.chapterstage.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

@Composable
actual fun HostedExperienceWebView(
    url: String,
    modifier: Modifier,
) {
    val state = rememberWebViewState(url)

    LaunchedEffect(state) {
        state.webSettings.isJavaScriptEnabled = true
    }

    WebView(
        state = state,
        modifier = modifier,
    )
}
