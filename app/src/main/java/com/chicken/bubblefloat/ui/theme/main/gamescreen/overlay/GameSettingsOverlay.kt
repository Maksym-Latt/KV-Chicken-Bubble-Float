package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.OrangePrimaryButton
import com.chicken.bubblefloat.ui.main.component.SecondaryBackButton
import com.chicken.bubblefloat.ui.main.component.SettingsToggleButton
import com.chicken.bubblefloat.ui.main.component.StartPrimaryButton
import com.chicken.bubblefloat.ui.main.settings.SettingsViewModel

@Composable
fun GameSettingsOverlay(
    onResume: () -> Unit,
    onRetry: () -> Unit,
    onHome: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val panelGrad = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF93311C),
            Color(0xFFB47234),
            Color(0xFF93311C)
        )
    )
    val cardShape = RoundedCornerShape(18.dp)
    val borderColor = Color(0xFF2B1A09)

    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onResume
            )
    ) {
        SecondaryBackButton(
            onClick = onResume,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(horizontal = 20.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(320.dp)
                .wrapContentHeight()
                .clip(cardShape)
                .background(panelGrad)
                .border(2.dp, borderColor, cardShape)
                .padding(vertical = 24.dp, horizontal = 20.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {},
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                GradientOutlinedText(
                    text = "Paused",
                    fontSize = 40.sp,
                    gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)),
                )

                SettingsToggleButton(
                    label = "Music",
                    value = ui.musicEnabled,
                    onToggle = viewModel::setMusicEnabled
                )

                SettingsToggleButton(
                    label = "Sound effects",
                    value = ui.soundEnabled,
                    onToggle = viewModel::setSoundEnabled
                )

                SettingsToggleButton(
                    label = "Debug hitboxes",
                    value = ui.debugHitboxesEnabled,
                    onToggle = viewModel::setDebugHitboxes,
                    enabledLabel = "Shown",
                    disabledLabel = "Hidden"
                )

                Spacer(Modifier.height(8.dp))

                StartPrimaryButton(
                    text = "Continue",
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth()
                )

                OrangePrimaryButton(
                    text = "Restart",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                OrangePrimaryButton(
                    text = "Menu",
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }
    }
}
