package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.AquaActionButton
import com.chicken.bubblefloat.ui.main.component.SolarStrokeLabel
import com.chicken.bubblefloat.ui.main.component.LaunchActionButton
import com.chicken.bubblefloat.ui.main.menuscreen.RunSummary

@Composable
fun WinOverlay(
    summary: RunSummary,
    onHome: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            SolarStrokeLabel(
                content = "Bubbles collected:",
                size = 30.sp,
                palette = listOf(Color(0xFFFFFFFF), Color(0xFFFD8CFF))
            )

            SolarStrokeLabel(
                content = "${summary.eggs}",
                size = 30.sp,
                palette = listOf(Color(0xFFFEADFF), Color(0xFFFC39FF))
            )
            Spacer(modifier = Modifier.weight(0.2f))
            Image(
                painter = painterResource(id = R.drawable.chicken_1_happy),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.9f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.weight(0.2f))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AquaActionButton(
                    label = "Retry",
                    onTap = onRetry,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
                LaunchActionButton(
                    label = "Menu",
                    onTap = onHome,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
