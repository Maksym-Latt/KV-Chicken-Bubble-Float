package com.chicken.bubblefloat.ui.main.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun VolumeSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val normalized = value.coerceIn(0, 100) / 100f
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDF5FF),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${value.coerceIn(0, 100)}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFF3D8)
            )
        }
        Slider(
            value = normalized,
            onValueChange = { fraction ->
                val newValue = (fraction.coerceIn(0f, 1f) * 100).roundToInt()
                onValueChange(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFFB845),
                activeTrackColor = Color(0xFFFFE38A),
                inactiveTrackColor = Color(0x40FFFFFF)
            )
        )
    }
}
