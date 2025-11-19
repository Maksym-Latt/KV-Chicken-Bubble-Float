package com.chicken.bubblefloat.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chicken.bubblefloat.audio.rememberAudioController
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.game.GameEngine
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedText
import com.chicken.bubblefloat.ui.main.component.GradientOutlinedTextShort
import com.chicken.bubblefloat.ui.main.component.SecondaryIconButton
import com.chicken.bubblefloat.ui.main.gamescreen.GameViewModel.TapOutcome
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.GameSettingsOverlay
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.IntroOverlay
import com.chicken.bubblefloat.ui.main.gamescreen.overlay.WinOverlay
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay

// ======================= 🐣 ЭКРАН ИГРЫ =======================
@Composable
fun GameScreen(
    onExitToMenu: (Int) -> Unit,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val audio = rememberAudioController()
    val lifecycleOwner = LocalLifecycleOwner.current
    var exitingToMenu by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    val current = viewModel.state.value
                    val isSettingsShown = current.phase == GameViewModel.GamePhase.Paused
                    val isWinShown = current.phase == GameViewModel.GamePhase.Result
                    if (!isWinShown && !isSettingsShown) {
                        viewModel.pauseAndOpenSettings()
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (viewModel.state.value.phase == GameViewModel.GamePhase.Paused) {
                        audio.pauseMusic()
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

    // ----------------------- Эффекты на клетках -----------------------
    val effects = remember { mutableStateListOf<CellEffect>() }
    var effectCounter by remember { mutableLongStateOf(0L) }
    var previousChickens by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    val recentHitIds = remember { mutableStateListOf<Int>() }

    fun triggerEffect(slot: Int, type: EffectType) {
        val effect = CellEffect(id = effectCounter++, slot = slot, type = type)
        effects += effect
    }

    LaunchedEffect(state.chickens) {
        if (state.phase != GameViewModel.GamePhase.Running) {
            previousChickens = state.chickens.associate { it.id to it.slotIndex }
            recentHitIds.clear()
            return@LaunchedEffect
        }

        val current = state.chickens.associate { it.id to it.slotIndex }
        val removed = previousChickens.filterKeys { it !in current }
        removed.forEach { (id, slot) ->
            if (recentHitIds.contains(id)) {
                recentHitIds.remove(id)
            } else {
                audio.playChickenEscape()
                triggerEffect(slot, EffectType.Escape)
            }
        }
        recentHitIds.removeAll { it !in current.keys }
        previousChickens = current
    }

    // ----------------------- Обработка Back -----------------------
    BackHandler {
        when (state.phase) {
            GameViewModel.GamePhase.Running -> viewModel.pauseAndOpenSettings()
            GameViewModel.GamePhase.Result -> {
                val finalScore = state.score
                exitingToMenu = true
                viewModel.exitToMenu()
                onExitToMenu(finalScore)
            }
            GameViewModel.GamePhase.Intro -> {
                exitingToMenu = true
                viewModel.exitToMenu()
                onExitToMenu(state.score)
            }
            GameViewModel.GamePhase.Paused -> Unit
        }
    }

    // ----------------------- Резерв под нижнюю курицу -----------------------
    var foregroundChickenHeightDp by remember { mutableStateOf(0.dp) }

    Surface(color = Color(0xFFFFF4D9)) {
        Box(modifier = Modifier.fillMaxSize()) {

            GameBackground()

            // =================== ОСНОВНОЙ КОНТЕНТ (HUD + GRID) ===================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 16.dp + foregroundChickenHeightDp
                    ),
                verticalArrangement = Arrangement.Top
            ) {
                TopHud(
                    phase = state.phase,
                    timeSeconds = state.remainingSeconds,
                    score = state.score,
                    combo = state.combo,
                    speed = state.speedLevel,
                    rareHits = state.rareHits,
                    onPause = viewModel::pause
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoard(
                        chickens = state.chickens,
                        effects = effects,
                        onTapSlot = { slot ->
                            when (val outcome = viewModel.tap(slot)) {
                                is TapOutcome.Hit -> {
                                    recentHitIds += outcome.chickenId
                                    val effectType =
                                        if (outcome.type == GameEngine.ChickenType.Rare) {
                                            audio.playRareChicken()
                                            EffectType.Rare
                                        } else {
                                            audio.playChickenHit()
                                            EffectType.Hit
                                        }
                                    triggerEffect(slot, effectType)
                                }
                                TapOutcome.Miss -> triggerEffect(slot, EffectType.Miss)
                            }
                        },
                        onEffectConsumed = { effect -> effects.remove(effect) }
                    )
                }
            }

            // =================== Нижняя курица ===================
            GameForegroundChicken(
                onMeasuredHeight = { measuredHeight ->
                    foregroundChickenHeightDp = measuredHeight
                }
            )

            // =================== INTRO OVERLAY ===================
            if (state.phase == GameViewModel.GamePhase.Intro) {
                IntroOverlay(
                    onStart = {
                        viewModel.startGame()
                    }
                )
            }

            // =================== PAUSE OVERLAY ===================
            if (state.phase == GameViewModel.GamePhase.Paused) {
                GameSettingsOverlay(
                    onResume = viewModel::resume,
                    onRetry = viewModel::retry,
                    onHome = {
                        val finalScore = state.score
                        exitingToMenu = true
                        viewModel.exitToMenu()
                        onExitToMenu(finalScore)
                    }
                )
            }

            // =================== RESULT OVERLAY ===================
            if (state.phase == GameViewModel.GamePhase.Result) {
                WinOverlay(
                    score = state.score,
                    onRetry = viewModel::retry,
                    onHome = {
                        val finalScore = state.score
                        exitingToMenu = true
                        viewModel.exitToMenu()
                        onExitToMenu(finalScore)
                    }
                )
            }
        }
    }
}



private data class CellEffect(val id: Long, val slot: Int, val type: EffectType)

private enum class EffectType { Hit, Miss, Escape, Rare }


// ========================= 🔼 TOP HUD =========================
@Composable
private fun TopHud(
    phase: GameViewModel.GamePhase,
    timeSeconds: Int,
    score: Int,
    combo: Int,
    speed: Int,
    rareHits: Int,
    onPause: () -> Unit,
) {
    val canPause = phase == GameViewModel.GamePhase.Running

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.displayCutout),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --------- ВЕРХНИЙ РЯД: кнопка слева, текст справа ---------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryIconButton(
                onClick = { if (canPause) onPause() },
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }

            // Этот Spacer с weight толкает ScoreBoard в самый правый край
            Spacer(modifier = Modifier.weight(1f))

            ScoreBoard(
                score = score,
                timeSeconds = timeSeconds,
                modifier = Modifier.wrapContentWidth(Alignment.End)
            )
        }

        Spacer(Modifier.height(20.dp))

        // --------- СТРОКА СТАТОВ СПРАВА ---------
        StatusRow(
            combo = combo,
            speed = speed,
            rareHits = rareHits,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


// ========================= 🎯 SCORE + TIME =========================
@Composable
fun ScoreBoard(
    score: Int,
    timeSeconds: Int,
    modifier: Modifier = Modifier
) {
    val scoreText = score.coerceAtLeast(0).toString().padStart(4, '0')
    val timeFormatted = remember(timeSeconds) {
        val s = timeSeconds.coerceAtLeast(0)
        "%d:%02d".format(s / 60, s % 60)
    }

    Column(
        modifier = modifier,                   // сюда приходит wrapContentWidth(End)
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.End    // выравниваем дочерние по правому краю
    ) {
        GradientOutlinedTextShort(
            text = "Score: $scoreText",
            fontSize = 24.sp,
            strokeWidth = 7f,
            textAlign = TextAlign.End,         // текст тоже вправо
            horizontalPadding = 0.dp
        )

        GradientOutlinedTextShort(
            text = "Time: $timeFormatted",
            fontSize = 24.sp,
            strokeWidth = 6f,
            textAlign = TextAlign.End,
            horizontalPadding = 0.dp
        )
    }
}

@Composable
fun StatusRow(
    combo: Int,
    speed: Int,
    rareHits: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusTextBlock(
            text = "Combo x$combo",
            modifier = Modifier.weight(1f)
        )

        StatusTextBlock(
            text = "Speed ${speed + 1}",
            modifier = Modifier.weight(1f)
        )

        StatusTextBlock(
            text = "Rare $rareHits",
            modifier = Modifier.weight(1f)
        )
    }
}

// ========================= 🧱 ОБЁРТКА ДЛЯ КРАТКИХ ТЕКСТОВ =========================
@Composable
private fun StatusTextBlock(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        GradientOutlinedTextShort(
            text = text,
            fontSize = 14.sp,
            strokeWidth = 6f,
            textAlign = TextAlign.Center,
            horizontalPadding = 0.dp
        )
    }
}

@Composable
private fun GameBoard(
    chickens: List<GameViewModel.Chicken>,
    effects: List<CellEffect>,
    onTapSlot: (Int) -> Unit,
    onEffectConsumed: (CellEffect) -> Unit,
) {
    val chickenBySlot = remember(chickens) { chickens.associateBy { it.slotIndex } }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        val columns = 3
        val rows = 4
        val spacing = 16.dp
        val cellSize = remember(maxWidth) {
            val totalSpacing = spacing * (columns - 1)
            val available = maxWidth - totalSpacing
            val safeWidth = if (available < 0.dp) 0.dp else available
            safeWidth / columns
        }
        val boardWidth = remember(cellSize) { cellSize * columns + spacing * (columns - 1) }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(boardWidth)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                repeat(rows) { rowIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        repeat(columns) { columnIndex ->
                            val slot = rowIndex * columns + columnIndex
                            val chicken = chickenBySlot[slot]
                            val effect = effects.firstOrNull { it.slot == slot }
                            ChickenCell(
                                chicken = chicken,
                                effect = effect,
                                onTap = { onTapSlot(slot) },
                                onEffectConsumed = onEffectConsumed,
                                modifier = Modifier.size(cellSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChickenCell(
    chicken: GameViewModel.Chicken?,
    effect: CellEffect?,
    onTap: () -> Unit,
    onEffectConsumed: (CellEffect) -> Unit,
    modifier: Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .shadow(12.dp, shape, clip = false, ambientColor = Color(0x33231105), spotColor = Color(0x33231105))
            .clip(shape)
            .background(Color.Transparent)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onTap
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.tile_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val stateRes = remember(chicken) {
            when {
                chicken == null -> R.drawable.tile_house
                chicken.isRare -> R.drawable.tile_house_outside_chicken
                else -> R.drawable.tile_house_with_chicken
            }
        }

        Crossfade(targetState = stateRes, modifier = Modifier.fillMaxSize(), label = "chicken_state") { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (chicken?.isRare == true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(3.dp, Color(0xFFFFE082), shape)
            )
        }

        if (effect != null) {
            EffectOverlay(effect = effect, onEffectConsumed = onEffectConsumed)
        }
    }
}

@Composable
private fun EffectOverlay(effect: CellEffect, onEffectConsumed: (CellEffect) -> Unit) {
    LaunchedEffect(effect.id) {
        delay(320)
        onEffectConsumed(effect)
    }

    when (effect.type) {
        EffectType.Hit -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GradientOutlinedText(
                    text = "POW!",
                    fontSize = 26.sp,
                    gradientColors = listOf(Color(0xFFFFF176), Color(0xFFFF5722))
                )
            }
        }
        EffectType.Rare -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GradientOutlinedText(
                    text = "+100",
                    fontSize = 28.sp,
                    gradientColors = listOf(Color(0xFFFFF9C4), Color(0xFFFFC107))
                )
            }
        }

        EffectType.Miss, EffectType.Escape -> {
            val cloudPainter = painterResource(R.drawable.cloud)

            val alphaAnim by animateFloatAsState(
                targetValue = if (isActive) 1f else 0f,
                animationSpec = tween(400, easing = LinearOutSlowInEasing),
                label = "cloudAlpha"
            )

            val scaleAnim by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.6f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "cloudScale"
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = cloudPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(84.dp)
                        .graphicsLayer(
                            alpha = alphaAnim,
                            scaleX = scaleAnim,
                            scaleY = scaleAnim
                        )
                )
                Image(
                    painter = cloudPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(62.dp)
                        .offset(x = (-16).dp, y = (-10).dp)
                        .graphicsLayer(
                            alpha = alphaAnim * 0.8f,
                            scaleX = scaleAnim * 0.9f,
                            scaleY = scaleAnim * 0.9f
                        )
                )
                Image(
                    painter = cloudPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .offset(x = 14.dp, y = 12.dp)
                        .graphicsLayer(
                            alpha = alphaAnim * 0.65f,
                            scaleX = scaleAnim * 0.8f,
                            scaleY = scaleAnim * 0.8f
                        )
                )
            }
        }
    }
}


@Composable
private fun GameBackground() {
    Image(
        painter = painterResource(id = R.drawable.bg_menu),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}


// ======================= 🐔 ПЕРЕДНЯЯ КУРИЦА =======================
@Composable
private fun BoxScope.GameForegroundChicken(
    onMeasuredHeight: (Dp) -> Unit
) {
    val density = LocalDensity.current

    Image(
        painter = painterResource(id = R.drawable.chicken),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp)
            .fillMaxWidth(0.4f)
            .onGloballyPositioned { coords ->
                val heightDp = with(density) { coords.size.height.toDp() }
                onMeasuredHeight(heightDp)
            },
        contentScale = ContentScale.Fit
    )
}
