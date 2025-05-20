package org.example.project.platform_specific

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
actual fun getWindowSizeClass(widthPx: Float?): WindowSizeClass {
    requireNotNull(widthPx) {
        "Desktop implementation of getWindowSizeClass requires widthPx argument"
    }
    val windowWidthDp = with(LocalDensity.current) { widthPx.toDp() }

    return when {
        windowWidthDp < 600.dp -> WindowSizeClass.Compact
        windowWidthDp < 840.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}