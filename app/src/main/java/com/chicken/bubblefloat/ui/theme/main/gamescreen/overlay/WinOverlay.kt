package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.OrangePrimaryButton
import com.chicken.bubblefloat.ui.main.component.StartPrimaryButton
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
            .background(Color(0xAA000000))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 36.dp)
                .background(Color(0xFFFFF6FF), shape = RoundedCornerShape(32.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GradientOutlinedText(
                text = "Great flight!",
                fontSize = 40.sp,
                gradientColors = listOf(Color(0xFFFFF0FF), Color(0xFF79E6FF))
            )

            Image(
                painter = painterResource(id = R.drawable.chicken_1_happy),
                contentDescription = null,
                modifier = Modifier.size(220.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Height: ${summary.heightMeters} m",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF364D63)
            )
            Text(
                text = "Bubbles collected: ${summary.bubbles}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF364D63)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StartPrimaryButton(
                    text = "Retry",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                OrangePrimaryButton(
                    text = "Menu",
                    onClick = onHome,
                    modifier = Modifier.width(220.dp)
                )
            }
        }
    }
}
