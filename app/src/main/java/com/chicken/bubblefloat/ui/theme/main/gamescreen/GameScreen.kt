package com.chicken.bubblefloat.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import kotlin.math.sin

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
    var hitEventCount by remember { mutableStateOf(0) }
    var eggPickupCount by remember { mutableStateOf(0) }

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

    LaunchedEffect(state.lives) {
        if (state.lives < lastLives) {
            audio.playChickenHit()
            hitEventCount++
        }
        lastLives = state.lives
    }

    LaunchedEffect(state.eggs) {
        if (state.eggs > lastEggs) {
            audio.playChickenPickup()
            eggPickupCount++
        }
        lastEggs = state.eggs
    }

    LaunchedEffect(state.invincibleMillis) {
        if (state.invincibleMillis > 0 && lastInvincible == 0L) {
            audio.playRareChicken()
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
                    hitEventCount = hitEventCount,
                    eggPickupCount = eggPickupCount,
                    isInvincible = state.invincibleMillis > 0
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
    hitEventCount: Int,
    eggPickupCount: Int,
    isInvincible: Boolean
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

        val bobAmplitudePx = with(density) { 6.dp.toPx() }
        val tiltAmplitude = 6f

        val infiniteTransition = rememberInfiniteTransition(label = "playfieldInfinite")
        val obstacleBob by infiniteTransition.animateFloat(
            initialValue = -bobAmplitudePx,
            targetValue = bobAmplitudePx,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "obstacleBob"
        )
        val obstacleTilt by infiniteTransition.animateFloat(
            initialValue = -tiltAmplitude,
            targetValue = tiltAmplitude,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "obstacleTilt"
        )

        val eggPulse by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "eggPulse"
        )
        val eggGlow by infiniteTransition.animateFloat(
            initialValue = 0.45f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1300, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "eggGlow"
        )

        val bubblePulseScale by infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bubblePulse"
        )
        val bubblePulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bubbleAlpha"
        )

        val invinciblePulse by infiniteTransition.animateFloat(
            initialValue = 0.92f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "invinciblePulse"
        )
        val invincibleGlow by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.55f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "invincibleGlow"
        )

        val hitShake = remember { Animatable(0f) }
        LaunchedEffect(hitEventCount) {
            if (hitEventCount > 0) {
                hitShake.snapTo(1f)
                hitShake.animateTo(0f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 220f))
            }
        }

        val eggBurst = remember { Animatable(0f) }
        LaunchedEffect(eggPickupCount) {
            if (eggPickupCount > 0) {
                eggBurst.snapTo(1f)
                eggBurst.animateTo(0f, animationSpec = tween(durationMillis = 550, easing = LinearOutSlowInEasing))
            }
        }

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
            val swayDirection = if (obstacle.id % 2 == 0) 1f else -1f
            val animatedModifier = when (obstacle.type) {
                GameEngine.ObstacleType.Thorns -> spriteModifier.graphicsLayer {
                    translationY = obstacleBob * 0.35f * swayDirection
                    rotationZ = obstacleTilt * 0.6f * swayDirection
                }
                GameEngine.ObstacleType.Crow -> spriteModifier.graphicsLayer {
                    translationY = obstacleBob * swayDirection
                    rotationZ = obstacleTilt * swayDirection
                    scaleX = 1f + 0.04f * swayDirection * (obstacleTilt / tiltAmplitude)
                    scaleY = scaleX
                }
            }
            ObstacleSprite(
                type = obstacle.type,
                modifier = animatedModifier
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
            val animatedModifier = when (collectible.type) {
                GameEngine.CollectibleType.Egg -> spriteModifier.graphicsLayer {
                    scaleX = eggPulse
                    scaleY = eggPulse
                    alpha = 0.7f + 0.3f * eggGlow
                }
                GameEngine.CollectibleType.Bubble -> spriteModifier.graphicsLayer {
                    scaleX = bubblePulseScale
                    scaleY = bubblePulseScale
                    alpha = bubblePulseAlpha
                }
            }
            CollectibleSprite(
                type = collectible.type,
                modifier = animatedModifier
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
        val wobbleTranslation = sin(hitShake.value * 22f) * with(density) { 12.dp.toPx() }
        val wobbleRotation = sin(hitShake.value * 30f) * 6f
        val wobbleScale = 1f + 0.1f * hitShake.value
        val auraScale = if (isInvincible) invinciblePulse else 1f + eggBurst.value * 0.35f
        val auraAlpha = if (isInvincible) invincibleGlow else eggBurst.value

        Box(
            modifier = playerModifier.graphicsLayer {
                translationX = wobbleTranslation
                rotationZ = wobbleRotation
                scaleX = wobbleScale
                scaleY = wobbleScale
            },
            contentAlignment = Alignment.Center
        ) {
            if (auraAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = auraAlpha
                            scaleX = auraScale
                            scaleY = auraScale
                        }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x66FFF2FF),
                                    Color(0x55C3F1FF),
                                    Color.Transparent
                                ),
                                center = Offset(0.5f, 0.5f)
                            )
                        )
                )
            }

            PlayerSprite(
                modifier = Modifier.fillMaxSize(),
                skinId = playerSkinId
            )
        }
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
private fun PlayerSprite(modifier: Modifier, skinId: String) {
    val skin = remember(skinId) { ChickenSkins.findById(skinId) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = skin.spriteRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.85f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun DebugHitbox(modifier: Modifier, color: Color) {
    Box(
        modifier = modifier.border(1.5.dp, color, RoundedCornerShape(2.dp))
    )
}
