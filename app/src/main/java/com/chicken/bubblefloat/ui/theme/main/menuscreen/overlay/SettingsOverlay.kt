package com.chicken.bubblefloat.ui.main.menuscreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.LabeledSlider
import com.chicken.bubblefloat.ui.main.component.SecondaryBackButton
import com.chicken.bubblefloat.ui.main.settings.SettingsViewModel

@Composable
fun SettingsOverlay(
    onClose: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // ----------------------- Colors -----------------------
    val panelGrad = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF93311C),
            Color(0xFFB47234),
            Color(0xFF93311C)
        )
    )
    val cardShape = RoundedCornerShape(18.dp)
    val borderColor = Color(0xFF2B1A09)

    // ----------------------- State -----------------------
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    // ----------------------- Overlay -----------------------
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() }
    ) {
        SecondaryBackButton(
            onClick = onClose,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
                .wrapContentHeight()
                .clip(cardShape)
                .background(panelGrad)                       // было Color(0xFF3DE3F8)
                .border(2.dp, borderColor, cardShape)        // было Color(0xFF101010)
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GradientOutlinedText(
                    text = "Settings",
                    fontSize = 46.sp,
                    gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)),
                )
                Spacer(Modifier.height(12.dp))

                LabeledSlider(
                    title = "Volume",
                    value = ui.musicVolume,
                    onChange = viewModel::setMusicVolume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(8.dp))

                LabeledSlider(
                    title = "Sound",
                    value = ui.soundVolume,
                    onChange = viewModel::setSoundVolume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}