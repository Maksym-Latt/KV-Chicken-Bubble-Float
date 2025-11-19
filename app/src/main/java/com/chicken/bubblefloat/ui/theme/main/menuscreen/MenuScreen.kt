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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.chicken.bubblefloat.ui.main.locker.ChickenSkins

@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    lastResult: RunSummary?,
    bestHeight: Int,
    bestEggs: Int,
    totalEggs: Int,
    selectedSkinId: String,
    onOpenSettings: () -> Unit,
    onOpenLocker: () -> Unit
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

                    Spacer(modifier = Modifier.width(16.dp))

                    EggWallet(totalEggs = totalEggs)

                    Spacer(modifier = Modifier.weight(1f))

                    RecordsPreview(bestHeight = bestHeight, bestEggs = bestEggs)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    GameTitle()

                    FloatingChickenBubble(skinId = selectedSkinId)

                    StartPrimaryButton(
                        text = "Play",
                        onClick = onStartGame,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    OrangePrimaryButton(
                        text = "Locker",
                        onClick = onOpenLocker,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    if (lastResult != null) {
                        Text(
                            text = "Last flight: ${lastResult.heightMeters} m • ${lastResult.eggs} eggs",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF39506B),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(2f))
            }
        }
    }
}

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
                Color(0xFFF7C8FF),
                Color(0xFF7BB7FF)
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


@Composable
private fun FloatingChickenBubble(skinId: String) {
    val skin = remember(skinId) { ChickenSkins.findById(skinId) }
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    listOf(Color(0x66FFFFFF), Color(0x339ADFFF), Color.Transparent)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = skin.bubbleRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.8f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun EggWallet(totalEggs: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xD0FFFFFF))
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.item_egg),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = totalEggs.toString(),
            color = Color(0xFF204A64),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecordsPreview(bestHeight: Int, bestEggs: Int) {
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
            text = "Eggs: $bestEggs",
            color = Color(0xFF274653),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
