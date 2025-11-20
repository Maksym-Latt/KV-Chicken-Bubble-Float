package com.chicken.bubblefloat.ui.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun SecondaryBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = SecondaryIconButton(
    onClick = onClick,
    modifier = modifier
) {
    Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(0.8f)
    )
}

@Composable
fun SecondaryIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    icon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val buttonDiameter = iconSize +
            contentPadding.calculateLeftPadding(LayoutDirection.Ltr) +
            contentPadding.calculateRightPadding(LayoutDirection.Ltr) +
            16.dp

    val iconColor = if (!isPressed) Color(0xfffdfdfd) else Color(0xFF5A3417)
    val iconAlpha = if (!isPressed) 1f else 0.8f

    Box(
        modifier = modifier
            .size(buttonDiameter)
            .clipToBounds()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            draw3DGoldButton(size, isPressed)
        }

        CompositionLocalProvider(LocalContentColor provides iconColor) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer { alpha = iconAlpha },
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
    }
}
// ======================= 🎨 ФОН КРУГЛОЙ КНОПКИ =======================
private fun DrawScope.draw3DGoldButton(
    canvasSize: Size,
    isPressed: Boolean
) {
    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

    val w = canvasSize.width
    val h = canvasSize.height
    val minSide = min(w, h)
    val scale = minSide / 100f

    val cx = w / 2f
    val cy = h / 2f

    // ---------- размеры слоёв ----------
    val outerRadius = 40f * scale
    val topRadius = 34f * scale
    val bottomOffset = 6f * scale

    // Тень под кнопкой
    val shadowColor = Color(0x66000000)

    // 🔵 НИЖНИЙ ГРАДИЕНТ (как на картинке): 0D8C91 → 05575B
    val bottomBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF0D8C91),
            Color(0xFF05575B)
        )
    )

    val borderColor = Color(0xFF003034) // тёмный контур под голубой стиль

    // 🔵 ВЕРХНИЙ ГРАДИЕНТ не нажатый: 6DFAFF → 0D8C91
    val topIdle = Brush.verticalGradient(
        listOf(
            Color(0xFF6DFAFF),
            Color(0xFF0D8C91)
        )
    )

    // 🔵 ВЕРХНИЙ ГРАДИЕНТ нажатый: (делаем темнее)
    val topPressed = Brush.verticalGradient(
        listOf(
            Color(0xFF4DD8DD),
            Color(0xFF05575B)
        )
    )

    val topBrush = if (isPressed) topPressed else topIdle

    // =================== 1) ТЕНЬ ===================
    drawOval(
        color = shadowColor,
        topLeft = Offset(
            x = cx - outerRadius * 0.9f,
            y = cy + outerRadius * 0.35f
        ),
        size = Size(
            width = outerRadius * 1.8f,
            height = outerRadius * 0.6f
        )
    )

    // =================== 2) НИЖНИЙ ДИСК ===================
    val bottomCenter = Offset(cx, cy + bottomOffset)

    drawCircle(
        brush = bottomBrush,
        radius = outerRadius,
        center = bottomCenter
    )

    drawCircle(
        color = borderColor,
        radius = outerRadius,
        center = bottomCenter,
        style = Stroke(width = 3.5f * scale)
    )

    // =================== 3) ВЕРХНИЙ ДИСК ===================
    val pressOffset = if (isPressed) 2f * scale else 0f
    val topCenter = Offset(cx, cy + pressOffset)

    // внешний тёмный контур
    drawCircle(
        color = borderColor,
        radius = outerRadius,
        center = topCenter
    )

    // верхний голубой диск
    drawCircle(
        brush = topBrush,
        radius = topRadius,
        center = topCenter
    )

    // светлый ободок
    drawCircle(
        color = Color(0xFFB6FFFF),
        radius = topRadius,
        center = topCenter,
        style = Stroke(width = 3.5f * scale)
    )

    // =================== 4) БЛИК ===================
    val highlightRadius = topRadius * 0.7f
    val highlightCenter = Offset(
        topCenter.x,
        topCenter.y - topRadius * 0.45f
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



@Preview(
    name = "Secondary Icon Button",
    showBackground = true,
    backgroundColor = 0xFF4BB7F5  // голубой фон как в игре
)
@Composable
private fun SecondaryIconButtonPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        SecondaryIconButton(
            onClick = {},
            iconSize = 40.dp
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.9f)
            )
        }
    }
}
