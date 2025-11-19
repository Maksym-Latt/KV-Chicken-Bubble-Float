package com.chicken.bubblefloat.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.settings.SettingsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import com.chicken.bubblefloat.ui.main.component.SecondaryBackButton
import com.chicken.bubblefloat.ui.main.component.SecondaryIconButton

// ======================== ⏸ PAUSE OVERLAY ========================
@Composable
fun GameSettingsOverlay(
    onResume: () -> Unit,
    onRetry: () -> Unit,
    onHome: () -> Unit,
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
            ) { onResume() }
    ) {
        SecondaryBackButton(
            onClick = onResume,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(start = 20.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
                .wrapContentHeight()
                .clip(cardShape)
                .background(panelGrad)
                .border(2.dp, borderColor, cardShape)
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
                    text = "Pause",
                    fontSize = 46.sp,
                    gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)),
                )
                Spacer(Modifier.height(12.dp))


                Spacer(Modifier.height(8.dp))


                // ----------------------- BOTTOM BUTTONS -----------------------
                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryIconButton(
                        onClick = onRetry,
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.72f)
                        )
                    }

                    SecondaryIconButton(
                        onClick = onHome,
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.72f)
                        )
                    }
                }
            }
        }
    }
}
