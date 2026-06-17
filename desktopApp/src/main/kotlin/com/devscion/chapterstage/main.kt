package com.devscion.chapterstage

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit

fun main() {
    FileKit.init(appId = "com.devscion.chapterstage")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Chapter Stage",
        ) {
            App()
        }
    }
}
