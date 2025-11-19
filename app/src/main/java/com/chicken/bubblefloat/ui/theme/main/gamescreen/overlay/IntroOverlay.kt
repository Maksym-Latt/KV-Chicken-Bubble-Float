package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.StartPrimaryButton

@Composable
fun IntroOverlay(
    onStart: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xe5000000))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End
        ) { }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------------- TITLE ----------------
            GradientOutlinedText(
                text = "GET READY!",
                fontSize = 40.sp,
                gradientColors = listOf(Color(0xFFFFE3A1), Color(0xFFFF9D52))
            )

            // ---------------- 3 CHICKEN STATES ----------------
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.tile_house),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Closed",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xffffffff),
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.tile_house_with_chicken),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Chicken inside",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xffffffff),
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.tile_house_outside_chicken),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Escaped",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xffffffff),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // ---------------- EXPLANATION ----------------
            Text(
                text = "Watch the chicken coops.\n" +
                        "Tap the chicken as soon as it pops out,\n" +
                        "before it hides again!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xffffffff),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "The faster you react — the more points you gain\nand the higher the speed level goes!",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xffffffff),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                ),
                textAlign = TextAlign.Center
            )

            // ---------------- BUTTON ----------------
            Column(
                modifier = Modifier.padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StartPrimaryButton(
                    text = "Start",
                    onClick = onStart
                )
            }
        }
    }
}