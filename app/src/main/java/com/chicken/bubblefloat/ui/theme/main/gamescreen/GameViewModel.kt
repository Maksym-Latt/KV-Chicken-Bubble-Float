package com.chicken.bubblefloat.ui.main.gamescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.bubblefloat.game.GameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    enum class GamePhase { Intro, Running, Paused, Result }

    data class GameUiState(
        val phase: GamePhase = GamePhase.Intro,
        val remainingMillis: Long = GameEngine.TOTAL_TIME,
        val score: Int = 0,
        val chickens: List<Chicken> = emptyList(),
        val combo: Int = 0,
        val bestCombo: Int = 0,
        val speedLevel: Int = 0,
        val rareHits: Int = 0
    ) {
        val remainingSeconds: Int get() = (remainingMillis.coerceAtLeast(0L) / 1000L).toInt()
    }

    data class Chicken(
        val id: Int,
        val slotIndex: Int,
        val type: GameEngine.ChickenType
    ) {
        val isRare: Boolean get() = type == GameEngine.ChickenType.Rare
    }

    sealed class TapOutcome {
        data class Hit(val chickenId: Int, val type: GameEngine.ChickenType, val combo: Int) : TapOutcome()
        object Miss : TapOutcome()
    }

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
                        remainingMillis = engineState.remainingMillis,
                        score = engineState.score,
                        chickens = engineState.chickens.map { it.toUiChicken() },
                        combo = engineState.combo,
                        bestCombo = engineState.bestCombo,
                        speedLevel = engineState.speedLevel,
                        rareHits = engineState.rareHits
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

    fun tap(slotIndex: Int): TapOutcome {
        val result = engine.tap(slotIndex)
        return when (result) {
            is GameEngine.TapResult.Hit -> TapOutcome.Hit(
                chickenId = result.chickenId,
                type = result.type,
                combo = result.combo
            )
            GameEngine.TapResult.Miss -> TapOutcome.Miss
        }
    }

    private fun GameEngine.Chicken.toUiChicken(): Chicken = Chicken(
        id = id,
        slotIndex = slotIndex,
        type = type
    )

    override fun onCleared() {
        engine.stop()
        super.onCleared()
    }
}
