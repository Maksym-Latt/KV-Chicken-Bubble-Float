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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.AquaActionButton
import com.chicken.bubblefloat.ui.main.component.LaunchActionButton
import com.chicken.bubblefloat.ui.main.locker.ChickenSkins
import com.chicken.bubblefloat.ui.theme.main.component.GameTitle

@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    selectedSkinId: String,
    onOpenLocker: () -> Unit
) {
    Surface(color = Color(0xFFFFF4D9)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg_menu_bf),
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
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    GameTitle()
                    Spacer(modifier = Modifier.weight(0.1f))
                    FloatingChickenBubble(skinId = selectedSkinId)

                    LaunchActionButton(
                        label = "Play",
                        onTap = onStartGame,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    AquaActionButton(
                        label = "Locker",
                        onTap = onOpenLocker,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.weight(2f))
            }
        }
    }
}

@Composable
private fun FloatingChickenBubble(skinId: String) {
    val skin = remember(skinId) { ChickenSkins.findById(skinId) }
    val infiniteTransition = rememberInfiniteTransition(label = "floatingChicken")
    val floatOffset = infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chickenBobOffset"
    )
    val bubbleScale = infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chickenBobScale"
    )
    Box(
        modifier = Modifier
            .size(280.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    listOf(Color(0x66FFFFFF), Color(0x339ADFFF), Color.Transparent)
                )
            )
            .offset(y = floatOffset.value.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = skin.bubbleRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.8f)
                .graphicsLayer {
                    scaleX = bubbleScale.value
                    scaleY = bubbleScale.value
                },
            contentScale = ContentScale.Fit
        )
    }
}