package com.chicken.bubblefloat.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Density
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.audio.rememberAudioController
import com.chicken.bubblefloat.game.GameEngine
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.SecondaryIconButton
import com.chicken.bubblefloat.ui.main.locker.ChickenSkins
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.GameSettingsOverlay
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.IntroOverlay
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.WinOverlay
import com.chicken.bubblefloat.ui.main.menuscreen.RunSummary
import com.chicken.bubblefloat.ui.main.settings.SettingsViewModel
import com.chicken.bubblefloat.ui.theme.main.component.CurrencyHeader
import kotlin.math.min

@Composable
fun GameScreen(
    onExitToMenu: (RunSummary) -> Unit,
    onRecordRun: (RunSummary) -> Unit,
    selectedSkinId: String,
    viewModel: GameViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settingsUi by settingsViewModel.ui.collectAsStateWithLifecycle()
    val audio = rememberAudioController()
    val lifecycleOwner = LocalLifecycleOwner.current
    var exitingToMenu by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    val current = viewModel.state.value
                    val isSettingsShown = current.phase == GameViewModel.GamePhase.Paused
                    val isResultShown = current.phase == GameViewModel.GamePhase.Result
                    if (!isSettingsShown && !isResultShown) {
                        viewModel.pauseAndOpenSettings()
                    }
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.phase, exitingToMenu) {
        if (exitingToMenu) return@LaunchedEffect
        when (state.phase) {
            GameViewModel.GamePhase.Running -> {
                audio.playGameMusic()
                audio.resumeMusic()
            }
            GameViewModel.GamePhase.Paused -> audio.pauseMusic()
            GameViewModel.GamePhase.Result -> {
                audio.stopMusic()
                audio.playGameWin()
            }
            GameViewModel.GamePhase.Intro -> Unit
        }
    }

    var lastLives by remember { mutableStateOf(state.lives) }
    var lastEggs by remember { mutableStateOf(state.eggs) }
    var lastInvincible by remember { mutableStateOf(state.invincibleMillis) }
    var lastResultSummary by remember { mutableStateOf<RunSummary?>(null) }
    var hitAnimationTrigger by remember { mutableStateOf(0) }
    var pickupAnimationTrigger by remember { mutableStateOf(0) }
    var powerupAnimationTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(state.lives) {
        if (state.lives < lastLives) {
            audio.playChickenHit()
            hitAnimationTrigger++
        }
        lastLives = state.lives
    }

    LaunchedEffect(state.eggs) {
        if (state.eggs > lastEggs) {
            audio.playChickenPickup()
            pickupAnimationTrigger++
        }
        lastEggs = state.eggs
    }

    LaunchedEffect(state.invincibleMillis) {
        if (state.invincibleMillis > 0 && lastInvincible == 0L) {
            audio.playRareChicken()
            powerupAnimationTrigger++
        }
        lastInvincible = state.invincibleMillis
    }

    LaunchedEffect(state.phase, state.heightRounded, state.eggs) {
        if (state.phase == GameViewModel.GamePhase.Result) {
            lastResultSummary = RunSummary(heightMeters = state.heightRounded, eggs = state.eggs)
        }
        if (state.phase == GameViewModel.GamePhase.Running) {
            lastResultSummary = null
        }
    }

    val summary = lastResultSummary ?: RunSummary(heightMeters = state.heightRounded, eggs = state.eggs)

    BackHandler {
        when (state.phase) {
            GameViewModel.GamePhase.Running -> viewModel.pauseAndOpenSettings()
            GameViewModel.GamePhase.Result, GameViewModel.GamePhase.Intro -> {
                exitingToMenu = true
                viewModel.exitToMenu()
                onExitToMenu(summary)
            }
            GameViewModel.GamePhase.Paused -> Unit
        }
    }

    Surface(color = Color(0xFFFFF4D9)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg_game),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                GameHud(
                    lives = state.lives,
                    eggs = state.eggs,
                    height = state.heightRounded,
                    invincibleProgress = state.invincibilityProgress,
                    isInvincible = state.invincibleMillis > 0,
                    onPause = { if (state.phase == GameViewModel.GamePhase.Running) viewModel.pause() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                GamePlayfield(
                    playerX = state.playerX,
                    obstacles = state.obstacles,
                    collectibles = state.collectibles,
                    onControl = viewModel::movePlayer,
                    showDebugHitboxes = settingsUi.debugHitboxesEnabled,
                    playerSkinId = selectedSkinId,
                    isInvincible = state.invincibleMillis > 0,
                    hitAnimationTrigger = hitAnimationTrigger,
                    collectAnimationTrigger = pickupAnimationTrigger,
                    powerupAnimationTrigger = powerupAnimationTrigger
                )
            }

            if (state.phase == GameViewModel.GamePhase.Intro) {
                IntroOverlay(onStart = viewModel::startGame)
            }

            if (state.phase == GameViewModel.GamePhase.Paused) {
                GameSettingsOverlay(
                    onResume = viewModel::resume,
                    onRetry = viewModel::retry,
                    onHome = {
                        exitingToMenu = true
                        viewModel.exitToMenu()
                        onExitToMenu(summary)
                    }
                )
            }

            if (state.phase == GameViewModel.GamePhase.Result) {
                WinOverlay(
                    summary = summary,
                    onRetry = {
                        onRecordRun(summary)
                        viewModel.retry()
                    },
                    onHome = {
                        exitingToMenu = true
                        viewModel.exitToMenu()
                        onExitToMenu(summary)
                    }
                )
            }
        }
    }
}

@Composable
private fun GameHud(
    lives: Int,
    eggs: Int,
    height: Int,
    invincibleProgress: Float,
    isInvincible: Boolean,
    onPause: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // --- Верхний ряд: пауза + зелёная валюта ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryIconButton(onClick = onPause) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.85f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            CurrencyHeader(
                eggs = eggs,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Ряд с сердцами по центру ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            HeartRow(lives = lives)
        }

        Spacer(modifier = Modifier.height(6.dp))


        InfinityRainbowBar(
            progress = invincibleProgress,
            isActive = isInvincible
        )
    }
}

// ---------- Infinity rainbow bar под сердцами ----------

@Composable
private fun InfinityRainbowBar(
    progress: Float,
    isActive: Boolean
) {
    val clamped = progress.coerceIn(0f, 1f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // полоса прогресса
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFEDEDED))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (isActive) clamped else 0f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFC4CFF),
                                Color(0xFFB446FF),
                                Color(0xFF7F3BFF)
                            )
                        )
                    )
            )
        }

        // подпись "Infinity rainbow" как на скрине
        Box {
            Text(
                text = "Infinity rainbow",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF141414),
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        offset = Offset(2f, 2f),
                        blurRadius = 8f
                    )
                )
            )
            Text(
                text = "Infinity rainbow",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}


@Composable
private fun HeartRow(lives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(GameEngine.MAX_LIVES) { index ->
            val active = index < lives
            Image(
                painter = painterResource(id = R.drawable.item_heart),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                colorFilter = if (active) null else ColorFilter.tint(Color(0x80FFFFFF))
            )
        }
    }
}

@Composable
private fun HeightBadge(height: Int) {
    GradientOutlinedText(
        text = "Height: ${height} m",
        fontSize = 32.sp,
        gradientColors = listOf(Color(0xFFE5FCFF), Color(0xFF85E4FF))
    )
}

@Composable
private fun HeartStatus(
    lives: Int,
    isInvincible: Boolean,
    invincibleProgress: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HeartRow(lives = lives)
        if (isInvincible) {
            BubbleShieldBar(progress = invincibleProgress)
        }
    }
}

@Composable
private fun BubbleShieldBar(progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "Bubble shield",
            color = Color(0xFF3B4B73),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0x33FFFFFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFFFD9F7), Color(0xFF8FE6FF))
                        )
                    )
            )
        }
    }
}

@Composable
private fun GamePlayfield(
    playerX: Float,
    obstacles: List<GameViewModel.Obstacle>,
    collectibles: List<GameViewModel.Collectible>,
    onControl: (Float) -> Unit,
    showDebugHitboxes: Boolean,
    playerSkinId: String,
    isInvincible: Boolean,
    hitAnimationTrigger: Int,
    collectAnimationTrigger: Int,
    powerupAnimationTrigger: Int
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0x66FFFFFF))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    updatePlayerFromPointer(down.position.x, size.width.toFloat(), onControl)
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (change.pressed) {
                            updatePlayerFromPointer(change.position.x, size.width.toFloat(), onControl)
                            change.consume()
                        } else {
                            break
                        }
                    }
                }
            }
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        obstacles.forEach { obstacle ->
            val placement = calculatePlacement(
                widthPx = widthPx,
                heightPx = heightPx,
                x = obstacle.x,
                y = obstacle.y,
                width = obstacle.width,
                height = obstacle.height,
                density = density
            )
            val hitboxPlacement = calculatePlacement(
                widthPx = widthPx,
                heightPx = heightPx,
                x = obstacle.x,
                y = obstacle.y,
                width = obstacle.hitboxSize,
                height = obstacle.hitboxSize,
                density = density
            )
            val spriteModifier = placement.asModifier()
            ObstacleSprite(
                type = obstacle.type,
                modifier = spriteModifier
            )
            if (showDebugHitboxes) {
                DebugHitbox(
                    modifier = hitboxPlacement.asModifier(),
                    color = Color(0xFFFF4D4D)
                )
            }
        }

        collectibles.forEach { collectible ->
            val placement = calculatePlacement(
                widthPx = widthPx,
                heightPx = heightPx,
                x = collectible.x,
                y = collectible.y,
                width = collectible.width,
                height = collectible.height,
                density = density
            )
            val hitboxPlacement = calculatePlacement(
                widthPx = widthPx,
                heightPx = heightPx,
                x = collectible.x,
                y = collectible.y,
                width = collectible.hitboxSize,
                height = collectible.hitboxSize,
                density = density
            )
            val spriteModifier = placement.asModifier()
            CollectibleSprite(
                type = collectible.type,
                modifier = spriteModifier
            )
            if (showDebugHitboxes) {
                DebugHitbox(
                    modifier = hitboxPlacement.asModifier(),
                    color = Color(0xFF00FFA3)
                )
            }
        }

        val playerPlacement = calculatePlacement(
            widthPx = widthPx,
            heightPx = heightPx,
            x = playerX,
            y = GameEngine.PLAYER_Y,
            width = GameEngine.PLAYER_SIZE,
            height = GameEngine.PLAYER_SIZE,
            density = density
        )

        val playerHitboxPlacement = calculatePlacement(
            widthPx = widthPx,
            heightPx = heightPx,
            x = playerX,
            y = GameEngine.PLAYER_Y,
            width = GameEngine.PLAYER_HITBOX_SIZE,
            height = GameEngine.PLAYER_HITBOX_SIZE,
            density = density
        )
        val playerModifier = playerPlacement.asModifier()

        PlayerSprite(
            modifier = playerModifier,
            skinId = playerSkinId,
            isInvincible = isInvincible,
            hitAnimationTrigger = hitAnimationTrigger,
            collectAnimationTrigger = collectAnimationTrigger,
            powerupAnimationTrigger = powerupAnimationTrigger
        )
        if (showDebugHitboxes) {
            DebugHitbox(
                modifier = playerHitboxPlacement.asModifier(),
                color = Color(0xFF4DB2FF)
            )
        }
    }
}

private fun updatePlayerFromPointer(x: Float, width: Float, onControl: (Float) -> Unit) {
    if (width <= 0f) return
    val fraction = (x / width).coerceIn(0f, 1f)
    onControl(fraction)
}

private fun calculatePlacement(
    widthPx: Float,
    heightPx: Float,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    density: Density
): Quadruple {

    // Единый квадратный масштаб
    val scale = min(widthPx, heightPx)

    // Игровая область (квадрат)
    val gameWidth = scale
    val gameHeight = scale

    // Центрируем игровую область по экрану
    val offsetX = (widthPx - gameWidth) / 2f
    val offsetY = (heightPx - gameHeight) / 2f

    // Координаты в пикселях, одинаковые для X и Y
    val pxX = offsetX + x * gameWidth
    val pxY = offsetY + (1f - y) * gameHeight

    // Размеры в пикселях
    val wPx = width * scale
    val hPx = height * scale

    // Сдвигаем влево/вверх на половину размера
    val left = with(density) { (pxX - wPx / 2f).toDp() }
    val top = with(density) { (pxY - hPx / 2f).toDp() }
    val w = with(density) { wPx.toDp() }
    val h = with(density) { hPx.toDp() }

    return Quadruple(left, top, w, h)
}

private data class Quadruple(
    val first: Dp,
    val second: Dp,
    val third: Dp,
    val fourth: Dp
)

private fun Quadruple.asModifier(): Modifier {
    return Modifier
        .offset(first, second)
        .size(third, fourth)
}

@Composable
private fun ObstacleSprite(type: GameEngine.ObstacleType, modifier: Modifier) {
    when (type) {
        GameEngine.ObstacleType.Thorns -> Image(
            painter = painterResource(id = R.drawable.item_thorn),
            contentDescription = null,
            modifier = modifier
        )
        GameEngine.ObstacleType.Crow -> Image(
            painter = painterResource(id = R.drawable.item_crow),
            contentDescription = null,
            modifier = modifier
        )
    }
}

@Composable
private fun CollectibleSprite(type: GameEngine.CollectibleType, modifier: Modifier) {
    when (type) {
        GameEngine.CollectibleType.Egg -> Image(
            painter = painterResource(id = R.drawable.item_egg),
            contentDescription = null,
            modifier = modifier
        )
        GameEngine.CollectibleType.Bubble -> Box(
            modifier = modifier
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFFF1FF), Color(0xFF92E0FF), Color.Transparent),
                        center = Offset(0.3f, 0.3f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.item_bubble),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
    }
}

@Composable
private fun PlayerSprite(
    modifier: Modifier,
    skinId: String,
    isInvincible: Boolean,
    hitAnimationTrigger: Int,
    collectAnimationTrigger: Int,
    powerupAnimationTrigger: Int
) {
    val skin = remember(skinId) { ChickenSkins.findById(skinId) }
    val density = LocalDensity.current

    val shakeOffset = remember { Animatable(0f) }
    val hitFlash = remember { Animatable(0f) }
    val pickupScale = remember { Animatable(1f) }
    val powerupScale = remember { Animatable(1f) }

    val idleTransition = rememberInfiniteTransition(label = "playerIdle")
    val bobOffset by idleTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobOffset"
    )
    val bobTilt by idleTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobTilt"
    )

    val auraPulseTransition = rememberInfiniteTransition(label = "aura")
    val auraPulseScale by auraPulseTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraPulse"
    )
    val auraAlpha by animateFloatAsState(
        targetValue = if (isInvincible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "auraAlpha"
    )

    LaunchedEffect(hitAnimationTrigger) {
        val impact = with(density) { 10.dp.toPx() }
        shakeOffset.snapTo(0f)
        hitFlash.snapTo(0.4f)
        hitFlash.animateTo(0f, animationSpec = tween(durationMillis = 280))
        shakeOffset.animateTo(
            targetValue = -impact,
            animationSpec = tween(durationMillis = 70, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = impact,
            animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(collectAnimationTrigger) {
        pickupScale.snapTo(1f)
        pickupScale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
        )
        pickupScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(powerupAnimationTrigger) {
        powerupScale.snapTo(1.2f)
        powerupScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val combinedScale = pickupScale.value * powerupScale.value

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (auraAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pxOffset = shakeOffset.value
                        translationX = pxOffset
                        translationY = with(density) { bobOffset.dp.toPx() / 2f }
                        scaleX = auraPulseScale * combinedScale
                        scaleY = auraPulseScale * combinedScale
                        alpha = auraAlpha
                    }
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x66C5F8FF),
                                Color(0x3389E6FF),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Image(
            painter = painterResource(id = skin.spriteRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.85f)
                .graphicsLayer {
                    translationX = shakeOffset.value
                    translationY = with(density) { bobOffset.dp.toPx() }
                    rotationZ = bobTilt
                    scaleX = combinedScale
                    scaleY = combinedScale
                },
            contentScale = ContentScale.Fit
        )

        if (hitFlash.value > 0.01f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        alpha = hitFlash.value
                    }
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x66FF7B7B))
            )
        }
    }
}

@Composable
private fun DebugHitbox(modifier: Modifier, color: Color) {
    Box(
        modifier = modifier.border(1.5.dp, color, RoundedCornerShape(2.dp))
    )
}
