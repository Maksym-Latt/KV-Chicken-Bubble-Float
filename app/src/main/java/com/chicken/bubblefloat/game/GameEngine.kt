package com.chicken.bubblefloat.game

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameEngine(
    private val scope: CoroutineScope,
    private val random: Random = Random(SystemClock.elapsedRealtime())
) {

    data class State(
        val isRunning: Boolean = false,
        val isPaused: Boolean = false,
        val isCompleted: Boolean = false,
        val remainingMillis: Long = TOTAL_TIME,
        val score: Int = 0,
        val combo: Int = 0,
        val bestCombo: Int = 0,
        val speedLevel: Int = 0,
        val chickens: List<Chicken> = emptyList(),
        val rareHits: Int = 0
    )

    data class Chicken(
        val id: Int,
        val slotIndex: Int,
        val type: ChickenType
    )

    enum class ChickenType { Resident, Rare }

    sealed class TapResult {
        data class Hit(val chickenId: Int, val type: ChickenType, val combo: Int) : TapResult()
        object Miss : TapResult()
    }

    private data class ActiveChicken(
        val id: Int,
        val slotIndex: Int,
        val type: ChickenType,
        val lifetime: Long,
        var remaining: Long
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var spawnJob: Job? = null

    private var timeLeft = TOTAL_TIME
    private var combo = 0
    private var bestCombo = 0
    private var speedLevel = 0
    private var rareHits = 0
    private var nextId = 0
    private var isPaused = false
    private var isRunning = false

    private val activeChickens = mutableListOf<ActiveChicken>()

    fun start() {
        resetState()
        isRunning = true
        isPaused = false
        _state.value = State(
            isRunning = true,
            isPaused = false,
            isCompleted = false,
            remainingMillis = timeLeft,
            score = 0,
            combo = 0,
            bestCombo = 0,
            speedLevel = 0,
            chickens = emptyList(),
            rareHits = 0
        )

        timerJob?.cancel()
        spawnJob?.cancel()

        timerJob = scope.launch {
            var previous = SystemClock.elapsedRealtime()
            while (isActive) {
                delay(TICK_RATE)
                val now = SystemClock.elapsedRealtime()
                if (!isRunning || isPaused) {
                    previous = now
                    continue
                }
                val delta = now - previous
                previous = now
                tick(delta)
            }
        }

        spawnJob = scope.launch {
            while (isActive) {
                delay(currentSpawnDelay())
                if (!isRunning || isPaused) continue
                spawnChicken()
            }
        }
    }

    fun pause() {
        if (!isRunning || isPaused) return
        isPaused = true
        _state.update { it.copy(isPaused = true) }
    }

    fun resume() {
        if (!isRunning || !isPaused) return
        isPaused = false
        _state.update { it.copy(isPaused = false) }
    }

    fun stop() {
        isRunning = false
        isPaused = false
        timerJob?.cancel()
        spawnJob?.cancel()
        timerJob = null
        spawnJob = null
        activeChickens.clear()
        _state.value = State()
    }

    fun tap(slotIndex: Int): TapResult {
        val current = _state.value
        if (!current.isRunning || current.isPaused || current.isCompleted) return TapResult.Miss

        val idx = activeChickens.indexOfFirst { it.slotIndex == slotIndex }
        if (idx == -1) {
            combo = 0
            timeLeft = (timeLeft - MISS_TIME_PENALTY).coerceAtLeast(0L)

            if (timeLeft == 0L) {
                completeGame()
                return TapResult.Miss
            }

            _state.update {
                it.copy(
                    remainingMillis = timeLeft,
                    combo = 0,
                    bestCombo = bestCombo,
                    speedLevel = speedLevel,
                    chickens = activeChickens.map { chicken -> chicken.toUi() }
                )
            }
            return TapResult.Miss
        }

        val chicken = activeChickens[idx]
        activeChickens.removeAt(idx)

        combo += 1
        if (combo > bestCombo) bestCombo = combo
        if (combo % SPEED_COMBO_STEP == 0) speedLevel = (speedLevel + 1).coerceAtMost(MAX_SPEED_LEVEL)

        val scoreGain = if (chicken.type == ChickenType.Rare) {
            RARE_SCORE_REWARD
        } else {
            BASE_SCORE_REWARD + speedLevel * SPEED_SCORE_BONUS
        }

        if (chicken.type == ChickenType.Rare) {
            rareHits += 1
        }

        _state.update {
            it.copy(
                score = it.score + scoreGain,
                combo = combo,
                bestCombo = bestCombo,
                speedLevel = speedLevel,
                chickens = activeChickens.map { c -> c.toUi() },
                rareHits = rareHits
            )
        }

        return TapResult.Hit(chickenId = chicken.id, type = chicken.type, combo = combo)
    }

    private fun tick(elapsed: Long) {
        if (!isRunning || isPaused) return
        if (timeLeft <= 0L) return

        timeLeft = (timeLeft - elapsed).coerceAtLeast(0L)
        val hadEscape = updateChickens(elapsed)
        if (hadEscape) {
            combo = 0
        }

        if (timeLeft <= 0L) {
            completeGame()
        } else {
            _state.update {
                it.copy(
                    remainingMillis = timeLeft,
                    combo = combo,
                    bestCombo = bestCombo,
                    speedLevel = speedLevel,
                    chickens = activeChickens.map { chicken -> chicken.toUi() }
                )
            }
        }
    }

    private fun updateChickens(elapsed: Long): Boolean {
        var hadEscape = false
        val iterator = activeChickens.iterator()
        while (iterator.hasNext()) {
            val chicken = iterator.next()
            chicken.remaining -= elapsed
            if (chicken.remaining <= 0L) {
                iterator.remove()
                hadEscape = true
            }
        }
        if (hadEscape) {
            _state.update {
                it.copy(
                    chickens = activeChickens.map { chicken -> chicken.toUi() },
                    combo = 0
                )
            }
        }
        return hadEscape
    }

    private fun spawnChicken() {
        val availableSlots = (0 until GRID_SIZE).filter { slot ->
            activeChickens.none { it.slotIndex == slot }
        }
        if (availableSlots.isEmpty()) return

        val slot = availableSlots.random(random)
        val type = if (random.nextFloat() < RARE_CHANCE) ChickenType.Rare else ChickenType.Resident
        val lifetime = if (type == ChickenType.Rare) RARE_VISIBLE_TIME else currentVisibleDuration()
        val chicken = ActiveChicken(
            id = nextId++,
            slotIndex = slot,
            type = type,
            lifetime = lifetime,
            remaining = lifetime
        )
        activeChickens += chicken
        _state.update {
            it.copy(chickens = activeChickens.map { c -> c.toUi() })
        }
    }

    private fun ActiveChicken.toUi(): Chicken = Chicken(
        id = id,
        slotIndex = slotIndex,
        type = type
    )

    private fun completeGame() {
        isRunning = false
        timerJob?.cancel()
        spawnJob?.cancel()
        timerJob = null
        spawnJob = null
        activeChickens.clear()
        _state.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                isCompleted = true,
                remainingMillis = 0L,
                chickens = emptyList()
            )
        }
    }

    private fun resetState() {
        timerJob?.cancel()
        spawnJob?.cancel()
        activeChickens.clear()
        timeLeft = TOTAL_TIME
        combo = 0
        bestCombo = 0
        speedLevel = 0
        rareHits = 0
        nextId = 0
    }

    private fun currentSpawnDelay(): Long {
        val reduction = speedLevel * SPAWN_REDUCTION_STEP
        return (BASE_SPAWN_DELAY - reduction).coerceAtLeast(MIN_SPAWN_DELAY)
    }

    private fun currentVisibleDuration(): Long {
        val reduction = speedLevel * VISIBLE_REDUCTION_STEP
        return (BASE_VISIBLE_TIME - reduction).coerceAtLeast(MIN_VISIBLE_TIME)
    }

    companion object {
        const val TOTAL_TIME: Long = 60_000L

        private const val GRID_SIZE = 12
        private const val TICK_RATE = 16L

        private const val BASE_SPAWN_DELAY = 1_050L
        private const val MIN_SPAWN_DELAY = 320L
        private const val SPAWN_REDUCTION_STEP = 70L

        private const val BASE_VISIBLE_TIME = 1_150L
        private const val MIN_VISIBLE_TIME = 420L
        private const val VISIBLE_REDUCTION_STEP = 55L
        private const val RARE_VISIBLE_TIME = 1_400L

        private const val MISS_TIME_PENALTY = 5_000L

        private const val BASE_SCORE_REWARD = 60
        private const val SPEED_SCORE_BONUS = 12
        private const val RARE_SCORE_REWARD = 100

        private const val SPEED_COMBO_STEP = 5
        private const val MAX_SPEED_LEVEL = 10

        private const val RARE_CHANCE = 0.08f
    }
}
