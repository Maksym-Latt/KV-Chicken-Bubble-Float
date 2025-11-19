package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.OrangePrimaryButton
import com.chicken.bubblefloat.ui.main.component.StartPrimaryButton

@Composable
fun WinOverlay(
    score: Int,
    onHome: () -> Unit,
    onRetry: () -> Unit
) {
    // ----------------------- Overlay -----------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xcd000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
    ) {

        // ----------------------- Share button (top right) -----------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalArrangement = Arrangement.End
        ) {

        }

        // ----------------------- Card -----------------------
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ----------------------- Title -----------------------
            GradientOutlinedText(
                text = "GAME OVER",
                fontSize = 40.sp,
                gradientColors = listOf(Color(0xFFFFE3A1), Color(0xFFFF9D52))
            )

            // ----------------------- Chick -----------------------
            Image(
                painter = painterResource(id = R.drawable.chicken_happy),
                contentDescription = null,
                modifier = Modifier.size(320.dp),
                contentScale = ContentScale.Fit
            )

            // ----------------------- Subtitle -----------------------
            GradientOutlinedText(
                text = "You fed $score seeds!",
                fontSize = 28.sp,
                gradientColors = listOf(Color(0xFFFFF4C2), Color(0xFFFFC66E))
            )

            // ----------------------- Buttons bottom -----------------------
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OrangePrimaryButton(
                    text = "Menu",
                    modifier = Modifier.width(240.dp),
                    onClick = onHome
                )
                StartPrimaryButton(
                    text =  "Play again",
                    onClick = onRetry
                )
            }
        }
    }
}
