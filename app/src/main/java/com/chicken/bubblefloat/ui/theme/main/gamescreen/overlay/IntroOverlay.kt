package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.chicken.bubblefloat.ui.main.component.StartPrimaryButton

@Composable
fun IntroOverlay(
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE0000000))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .background(Color(0xFFFEF4FF), shape = RoundedCornerShape(28.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GradientOutlinedText(
                text = "Ready to float?",
                fontSize = 36.sp,
                gradientColors = listOf(Color(0xFFFFE3FF), Color(0xFF8DEBFF))
            )

            Text(
                text = "Drag the bubble left and right. Avoid prickly traps and angry birds.",
                textAlign = TextAlign.Center,
                color = Color(0xFF3C3F63),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            DangerRow()

            Text(
                text = "Collect tiny bubbles to gain points. Rainbow bubbles make you invincible for a short time!",
                textAlign = TextAlign.Center,
                color = Color(0xFF3C3F63),
                fontSize = 16.sp
            )

            RewardRow()

            Spacer(modifier = Modifier.size(4.dp))

            StartPrimaryButton(
                text = "Start",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

@Composable
private fun DangerRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DangerItem(icon = R.drawable.item_thorn, label = "Thorns")
        DangerItem(icon = R.drawable.item_crow, label = "Crows")
        DangerItem(icon = R.drawable.item_egg, label = "Branches")
    }
}

@Composable
private fun RewardRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RewardItem(icon = R.drawable.item_bubble, label = "Bubbles")
        RainbowRewardItem()
    }
}

@Composable
private fun DangerItem(icon: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = label,
            color = Color(0xFF5A2B24),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RewardItem(icon: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = label,
            color = Color(0xFF1D5F63),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RainbowRewardItem() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFFF2FF), Color(0xFF8EE8FF), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Text(
            text = "Rainbow",
            color = Color(0xFF1D5F63),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
