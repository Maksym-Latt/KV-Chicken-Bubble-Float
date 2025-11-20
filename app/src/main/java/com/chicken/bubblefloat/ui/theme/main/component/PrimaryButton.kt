package com.chicken.bubblefloat.ui.main.component

import android.R.attr.textColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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


// ---------- Public API ----------
@Composable
fun StartPrimaryButton(
    text: String = "START",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = PrimaryButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    variant = PrimaryVariant.StartGreen
)

@Composable
fun GreenPrimaryButtonWithEgg(
    price: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = PrimaryButton(
    text = price.toString(),
    onClick = onClick,
    modifier = modifier,
    variant = PrimaryVariant.StartGreen,
    content = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.item_egg),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            GradientOutlinedText(
                text = price.toString(),
                fontSize = 28.sp,
                gradientColors = listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))
            )
        }
    }
)

@Composable
fun OrangePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = PrimaryButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    variant = PrimaryVariant.Orange
)

@Composable
fun BluePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = PrimaryButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    variant = PrimaryVariant.Blue
)

// ---------- Internal ----------

private enum class PrimaryVariant { StartGreen, Orange, Blue }

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PrimaryVariant,
    content: (@Composable (() -> Unit))? = null
){
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val params: ButtonParams = when (variant) {
        PrimaryVariant.StartGreen -> ButtonParams(
            family = FontFamily.SansSerif,
            weight = FontWeight.ExtraBold,
            size = 40.sp,
            line = 44.sp,
            padH = 44.dp
        )

        PrimaryVariant.Orange -> ButtonParams(
            family = FontFamily.SansSerif,
            weight = FontWeight.Bold,
            size = 24.sp,
            line = 32.sp,
            padH = 24.dp
        )

        PrimaryVariant.Blue -> ButtonParams(
            family = FontFamily.SansSerif,
            weight = FontWeight.Bold,
            size = 28.sp,
            line = 34.sp,
            padH = 28.dp
        )
    }

    val baseText = Color(0xFFF5F5F5)
    val pressedText = Color(
        red = (baseText.red * 0.85f).coerceIn(0f, 1f),
        green = (baseText.green * 0.85f).coerceIn(0f, 1f),
        blue = (baseText.blue * 0.85f).coerceIn(0f, 1f),
        alpha = 1f
    )
    val textColor = if (isPressed) pressedText else baseText

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .clipToBounds()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            draw3DPrimaryPill(
                canvasSize = size,
                isPressed = isPressed,
                variant = variant
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = params.padH, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (content == null) {
                GradientOutlinedText(
                    text = text,
                    fontSize = params.size,
                    gradientColors = listOf(textColor, textColor)
                )
            } else {
                content()
            }
        }
    }
}

private data class PrimaryPillColors(
    val shadowColor: Color,
    val bottomBrush: Brush,
    val borderColor: Color,
    val topIdle: Brush,
    val topPressed: Brush
)

private fun DrawScope.draw3DPrimaryPill(
    canvasSize: Size,
    isPressed: Boolean,
    variant: PrimaryVariant
) {
    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

    val w = canvasSize.width
    val h = canvasSize.height
    val centerY = h / 2f

    val scale = min(w, h) / 100f

    val colors = when (variant) {
        PrimaryVariant.StartGreen -> PrimaryPillColors(
            shadowColor = Color(0x66000000),
            bottomBrush = Brush.verticalGradient(
                listOf(
                    Color(0xFF0C3F00),
                    Color(0xFF062400)
                )
            ),
            borderColor = Color(0xFF063000),
            topIdle = Brush.verticalGradient(
                listOf(
                    Color(0xFFA7FF4A),
                    Color(0xFF156D00)
                )
            ),
            topPressed = Brush.verticalGradient(
                listOf(
                    Color(0xFF99B978),
                    Color(0xFF0C3F00)
                )
            )
        )

        PrimaryVariant.Orange -> PrimaryPillColors(
            shadowColor = Color(0x66000000),
            bottomBrush = Brush.verticalGradient(
                listOf(
                    Color(0xFFAA4A00),
                    Color(0xFF5A1F00)
                )
            ),
            borderColor = Color(0xFF5A1F00),
            topIdle = Brush.verticalGradient(
                listOf(
                    Color(0xFFFFE3A1),
                    Color(0xFFF56B00)
                )
            ),
            topPressed = Brush.verticalGradient(
                listOf(
                    Color(0xFFFFC847),
                    Color(0xFFAA4A00)
                )
            )
        )

        PrimaryVariant.Blue -> PrimaryPillColors(
            shadowColor = Color(0x66000000),
            bottomBrush = Brush.verticalGradient(
                listOf(
                    Color(0xFF0D8C91),
                    Color(0xFF05575B)
                )
            ),
            borderColor = Color(0xFF003034),
            topIdle = Brush.verticalGradient(
                listOf(
                    Color(0xFF6DFAFF),
                    Color(0xFF0D8C91)
                )
            ),
            topPressed = Brush.verticalGradient(
                listOf(
                    Color(0xFF4DD8DD),
                    Color(0xFF05575B)
                )
            )
        )
    }

    val topBrush = if (isPressed) colors.topPressed else colors.topIdle

    drawOval(
        color = colors.shadowColor,
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
        brush = colors.bottomBrush,
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
        color = colors.borderColor,
        topLeft = Offset(horizontalInset, topTop),
        size = Size(w - horizontalInset * 2, pillHeight),
        cornerRadius = CornerRadius(pillRadius, pillRadius),
        style = Stroke(width = 3.5f * scale)
    )
}

@Stable
private data class ButtonParams(
    val family: FontFamily,
    val weight: FontWeight,
    val size: TextUnit,
    val line: TextUnit,
    val padH: Dp
)