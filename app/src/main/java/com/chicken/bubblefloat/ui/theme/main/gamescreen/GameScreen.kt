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
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.GameSettingsOverlay
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.IntroOverlay
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.WinOverlay
import com.chicken.bubblefloat.ui.main.menuscreen.RunSummary
import com.chicken.bubblefloat.ui.main.settings.SettingsViewModel

@Composable
fun GameScreen(
    onExitToMenu: (RunSummary) -> Unit,
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

    LaunchedEffect(state.lives) {
        if (state.lives < lastLives) {
            audio.playChickenHit()
        }
        lastLives = state.lives
    }

    LaunchedEffect(state.eggs) {
        if (state.eggs > lastEggs) {
            audio.playChickenPickup()
        }
        lastEggs = state.eggs
    }

    LaunchedEffect(state.invincibleMillis) {
        if (state.invincibleMillis > 0 && lastInvincible == 0L) {
            audio.playRareChicken()
        }
        lastInvincible = state.invincibleMillis
    }

    val summary = remember(state.heightRounded, state.eggs) {
        RunSummary(heightMeters = state.heightRounded, eggs = state.eggs)
    }

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
                    showDebugHitboxes = settingsUi.debugHitboxesEnabled
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
                    onRetry = viewModel::retry,
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
    Column(modifier = Modifier.fillMaxWidth()) {
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

            Spacer(modifier = Modifier.width(16.dp))

            HeartStatus(
                lives = lives,
                isInvincible = isInvincible,
                invincibleProgress = invincibleProgress
            )

            Spacer(modifier = Modifier.weight(1f))

            EggCounter(eggs = eggs)
        }

        Spacer(modifier = Modifier.height(12.dp))

        HeightBadge(height = height)
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
private fun EggCounter(eggs: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xD0FFFFFF))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.item_egg),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = eggs.toString(),
            color = Color(0xFF16435C),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            maxLines = 1
        )
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
    showDebugHitboxes: Boolean
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
            val spriteModifier = Modifier
                .offset(placement.first, placement.second)
                .size(placement.third, placement.fourth)
            ObstacleSprite(
                type = obstacle.type,
                modifier = spriteModifier
            )
            if (showDebugHitboxes) {
                DebugHitbox(
                    modifier = spriteModifier,
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
            val spriteModifier = Modifier
                .offset(placement.first, placement.second)
                .size(placement.third, placement.fourth)
            CollectibleSprite(
                type = collectible.type,
                modifier = spriteModifier
            )
            if (showDebugHitboxes) {
                DebugHitbox(
                    modifier = spriteModifier,
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

        val playerModifier = Modifier
            .offset(playerPlacement.first, playerPlacement.second)
            .size(playerPlacement.third, playerPlacement.fourth)

        PlayerSprite(
            modifier = playerModifier
        )
        if (showDebugHitboxes) {
            DebugHitbox(
                modifier = playerModifier,
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
    val leftPx = (x - width / 2f) * widthPx
    val topPx = heightPx - (y + height / 2f) * heightPx
    val wPx = width * widthPx
    val hPx = height * heightPx
    val left = with(density) { leftPx.toDp() }
    val top = with(density) { topPx.toDp() }
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
private fun PlayerSprite(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.chicken_1),
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
