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
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText


@Composable
fun GameTitle(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        // CHICKEN (верхний слой)
        GradientOutlinedText(
            text = "CHICKEN",
            fontSize = 52.sp,
            strokeWidth = 8f,
            gradientColors = listOf(
                Color(0xFF5EE7E7),
                Color(0xFF138E90)
            )
        )

        // BUBBLE (слегка ниже CHICKEN)
        GradientOutlinedText(
            text = "BUBBLE",
            modifier = Modifier.offset(y = 30.dp),
            fontSize = 52.sp,
            strokeWidth = 8f,
            gradientColors = listOf(
                Color(0xffffffff),
                Color(0xffff39ef)
            )
        )

        // FLOAT (ниже остальных)
        GradientOutlinedText(
            text = "FLOAT",
            modifier = Modifier.offset(y = 60.dp),
            fontSize = 52.sp,
            strokeWidth = 8f,
            gradientColors = listOf(
                Color(0xFF7CF3F1),
                Color(0xFF5DD4FA)
            )
        )
    }
}