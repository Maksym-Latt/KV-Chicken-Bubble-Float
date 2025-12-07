package com.chicken.bubblefloat.ui.theme.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.ui.main.component.SolarStrokeLabel


@Composable
fun GameTitle(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        SolarStrokeLabel(
            content = "CHICKEN",
            size = 52.sp,
            edgeThickness = 8f,
            palette = listOf(
                Color(0xFF5EE7E7),
                Color(0xFF138E90)
            )
        )
        SolarStrokeLabel(
            content = "BUBBLE",
            modifier = Modifier.offset(y = 35.dp),
            size = 52.sp,
            edgeThickness = 8f,
            palette = listOf(
                Color(0xffffffff),
                Color(0xffff39ef)
            )
        )
        SolarStrokeLabel(
            content = "FLOAT",
            modifier = Modifier.offset(y = 70.dp),
            size = 52.sp,
            edgeThickness = 8f,
            palette = listOf(
                Color(0xFF7CF3F1),
                Color(0xFF5DD4FA)
            )
        )
    }
}