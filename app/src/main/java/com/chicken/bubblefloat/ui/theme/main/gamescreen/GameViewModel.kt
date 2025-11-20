package com.chicken.bubblefloat.ui.main.gamescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.bubblefloat.game.GameEngine
import com.chicken.bubblefloat.ui.main.menuscreen.RunSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class GameViewModel : ViewModel() {

    enum class GamePhase { Intro, Running, Paused, Result }

    data class GameUiState(
        val phase: GamePhase = GamePhase.Intro,
        val heightMeters: Float = 0f,
        val eggs: Int = 0,
        val lives: Int = GameEngine.MAX_LIVES,
        val playerX: Float = 0.5f,
        val invincibleMillis: Long = 0L,
        val obstacles: List<Obstacle> = emptyList(),
        val collectibles: List<Collectible> = emptyList()
    ) {
        val heightRounded: Int get() = heightMeters.roundToInt()
        val invincibilityProgress: Float
            get() = (invincibleMillis / GameEngine.POWERUP_DURATION.toFloat()).coerceIn(0f, 1f)
    }

    data class Obstacle(
        val id: Int,
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val hitboxSize: Float,
        val type: GameEngine.ObstacleType
    )

    data class Collectible(
        val id: Int,
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val hitboxSize: Float,
        val type: GameEngine.CollectibleType
    )

    private val engine = GameEngine(viewModelScope)
    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            engine.state.collectLatest { engineState ->
                _state.update { current ->
                    val phase = when {
                        engineState.isCompleted -> GamePhase.Result
                        engineState.isPaused -> GamePhase.Paused
                        engineState.isRunning -> GamePhase.Running
                        else -> GamePhase.Intro
                    }
                    current.copy(
                        phase = phase,
                        heightMeters = engineState.heightMeters,
                        eggs = engineState.eggs,
                        lives = engineState.lives,
                        playerX = engineState.playerX,
                        invincibleMillis = engineState.invincibleMillis,
                        obstacles = engineState.obstacles.map { it.toUiObstacle() },
                        collectibles = engineState.collectibles.map { it.toUiCollectible() }
                    )
                }
            }
        }
    }

    fun startGame() {
        if (_state.value.phase == GamePhase.Running) return
        engine.start()
    }

    fun pause() {
        if (_state.value.phase != GamePhase.Running) return
        engine.pause()
    }

    fun pauseAndOpenSettings() {
        pause()
    }

    fun resume() {
        if (_state.value.phase != GamePhase.Paused) return
        engine.resume()
    }

    fun retry() {
        engine.start()
    }

    fun exitToMenu() {
        engine.stop()
        _state.value = GameUiState()
    }

    fun movePlayer(fraction: Float) {
        engine.setPlayerTarget(fraction)
    }

    fun currentSummary(): RunSummary {
        val ui = _state.value
        return RunSummary(heightMeters = ui.heightRounded, eggs = ui.eggs)
    }

    private fun GameEngine.Obstacle.toUiObstacle() = Obstacle(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
        hitboxSize = hitboxSize,
        type = type
    )

    private fun GameEngine.Collectible.toUiCollectible() = Collectible(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
        hitboxSize = hitboxSize,
        type = type
    )

    override fun onCleared() {
        engine.stop()
        super.onCleared()
    }
}
