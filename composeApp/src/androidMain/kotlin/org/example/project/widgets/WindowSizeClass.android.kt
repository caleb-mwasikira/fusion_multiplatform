package org.example.project.widgets

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
actual fun getWindowSizeClass(widthPx: Float?): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    return when {
        screenWidth < 600 -> WindowSizeClass.Compact
        screenWidth < 840 -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}