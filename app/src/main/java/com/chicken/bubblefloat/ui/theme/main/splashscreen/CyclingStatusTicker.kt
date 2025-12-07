package com.chicken.bubblefloat.ui.main.splashscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.ui.main.component.SolarStrokeLabel
import kotlinx.coroutines.delay

@Composable
fun CyclingStatusTicker(
    modifier: Modifier = Modifier,
    label: String = "LOADING",
    intervalMs: Long = 300L
) {
    val order = intArrayOf(1, 2, 3, 0)
    var idx by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMs)
            idx = (idx + 1) % order.size
        }
    }
    val dots = order[idx]

    val display = buildString {
        append(label)
        if (dots > 0) append(" ")
        repeat(dots) { append(".") }
    }

    SolarStrokeLabel(
        content = display,
        modifier = modifier,
        size = 44.sp,
        edgeThickness = 10f
    )
}