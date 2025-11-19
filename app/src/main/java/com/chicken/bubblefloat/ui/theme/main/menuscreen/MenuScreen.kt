package com.chicken.bubblefloat.ui.main.menuscreen

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.OrangePrimaryButton
import com.chicken.bubblefloat.ui.main.component.SecondaryIconButton
import com.chicken.bubblefloat.ui.main.component.StartPrimaryButton
import androidx.compose.runtime.getValue

@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    lastResult: RunSummary?,
    bestHeight: Int,
    bestBubbles: Int,
    onOpenSettings: () -> Unit,
    onOpenRecords: () -> Unit
) {
    Surface(color = Color(0xFFFFF4D9)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg_menu),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryIconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    RecordsPreview(bestHeight = bestHeight, bestBubbles = bestBubbles)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    GradientOutlinedText(
                        text = "Chicken\nBubble Float",
                        fontSize = 44.sp,
                        gradientColors = listOf(Color(0xFFFCEDFF), Color(0xFF74E8FF))
                    )

                    FloatingChickenBubble()

                    StartPrimaryButton(
                        text = "Play",
                        onClick = onStartGame,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    OrangePrimaryButton(
                        text = "Records",
                        onClick = onOpenRecords,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    if (lastResult != null) {
                        Text(
                            text = "Last flight: ${lastResult.heightMeters} m • ${lastResult.bubbles} bubbles",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF39506B),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FloatingChickenBubble() {

    Image(
        painter = painterResource(id = R.drawable.chicken_1),
        contentDescription = null,
        modifier = Modifier
            .size(160.dp),
        contentScale = ContentScale.Fit
    )

}

@Composable
private fun RecordsPreview(bestHeight: Int, bestBubbles: Int) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xB0FFFFFF), Color(0x90D6FFFF))
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Best height",
            color = Color(0xFF274653),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "$bestHeight m",
            color = Color(0xFF0D4C5E),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Bubbles: $bestBubbles",
            color = Color(0xFF274653),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
