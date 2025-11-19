package com.chicken.bubblefloat.ui.main.menuscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.SecondaryIconButton
import com.chicken.bubblefloat.ui.main.component.StartPrimaryButton

@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    lastScore: Int?,
    onOpenSettings: () -> Unit,
) {
    // ---------------- Root Surface ----------------
    Surface(color = Color(0xFFFFF4D9)) {
        Box(modifier = Modifier.fillMaxSize()) {

            // ---------------- Background ----------------
            MenuBackground()

            // ---------------- Settings Button (Top-Start) ----------------
            SecondaryIconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open settings",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }

            // ---------------- Center Content ----------------
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    // ------ Title Banner ------
                    TitleBanner()

                    // ------ Chicken Hero ------
                    MenuChickenHero()

                    // ------ Start Button ------
                    StartPrimaryButton(
                        onClick = onStartGame,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    // ------ Last Score ------
                    if (lastScore != null) {
                        GradientOutlinedText(
                            text = "Last score: $lastScore",
                            fontSize = 16.sp,
                            gradientColors = listOf(Color.White, Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuBackground() {
    Image(
        painter = painterResource(id = R.drawable.bg_menu),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun TitleBanner() {
    Image(
        painter = painterResource(id = R.drawable.title),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(0.8f),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun MenuChickenHero() {
    Image(
        painter = painterResource(id = R.drawable.chicken),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(0.5f),
        contentScale = ContentScale.Crop
    )
}
