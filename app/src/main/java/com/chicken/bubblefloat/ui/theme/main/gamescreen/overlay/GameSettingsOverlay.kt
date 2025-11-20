package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
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
import com.chicken.bubblefloat.ui.main.component.BluePrimaryButton
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.OrangePrimaryButton
import com.chicken.bubblefloat.ui.main.component.SecondaryBackButton
import com.chicken.bubblefloat.ui.main.component.SecondaryIconButton
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
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    val panelGrad = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF8FF),
            Color(0xFFFF8AE8)
        )
    )
    val cardShape = RoundedCornerShape(36.dp)
    val borderColor = Color(0xFF000000)

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
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(260.dp)
                .wrapContentHeight()
                .clip(cardShape)
                .background(panelGrad)
                .border(3.dp, borderColor, cardShape)
                .padding(vertical = 14.dp, horizontal = 10.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // заголовок как на макете
                GradientOutlinedText(
                    text = "Paused",
                    fontSize = 40.sp,
                    gradientColors = listOf(Color.White, Color.White),
                )

                // блок из 4 круглых иконок (2x2)
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Restart
                        SecondaryIconButton(onClick = onRetry,
                            modifier = Modifier.size(92.dp)) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Restart",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }

                        // Home
                        SecondaryIconButton(onClick = onHome,
                            modifier = Modifier.size(92.dp)) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sound toggle
                        SecondaryIconButton(
                            onClick = { viewModel.setSoundEnabled(!ui.soundEnabled) },
                            modifier = Modifier.size(92.dp)
                        ) {
                            Icon(
                                imageVector = if (ui.soundEnabled)
                                    Icons.Default.VolumeUp
                                else
                                    Icons.Default.VolumeOff,
                                contentDescription = "Sound",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }

                        // Music toggle
                        SecondaryIconButton(
                            onClick = { viewModel.setMusicEnabled(!ui.musicEnabled) },
                            modifier = Modifier.size(92.dp)
                        ) {
                            Icon(
                                imageVector = if (ui.musicEnabled)
                                    Icons.Default.MusicNote
                                else
                                    Icons.Default.MusicOff,
                                contentDescription = "Music",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    BluePrimaryButton(
                        text = "Play",
                        onClick = onResume,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Spacer(Modifier.height(2.dp))

                }

            }
        }
    }
}
