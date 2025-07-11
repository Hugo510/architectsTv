package com.example.feature_ui_tv.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenConfig(
    val isSmallScreen: Boolean,
    val isMediumScreen: Boolean,
    val isLargeScreen: Boolean,
    val contentPadding: Dp,
    val cardSpacing: Dp,
    val textScale: Float,
    val imageAspectRatio: Float
)

@Composable
fun rememberScreenConfig(): ScreenConfig {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    return remember(screenWidth) {
        when {
            screenWidth < 720 -> ScreenConfig(
                isSmallScreen = true,
                isMediumScreen = false,
                isLargeScreen = false,
                contentPadding = 8.dp,
                cardSpacing = 8.dp,
                textScale = 0.8f,
                imageAspectRatio = 1.5f
            )
            screenWidth < 1280 -> ScreenConfig(
                isSmallScreen = false,
                isMediumScreen = true,
                isLargeScreen = false,
                contentPadding = 16.dp,
                cardSpacing = 12.dp,
                textScale = 1.0f,
                imageAspectRatio = 1.8f
            )
            else -> ScreenConfig(
                isSmallScreen = false,
                isMediumScreen = false,
                isLargeScreen = true,
                contentPadding = 24.dp,
                cardSpacing = 16.dp,
                textScale = 1.2f,
                imageAspectRatio = 2.0f
            )
        }
    }
}
