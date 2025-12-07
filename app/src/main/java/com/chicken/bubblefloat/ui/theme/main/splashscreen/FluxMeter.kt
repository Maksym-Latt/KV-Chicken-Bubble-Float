package com.chicken.bubblefloat.ui.main.splashscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun FluxMeter(
    value: Float,
    modifier: Modifier = Modifier,
    barThickness: Int = 22
) {
    val normalized = value.coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .height(barThickness.dp)
            .fillMaxWidth()
    ) {
        val radius = size.height / 2f

        drawRoundRect(
            color = Color(0xFF595B60),
            cornerRadius = CornerRadius(radius, radius)
        )

        if (normalized > 0f) {
            val fillWidth = max(size.height, size.width * normalized)

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFB74D), Color(0xFFFF8F00))
                ),
                size = Size(fillWidth, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height * 0.55f
                ),
                size = Size(fillWidth, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}