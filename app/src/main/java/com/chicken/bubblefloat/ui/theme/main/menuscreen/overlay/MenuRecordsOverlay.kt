package com.chicken.bubblefloat.ui.main.menuscreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText

@Composable
fun MenuRecordsOverlay(
    bestHeight: Int,
    bestBubbles: Int,
    onClose: () -> Unit
) {
    val panelShape = RoundedCornerShape(28.dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClose
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(320.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFFF0D7), Color(0xFFFFD9B8))
                    ),
                    shape = panelShape
                )
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GradientOutlinedText(
                text = "Records",
                fontSize = 38.sp,
                gradientColors = listOf(Color(0xFFFFF8FF), Color(0xFFE671FF))
            )

            Text(
                text = "Best height",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6B3B2A),
                    fontWeight = FontWeight.Bold
                )
            )
            StatValue("${bestHeight} m")

            Text(
                text = "Best bubbles",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6B3B2A),
                    fontWeight = FontWeight.Bold
                )
            )
            StatValue(bestBubbles.toString())
        }
    }
}

@Composable
private fun StatValue(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFE4FBFF), Color(0xFFC7F5FF))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(vertical = 10.dp),
        textAlign = TextAlign.Center,
        color = Color(0xFF1A4F5F),
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold
    )
}
