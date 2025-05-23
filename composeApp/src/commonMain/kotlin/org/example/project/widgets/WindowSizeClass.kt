package org.example.project.widgets

import androidx.compose.runtime.Composable

enum class WindowSizeClass {
    Compact, Medium, Expanded
}

@Composable
expect fun getWindowSizeClass(widthPx: Float? = null): WindowSizeClass