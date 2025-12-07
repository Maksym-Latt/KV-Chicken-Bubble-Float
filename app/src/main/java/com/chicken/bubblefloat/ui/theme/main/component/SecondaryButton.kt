package com.chicken.bubblefloat.ui.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun RetroNavButton(
    onPress: () -> Unit,
    modifier: Modifier = Modifier
) = RetroIconButton(
    onPress = onPress,
    modifier = modifier
) {
    Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(0.8f)
    )
}

@Composable
fun RetroIconButton(
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    padding: PaddingValues = PaddingValues(8.dp),
    icon: @Composable () -> Unit
) {
    val interact = remember { MutableInteractionSource() }
    val pressed by interact.collectIsPressedAsState()

    val diameter = iconSize +
            padding.calculateLeftPadding(LayoutDirection.Ltr) +
            padding.calculateRightPadding(LayoutDirection.Ltr) +
            16.dp

    val fgColor = if (!pressed) Color(0xfffdfdfd) else Color(0xFF5A3417)
    val fgAlpha = if (!pressed) 1f else 0.8f

    Box(
        modifier = modifier
            .size(diameter)
            .clipToBounds()
            .clickable(
                interactionSource = interact,
                indication = null,
                onClick = onPress
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOrbControl(size, pressed)
        }

        CompositionLocalProvider(LocalContentColor provides fgColor) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer { alpha = fgAlpha },
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
    }
}

private fun DrawScope.drawOrbControl(
    orbSize: Size,
    pressed: Boolean
) {
    if (orbSize.width <= 0f || orbSize.height <= 0f) return

    val w = orbSize.width
    val h = orbSize.height
    val side = min(w, h)
    val unit = side / 100f

    val cx = w / 2f
    val cy = h / 2f

    val ringRadius = 40f * unit
    val capRadius = 34f * unit
    val drop = 6f * unit

    val shadow = Color(0x66000000)

    val lowerShell = Brush.verticalGradient(
        listOf(
            Color(0xFF0D8C91),
            Color(0xFF05575B)
        )
    )

    val rimColor = Color(0xFF003034)

    val shellIdle = Brush.verticalGradient(
        listOf(
            Color(0xFF6DFAFF),
            Color(0xFF0D8C91)
        )
    )

    val shellPressed = Brush.verticalGradient(
        listOf(
            Color(0xFF4DD8DD),
            Color(0xFF05575B)
        )
    )

    val topBrush = if (pressed) shellPressed else shellIdle

    drawOval(
        color = shadow,
        topLeft = Offset(
            x = cx - ringRadius * 0.9f,
            y = cy + ringRadius * 0.35f
        ),
        size = Size(
            width = ringRadius * 1.8f,
            height = ringRadius * 0.6f
        )
    )

    val bottomCenter = Offset(cx, cy + drop)

    drawCircle(
        brush = lowerShell,
        radius = ringRadius,
        center = bottomCenter
    )

    drawCircle(
        color = rimColor,
        radius = ringRadius,
        center = bottomCenter,
        style = Stroke(width = 3.5f * unit)
    )

    val pressOffset = if (pressed) 2f * unit else 0f
    val topCenter = Offset(cx, cy + pressOffset)

    drawCircle(
        color = rimColor,
        radius = ringRadius,
        center = topCenter
    )
    drawCircle(
        brush = topBrush,
        radius = capRadius,
        center = topCenter
    )
    drawCircle(
        color = Color(0xFFB6FFFF),
        radius = capRadius,
        center = topCenter,
        style = Stroke(width = 3.5f * unit)
    )
    val highlightRadius = capRadius * 0.7f
    val highlightCenter = Offset(
        topCenter.x,
        topCenter.y - capRadius * 0.45f
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x66FFFFFF),
                Color(0x00FFFFFF)
            ),
            center = highlightCenter,
            radius = highlightRadius
        ),
        radius = highlightRadius,
        center = highlightCenter
    )
}