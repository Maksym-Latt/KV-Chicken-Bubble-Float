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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.OrangePrimaryButton
import com.chicken.bubblefloat.ui.main.locker.ChickenSkin
import com.chicken.bubblefloat.ui.main.locker.ChickenSkins

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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                )
        ) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                SurfacePanel(
                    eggs = eggs,
                    skin = currentSkin,
                    isOwned = isOwned,
                    isSelected = isSelected,
                    onPrev = { pageIndex = (pageIndex - 1 + skins.size) % skins.size },
                    onNext = { pageIndex = (pageIndex + 1) % skins.size },
                    onBuy = {
                        val success = onBuySkin(currentSkin)
                        if (!success) {
                            showInsufficientDialog = true
                        }
                    },
                    onEquip = { onSelectSkin(currentSkin.id) },
                    onClose = onClose
                )
            }
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
private fun SurfacePanel(
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
    val cardShape = RoundedCornerShape(32.dp)
    Column(
        modifier = Modifier
            .width(340.dp)
            .clip(cardShape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFB7E4FF), Color(0xFFFFE8EF))
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.bg_menu),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                CurrencyHeader(eggs = eggs)
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(28.dp)
                        .clickable(onClick = onClose)
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                GradientOutlinedText(
                    text = "LOCKER",
                    fontSize = 32.sp,
                    gradientColors = listOf(Color(0xFFFFFDFE), Color(0xFFE7B2FF))
                )
                Text(
                    text = "Collect eggs and unlock new chickens!",
                    color = Color(0xFF2E3E5C),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                SkinCarousel(
                    skin = skin,
                    onPrev = onPrev,
                    onNext = onNext
                )
                SkinTitle(skin)
                ActionButton(
                    skin = skin,
                    isOwned = isOwned,
                    isSelected = isSelected,
                    onBuy = onBuy,
                    onEquip = onEquip
                )
            }
        }
    }
}

@Composable
private fun CurrencyHeader(eggs: Int) {
    Row(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xCC1B8E5F))
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.item_egg),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = eggs.toString(),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
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
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0x33FFFFFF), Color(0x33000000))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = skin.spriteRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.9f),
                contentScale = ContentScale.Fit
            )
        }
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
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFF3C5A81))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SkinTitle(skin: ChickenSkin) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = skin.title,
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = skin.accentColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFF6EC))
                .border(2.dp, skin.accentColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Premium chicken bubble",
                color = Color(0xFF3A4A64),
                fontWeight = FontWeight.Medium
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
            OrangePrimaryButton(
                text = "Selected",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
        }
        isOwned -> {
            OrangePrimaryButton(
                text = "Equip",
                onClick = onEquip,
                modifier = Modifier.fillMaxWidth()
            )
        }
        else -> {
            OrangePrimaryButton(
                text = "Buy for ${skin.price}",
                onClick = onBuy,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
            OrangePrimaryButton(
                text = "Okay",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
