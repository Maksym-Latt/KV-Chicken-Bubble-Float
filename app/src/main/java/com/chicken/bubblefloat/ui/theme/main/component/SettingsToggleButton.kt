package com.chicken.bubblefloat.ui.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsToggleButton(
    label: String,
    value: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabledLabel: String = "On",
    disabledLabel: String = "Off"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFDF5FF),
            modifier = Modifier.weight(1f)
        )

        val chipShape = RoundedCornerShape(999.dp)
        val interaction = remember { MutableInteractionSource() }
        val chipColors = if (value) {
            Brush.horizontalGradient(listOf(Color(0xFFFFC74A), Color(0xFFFF9A3C)))
        } else {
            Brush.horizontalGradient(listOf(Color(0x4DFFFFFF), Color(0x4DFFFFFF)))
        }
        val labelColor = if (value) Color(0xFF2B1A09) else Color(0xFF3B4B73)
        val textLabel = if (value) enabledLabel else disabledLabel

        Text(
            text = textLabel,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            modifier = Modifier
                .clip(chipShape)
                .background(chipColors)
                .clickable(interactionSource = interaction, indication = null) {
                    onToggle(!value)
                }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}
