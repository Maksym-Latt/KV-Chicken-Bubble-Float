package com.chicken.bubblefloat.ui.main.component

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.collections.map
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.chicken.bubblefloat.R

@Composable
fun AdaptiveGradientOutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 44.sp,
    strokeWidth: Float = 9f,
    gradientColors: List<Color> = listOf(Color(0xFFFFA726), Color(0xFFFF6F00)),
    typeface: Typeface? = null,
    fontResId: Int? = R.font.tillana_extra_bold
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // ---------- Load font ----------
    val resolvedTypeface = remember {
        typeface ?: fontResId?.let { ResourcesCompat.getFont(context, it) }
    }

    // ---------- Pre-measure text ----------
    val textHeightPx = remember(fontSize, resolvedTypeface) {
        val p = android.graphics.Paint().apply {
            isAntiAlias = true
            textSize = with(density) { fontSize.toPx() }
            this.typeface = resolvedTypeface
        }
        val fm = p.fontMetrics
        (fm.descent - fm.ascent)
    }

    val textHeightDp = with(density) { textHeightPx.toDp() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(textHeightDp)    // ← ключевой фикс
    ) {
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            textSize = with(density) { fontSize.toPx() }
            this.typeface = resolvedTypeface
        }

        val textWidth = paint.measureText(text)
        val fm = paint.fontMetrics

        val x = (size.width - textWidth) / 2f
        val baseline = size.height / 2f - (fm.ascent + fm.descent) / 2f

        // ---------- Stroke ----------
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeJoin = android.graphics.Paint.Join.ROUND
        paint.color = Color.Black.toArgb()

        drawContext.canvas.nativeCanvas.drawText(text, x, baseline, paint)

        // ---------- Gradient fill ----------
        paint.style = android.graphics.Paint.Style.FILL
        paint.shader = android.graphics.LinearGradient(
            0f,
            baseline + fm.ascent,
            0f,
            baseline + fm.descent,
            gradientColors.map { it.toArgb() }.toIntArray(),
            null,
            android.graphics.Shader.TileMode.CLAMP
        )

        drawContext.canvas.nativeCanvas.drawText(text, x, baseline, paint)
    }
}


@Composable
fun GradientOutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 44.sp,
    strokeWidth: Float = 9f,
    gradientColors: List<Color> = listOf(Color(0xFFFFA726), Color(0xFFFF6F00)),
    typeface: Typeface? = null,
    fontResId: Int? = R.font.tillana_extra_bold
) {
    AdaptiveGradientOutlinedText(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        strokeWidth = strokeWidth,
        gradientColors = gradientColors,
        typeface = typeface,
        fontResId = fontResId
    )
}