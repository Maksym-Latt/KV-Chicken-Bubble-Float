package com.chicken.bubblefloat.game

import android.os.SystemClock
import com.chicken.bubblefloat.game.GameDimensions.Bubble
import com.chicken.bubblefloat.game.GameDimensions.Crow
import com.chicken.bubblefloat.game.GameDimensions.Egg
import com.chicken.bubblefloat.game.GameDimensions.Player
import com.chicken.bubblefloat.game.GameDimensions.Thorn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign
import kotlin.random.Random

class GameEngine(
    private val scope: CoroutineScope,
    private val random: Random = Random(SystemClock.elapsedRealtime())
) {

    data class State(
        val isRunning: Boolean = false,
        val isPaused: Boolean = false,
        val isCompleted: Boolean = false,
        val heightMeters: Float = 0f,
        val eggs: Int = 0,
        val lives: Int = MAX_LIVES,
        val speed: Float = BASE_SPEED,
        val playerX: Float = PLAYER_START_X,
        val invincibleMillis: Long = 0L,
        val obstacles: List<Obstacle> = emptyList(),
        val collectibles: List<Collectible> = emptyList()
    )

    data class Obstacle(
        val id: Int,
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val hitboxScale: Float,
        val type: ObstacleType
    )

    data class Collectible(
        val id: Int,
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val hitboxScale: Float,
        val type: CollectibleType
    )

    enum class ObstacleType { Thorns, Crow }
    enum class CollectibleType { Egg, Bubble }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private var timerJob: Job? = null

    private var isRunning = false
    private var isPaused = false
    private var isCompleted = false

    private var height = 0f
    private var eggs = 0
    private var lives = MAX_LIVES
    private var playerX = PLAYER_START_X
    private var targetX = PLAYER_START_X
    private var speed = BASE_SPEED
    private var invincibleMillis = 0L
    private var damageCooldown = 0L
    private var spawnAccumulator = 0f
    private var bubbleAccumulator = 0f

    private var nextId = 0

    private val activeObstacles = mutableListOf<ActiveObstacle>()
    private val activeCollectibles = mutableListOf<ActiveCollectible>()

    private data class ActiveObstacle(
        val id: Int,
        var x: Float,
        var y: Float,
        val size: Float,
        val hitboxScale: Float,
        val type: ObstacleType
    )

    private data class ActiveCollectible(
        val id: Int,
        var x: Float,
        var y: Float,
        val size: Float,
        val hitboxScale: Float,
        val type: CollectibleType
    )

    fun start() {
        resetState()
        isRunning = true
        isPaused = false
        isCompleted = false
        publishState()

        timerJob?.cancel()
        timerJob = scope.launch {
            var previous = SystemClock.elapsedRealtime()
            while (isActive) {
                delay(TICK_RATE)
                if (!isRunning) continue
                val now = SystemClock.elapsedRealtime()
                val delta = now - previous
                previous = now
                if (isPaused) continue
                tick(delta)
            }
        }
    }

    fun pause() {
        if (!isRunning || isPaused) return
        isPaused = true
        publishState()
    }

    fun resume() {
        if (!isRunning || !isPaused) return
        isPaused = false
        publishState()
    }

    fun stop() {
        isRunning = false
        isPaused = false
        isCompleted = false
        timerJob?.cancel()
        timerJob = null
        resetState()
        publishState()
    }

    fun setPlayerTarget(fraction: Float) {
        val minX = PLAYER_SIZE / 2f
        val maxX = 1f - PLAYER_SIZE / 2f
        val clamped = fraction.coerceIn(minX, maxX)
        targetX = clamped
        if (!isRunning) {
            playerX = clamped
            publishState()
        }
    }

    private fun tick(elapsed: Long) {
        val deltaSeconds = elapsed / 1000f
        val heightGain = speed * deltaSeconds
        height += heightGain
        speed = (speed + SPEED_ACCELERATION * deltaSeconds).coerceAtMost(MAX_SPEED)

        val normalizedShift = heightGain / METERS_PER_SCREEN
        activeObstacles.forEach { it.y -= normalizedShift }
        activeCollectibles.forEach { it.y -= normalizedShift }

        invincibleMillis = (invincibleMillis - elapsed).coerceAtLeast(0L)
        damageCooldown = (damageCooldown - elapsed).coerceAtLeast(0L)

        updatePlayerPosition(deltaSeconds)
        removeOffscreen()
        handleSpawns(heightGain)
        handleCollisions()

        if (lives <= 0) {
            completeGame()
            return
        }

        publishState()
    }

    private fun handleSpawns(heightGain: Float) {
        spawnAccumulator += heightGain
        while (spawnAccumulator >= SPAWN_STEP_METERS) {
            spawnAccumulator -= SPAWN_STEP_METERS
            spawnObstacle()
            if (random.nextFloat() < EGG_ROW_CHANCE) {
                spawnEggCluster()
            }
        }

        bubbleAccumulator += heightGain
        if (bubbleAccumulator >= BUBBLE_STEP_METERS) {
            bubbleAccumulator -= BUBBLE_STEP_METERS
            if (random.nextFloat() < BUBBLE_SPAWN_CHANCE) {
                spawnBubble()
            }
        }
    }

    private fun updatePlayerPosition(deltaSeconds: Float) {
        val diff = targetX - playerX
        if (abs(diff) < 0.001f) {
            playerX = targetX
            return
        }
        val direction = sign(diff)
        val step = PLAYER_MOVE_SPEED * deltaSeconds
        playerX = if (abs(diff) <= step) {
            targetX
        } else {
            (playerX + direction * step).coerceIn(PLAYER_SIZE / 2f, 1f - PLAYER_SIZE / 2f)
        }
    }

    private fun handleCollisions() {
        val playerHalf = PLAYER_SIZE * PLAYER_HITBOX_SCALE / 2f
        val playerRect = Rect(
            left = playerX - playerHalf,
            right = playerX + playerHalf,
            bottom = PLAYER_Y - playerHalf,
            top = PLAYER_Y + playerHalf
        )

        val obstacleIterator = activeObstacles.iterator()
        while (obstacleIterator.hasNext()) {
            val obstacle = obstacleIterator.next()
            if (playerRect.intersects(obstacle.toRect())) {
                obstacleIterator.remove()
                if (invincibleMillis <= 0L && damageCooldown <= 0L) {
                    lives -= 1
                    damageCooldown = HIT_PROTECTION
                    invincibleMillis = HIT_PROTECTION
                }
            }
        }

        val collectibleIterator = activeCollectibles.iterator()
        while (collectibleIterator.hasNext()) {
            val collectible = collectibleIterator.next()
            if (playerRect.intersects(collectible.toRect())) {
                collectibleIterator.remove()
                when (collectible.type) {
                    CollectibleType.Egg -> eggs += 1
                    CollectibleType.Bubble -> invincibleMillis = POWERUP_DURATION
                }
            }
        }
    }

    private fun removeOffscreen() {
        activeObstacles.removeAll { it.y + it.size / 2f < -REMOVAL_MARGIN }
        activeCollectibles.removeAll { it.y + it.size / 2f < -REMOVAL_MARGIN }
    }

    private fun spawnObstacle() {
        val roll = random.nextFloat()
        val type = when {
            roll < 0.55f -> ObstacleType.Thorns
            else -> ObstacleType.Crow
        }
        val config = when (type) {
            ObstacleType.Thorns -> Thorn
            ObstacleType.Crow -> Crow
        }
        val width = config.spriteSize
        val half = width / 2f
        val x = random.nextFloat().coerceIn(half, 1f - half)
        val y = 1.2f + random.nextFloat() * 0.4f
        activeObstacles += ActiveObstacle(
            id = nextId++,
            x = x,
            y = y,
            size = config.spriteSize,
            hitboxScale = config.hitboxScale,
            type = type
        )
    }

    private fun spawnEggCluster() {
        val eggSize = Egg.spriteSize
        val baseX = random.nextFloat().coerceIn(0.15f, 0.85f)
        val count = 3
        repeat(count) { index ->
            val offset = (index - (count - 1) / 2f) * (eggSize + 0.01f)
            activeCollectibles += ActiveCollectible(
                id = nextId++,
                x = (baseX + offset).coerceIn(eggSize / 2f, 1f - eggSize / 2f),
                y = 1.1f + random.nextFloat() * 0.3f,
                size = eggSize,
                hitboxScale = Egg.hitboxScale,
                type = CollectibleType.Egg
            )
        }
    }

    private fun spawnBubble() {
        val config = Bubble
        activeCollectibles += ActiveCollectible(
            id = nextId++,
            x = random.nextFloat().coerceIn(config.spriteSize / 2f, 1f - config.spriteSize / 2f),
            y = 1.1f + random.nextFloat() * 0.3f,
            size = config.spriteSize,
            hitboxScale = config.hitboxScale,
            type = CollectibleType.Bubble
        )
    }

    private fun completeGame() {
        isRunning = false
        isPaused = false
        isCompleted = true
        timerJob?.cancel()
        timerJob = null
        publishState()
    }

    private fun resetState() {
        height = 0f
        eggs = 0
        lives = MAX_LIVES
        playerX = PLAYER_START_X
        targetX = PLAYER_START_X
        speed = BASE_SPEED
        invincibleMillis = 0L
        damageCooldown = 0L
        spawnAccumulator = 0f
        bubbleAccumulator = 0f
        nextId = 0
        activeObstacles.clear()
        activeCollectibles.clear()
    }

    private fun publishState() {
        _state.value = State(
            isRunning = isRunning,
            isPaused = isPaused,
            isCompleted = isCompleted,
            heightMeters = height,
            eggs = eggs,
            lives = lives,
            speed = speed,
            playerX = playerX,
            invincibleMillis = invincibleMillis,
            obstacles = activeObstacles.map { it.toPublic() },
            collectibles = activeCollectibles.map { it.toPublic() }
        )
    }

    private fun ActiveObstacle.toPublic() = Obstacle(
        id = id,
        x = x,
        y = y,
        width = size,
        height = size,
        hitboxScale = hitboxScale,
        type = type
    )

    private fun ActiveCollectible.toPublic() = Collectible(
        id = id,
        x = x,
        y = y,
        width = size,
        height = size,
        hitboxScale = hitboxScale,
        type = type
    )

    private data class Rect(
        val left: Float,
        val right: Float,
        val bottom: Float,
        val top: Float
    ) {
        fun intersects(other: Rect): Boolean {
            return right > other.left &&
                    left < other.right &&
                    top > other.bottom &&
                    bottom < other.top
        }
    }

    private fun ActiveObstacle.toRect(): Rect {
        val half = size * hitboxScale / 2f
        return Rect(
            left = x - half,
            right = x + half,
            bottom = y - half,
            top = y + half
        )
    }

    private fun ActiveCollectible.toRect(): Rect {
        val half = size * hitboxScale / 2f
        return Rect(
            left = x - half,
            right = x + half,
            bottom = y - half,
            top = y + half
        )
    }

    companion object {
        const val MAX_LIVES = 3
        const val PLAYER_Y = 0.2f
        val PLAYER_SIZE = Player.spriteSize
        val PLAYER_HITBOX_SCALE = Player.hitboxScale
        const val POWERUP_DURATION = 5_000L

        private const val PLAYER_START_X = 0.5f
        private const val PLAYER_MOVE_SPEED = 1.5f
        private const val BASE_SPEED = 1.05f
        private const val MAX_SPEED = 3.0f
        private const val SPEED_ACCELERATION = 0.12f
        private const val SPAWN_STEP_METERS = 1.7f
        private const val EGG_ROW_CHANCE = 0.7f
        private const val BUBBLE_STEP_METERS = 9f
        private const val BUBBLE_SPAWN_CHANCE = 0.35f
        private const val METERS_PER_SCREEN = 5.2f
        private const val REMOVAL_MARGIN = 0.25f
        private const val HIT_PROTECTION = 900L
        private const val TICK_RATE = 16L
    }
}
