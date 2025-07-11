package com.example.feature_ui_tv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box           // ← IMPORT CORRECTO
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun PageIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp? = null,
    spacing: Dp? = null,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
) {
    val screenConfig = rememberScreenConfig()
    val responsiveDotSize = dotSize ?: if (screenConfig.isSmallScreen) 6.dp else 8.dp
    val responsiveSpacing = spacing ?: if (screenConfig.isSmallScreen) 6.dp else 8.dp
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(responsiveSpacing)
    ) {
        for (i in 0 until totalPages) {
            val color = if (i == currentPage) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .size(responsiveDotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
