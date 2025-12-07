package com.chicken.bubblefloat.ui.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import com.chicken.bubblefloat.R


@Composable
fun LaunchActionButton(
    label: String = "START",
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) = CoreActionButton(
    label = label,
    onTap = onTap,
    modifier = modifier,
    style = ActionStyle.Lime
)

@Composable
fun LimeEggActionButton(
    cost: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) = CoreActionButton(
    label = cost.toString(),
    onTap = onTap,
    modifier = modifier,
    style = ActionStyle.Lime,
    additional = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.item_egg),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            SolarStrokeLabel(
                content = cost.toString(),
                size = 28.sp,
                palette = listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))
            )
        }
    }
)

@Composable
fun AquaActionButton(
    label: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) = CoreActionButton(
    label = label,
    onTap = onTap,
    modifier = modifier,
    style = ActionStyle.Aqua
)

// ---------- Internal ----------

private enum class ActionStyle { Lime, Ember, Aqua }

@Composable
private fun CoreActionButton(
    label: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    style: ActionStyle,
    additional: (@Composable (() -> Unit))? = null
){
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val params: ActionTypography = when (style) {
        ActionStyle.Lime -> ActionTypography(
            family = FontFamily.SansSerif,
            weight = FontWeight.ExtraBold,
            size = 40.sp,
            lineHeight = 44.sp,
            padding = 44.dp
        )

        ActionStyle.Ember -> ActionTypography(
            family = FontFamily.SansSerif,
            weight = FontWeight.Bold,
            size = 24.sp,
            lineHeight = 32.sp,
            padding = 24.dp
        )

        ActionStyle.Aqua -> ActionTypography(
            family = FontFamily.SansSerif,
            weight = FontWeight.Bold,
            size = 28.sp,
            lineHeight = 34.sp,
            padding = 28.dp
        )
    }

    val idleColor = Color(0xFFF5F5F5)
    val pressedText = Color(
        red = (idleColor.red * 0.85f).coerceIn(0f, 1f),
        green = (idleColor.green * 0.85f).coerceIn(0f, 1f),
        blue = (idleColor.blue * 0.85f).coerceIn(0f, 1f),
        alpha = 1f
    )
    val tint = if (pressed) pressedText else idleColor

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .clipToBounds()
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            draw3DPrimaryPill(
                canvasSize = size,
                isPressed = pressed,
                variant = style
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = params.padding, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (additional == null) {
                SolarStrokeLabel(
                    content = label,
                    size = params.size,
                    palette = listOf(tint, tint)
                )
            } else {
                additional()
            }
        }
    }
}

private data class CapsulePalette(
    val shadow: Color,
    val lower: Brush,
    val outline: Color,
    val upperIdle: Brush,
    val upperPressed: Brush
)

private fun DrawScope.draw3DPrimaryPill(
    canvasSize: Size,
    isPressed: Boolean,
    variant: ActionStyle
) {
    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

    val w = canvasSize.width
    val h = canvasSize.height
    val centerY = h / 2f

    val scale = min(w, h) / 100f

    val colors = when (variant) {
        ActionStyle.Lime -> CapsulePalette(
            shadow = Color(0x66000000),
            lower = Brush.verticalGradient(
                listOf(
                    Color(0xFF0C3F00),
                    Color(0xFF062400)
                )
            ),
            outline = Color(0xFF063000),
            upperIdle = Brush.verticalGradient(
                listOf(
                    Color(0xFFA7FF4A),
                    Color(0xFF156D00)
                )
            ),
            upperPressed = Brush.verticalGradient(
                listOf(
                    Color(0xFF99B978),
                    Color(0xFF0C3F00)
                )
            )
        )

        ActionStyle.Ember -> CapsulePalette(
            shadow = Color(0x66000000),
            lower = Brush.verticalGradient(
                listOf(
                    Color(0xFFAA4A00),
                    Color(0xFF5A1F00)
                )
            ),
            outline = Color(0xFF5A1F00),
            upperIdle = Brush.verticalGradient(
                listOf(
                    Color(0xFFFFE3A1),
                    Color(0xFFF56B00)
                )
            ),
            upperPressed = Brush.verticalGradient(
                listOf(
                    Color(0xFFFFC847),
                    Color(0xFFAA4A00)
                )
            )
        )

        ActionStyle.Aqua -> CapsulePalette(
            shadow = Color(0x66000000),
            lower = Brush.verticalGradient(
                listOf(
                    Color(0xFF0D8C91),
                    Color(0xFF05575B)
                )
            ),
            outline = Color(0xFF003034),
            upperIdle = Brush.verticalGradient(
                listOf(
                    Color(0xFF6DFAFF),
                    Color(0xFF0D8C91)
                )
            ),
            upperPressed = Brush.verticalGradient(
                listOf(
                    Color(0xFF4DD8DD),
                    Color(0xFF05575B)
                )
            )
        )
    }

    val topBrush = if (isPressed) colors.upperPressed else colors.upperIdle

    drawOval(
        color = colors.shadow,
        topLeft = Offset(
            x = w * 0.05f,
            y = h * 0.67f
        ),
        size = Size(
            width = w * 0.9f,
            height = h * 0.40f
        )
    )

    val horizontalInset = 3f * scale
    val pillHeight = h * 0.72f
    val pillRadius = pillHeight / 2f
    val liftOffset = if (isPressed) h * 0.04f else h * 0.095f

    val bottomCenterY = centerY + liftOffset
    val bottomTop = bottomCenterY - pillHeight / 2f

    drawRoundRect(
        brush = colors.lower,
        topLeft = Offset(horizontalInset, bottomTop),
        size = Size(w - horizontalInset * 2, pillHeight),
        cornerRadius = CornerRadius(pillRadius, pillRadius)
    )

    val topCenterY = centerY
    val topTop = topCenterY - pillHeight / 2f

    drawRoundRect(
        brush = topBrush,
        topLeft = Offset(horizontalInset, topTop),
        size = Size(w - horizontalInset * 2, pillHeight),
        cornerRadius = CornerRadius(pillRadius, pillRadius)
    )

    drawRoundRect(
        color = colors.outline,
        topLeft = Offset(horizontalInset, topTop),
        size = Size(w - horizontalInset * 2, pillHeight),
        cornerRadius = CornerRadius(pillRadius, pillRadius),
        style = Stroke(width = 3.5f * scale)
    )
}

@Stable
private data class ActionTypography(
    val family: FontFamily,
    val weight: FontWeight,
    val size: TextUnit,
    val lineHeight: TextUnit,
    val padding: Dp
)