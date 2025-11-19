package com.chicken.bubblefloat.ui.main.splashscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import com.chicken.bubblefloat.R

@Composable
internal fun SplashScreen(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7AD8FF), Color(0xFFFFF4D9))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            // ---- GAME TITLE (как на главном меню) ----
            Image(
                painter = painterResource(id = R.drawable.title),
                contentDescription = "Game title",
                modifier = Modifier.fillMaxWidth(1f),
                contentScale = ContentScale.Crop
            )

            // ---- CHICKEN HERO (как на главном меню, но меньше) ----
            Image(
                painter = painterResource(id = R.drawable.chicken),
                contentDescription = "Chicken mascot",
                modifier = Modifier
                    .fillMaxWidth(0.42f),
                contentScale = ContentScale.Crop
            )

            // ---- LOADING TEXT ----
            AnimatedLoadingText(
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // ---- PROGRESS BAR ----
            GradientProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}
