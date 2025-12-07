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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.chicken.bubblefloat.R

@Composable
fun AdaptiveGlowStrokeCaption(
    title: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 44.sp,
    contourSize: Float = 9f,
    spectrum: List<Color> = listOf(Color(0xFFFFA726), Color(0xFFFF6F00)),
    customTypeface: Typeface? = null,
    fallbackFont: Int? = R.font.tillana_extra_bold
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val fontAsset = remember {
        customTypeface ?: fallbackFont?.let { ResourcesCompat.getFont(context, it) }
    }

    val pxHeight = remember(fontSize, fontAsset) {
        val p = android.graphics.Paint().apply {
            isAntiAlias = true
            textSize = with(density) { fontSize.toPx() }
            this.typeface = fontAsset
        }
        val fm = p.fontMetrics
        (fm.descent - fm.ascent)
    }

    val dpHeight = with(density) { pxHeight.toDp() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(dpHeight)
    ) {
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            textSize = with(density) { fontSize.toPx() }
            this.typeface = fontAsset
        }

        val textWidth = paint.measureText(title)
        val fm = paint.fontMetrics

        val x = (size.width - textWidth) / 2f
        val baseline = size.height / 2f - (fm.ascent + fm.descent) / 2f

        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = contourSize
        paint.strokeJoin = android.graphics.Paint.Join.ROUND
        paint.color = Color.Black.toArgb()

        drawContext.canvas.nativeCanvas.drawText(title, x, baseline, paint)

        paint.style = android.graphics.Paint.Style.FILL
        paint.shader = android.graphics.LinearGradient(
            0f,
            baseline + fm.ascent,
            0f,
            baseline + fm.descent,
            spectrum.map { it.toArgb() }.toIntArray(),
            null,
            android.graphics.Shader.TileMode.CLAMP
        )

        drawContext.canvas.nativeCanvas.drawText(title, x, baseline, paint)
    }
}


@Composable
fun SolarStrokeLabel(
    content: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 44.sp,
    edgeThickness: Float = 9f,
    palette: List<Color> = listOf(Color(0xFFFFA726), Color(0xFFFF6F00)),
    fontOverride: Typeface? = null,
    fontFallback: Int? = R.font.tillana_extra_bold
) {
    AdaptiveGlowStrokeCaption(
        title = content,
        modifier = modifier,
        fontSize = size,
        contourSize = edgeThickness,
        spectrum = palette,
        customTypeface = fontOverride,
        fallbackFont = fontFallback
    )
}