package com.chicken.bubblefloat.ui.main.menuscreen.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.AdaptiveGlowStrokeCaption
import com.chicken.bubblefloat.ui.main.component.AquaActionButton
import com.chicken.bubblefloat.ui.main.component.LimeEggActionButton
import com.chicken.bubblefloat.ui.main.component.RetroIconButton
import com.chicken.bubblefloat.ui.main.locker.ChickenSkin
import com.chicken.bubblefloat.ui.main.locker.ChickenSkins
import com.chicken.bubblefloat.ui.theme.main.component.CurrencyHeader

@Composable
fun LockerOverlay(
    eggs: Int,
    selectedSkinId: String,
    ownedSkins: Set<String>,
    onSelectSkin: (String) -> Unit,
    onBuySkin: (ChickenSkin) -> Boolean,
    onClose: () -> Unit
) {
    var pageIndex by rememberSaveable { mutableStateOf(0) }
    var showInsufficientDialog by remember { mutableStateOf(false) }

    val skins = ChickenSkins.all
    if (pageIndex !in skins.indices) {
        pageIndex = 0
    }

    val currentSkin = skins[pageIndex]
    val isOwned = ownedSkins.contains(currentSkin.id)
    val isSelected = selectedSkinId == currentSkin.id

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA000000))
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            LockerPanel(
                eggs = eggs,
                skin = currentSkin,
                isOwned = isOwned,
                isSelected = isSelected,
                onPrev = { pageIndex = (pageIndex - 1 + skins.size) % skins.size },
                onNext = { pageIndex = (pageIndex + 1) % skins.size },
                onBuy = {
                    val success = onBuySkin(currentSkin)
                    if (!success) showInsufficientDialog = true
                },
                onEquip = { onSelectSkin(currentSkin.id) },
                onClose = onClose
            )
        }

        AnimatedVisibility(
            visible = showInsufficientDialog,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            InsufficientEggsDialog(onDismiss = { showInsufficientDialog = false })
        }

    }
}


@Composable
private fun LockerPanel(
    eggs: Int,
    skin: ChickenSkin,
    isOwned: Boolean,
    isSelected: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_menu_bf),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RetroIconButton(onPress = onClose) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }
                CurrencyHeader(eggs = eggs)
            }



            Spacer(modifier = Modifier.weight(2f))

            SkinTitle(skin = skin)

            Spacer(modifier = Modifier.weight(0.5f))

            SkinCarousel(
                skin = skin,
                onPrev = onPrev,
                onNext = onNext
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ActionButton(
                    skin = skin,
                    isOwned = isOwned,
                    isSelected = isSelected,
                    onBuy = onBuy,
                    onEquip = onEquip
                )
            }

            Spacer(modifier = Modifier.weight(2f))

        }
    }
}

@Composable
private fun SkinCarousel(
    skin: ChickenSkin,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArrowButton(onClick = onPrev) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous",
                tint = Color.White
            )
        }

        Image(
            painter = painterResource(id = skin.spriteRes),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp),
            contentScale = ContentScale.Fit
        )

        ArrowButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ArrowButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF3C5A81))
            .border(3.dp, Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private val CHICKEN_COLOR = Color(0xFF00C2B8)

@Composable
private fun SkinTitle(skin: ChickenSkin) {
    val rawLines = skin.title.uppercase().split("\n")

    val lines = if (rawLines.size == 1) {
        listOf(rawLines[0], "CHICKEN")
    } else rawLines

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-6).dp)
    ) {
        lines.forEachIndexed { index, line ->

            val isChickenLine = line.contains("CHICKEN")

            val gradient = if (isChickenLine) {
                listOf(CHICKEN_COLOR, CHICKEN_COLOR)
            } else {
                listOf(
                    skin.accentColor.copy(alpha = 0.95f),
                    skin.accentColor
                )
            }

            val alpha = when {
                rawLines.size == 1 && index == 1 -> 0f
                else -> 1f
            }

            AdaptiveGlowStrokeCaption(
                title = line,
                fontSize = 46.sp,
                contourSize = 7f,
                spectrum = gradient,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { this.alpha = alpha }
            )
        }
    }
}

@Composable
private fun ActionButton(
    skin: ChickenSkin,
    isOwned: Boolean,
    isSelected: Boolean,
    onBuy: () -> Unit,
    onEquip: () -> Unit
) {
    when {
        isSelected -> {
            AquaActionButton(
                label = "SELECTED",
                onTap = {},
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        isOwned -> {
            AquaActionButton(
                label = "SELECT",
                onTap = onEquip,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        else -> {
            LimeEggActionButton(
                cost = skin.price,
                onTap = onBuy,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}


@Composable
private fun LockerPrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF36D14C),
                        Color(0xFF178B28)
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
private fun InsufficientEggsDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFF4DF))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Not enough eggs",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF5D2A42)
            )
            Text(
                text = "Collect more eggs to buy this chicken.",
                textAlign = TextAlign.Center,
                color = Color(0xFF5D2A42)
            )
            LockerPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss
            ) {
                Text(
                    text = "OKAY",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
