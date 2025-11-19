package com.chicken.bubblefloat.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import com.chicken.bubblefloat.R
import javax.inject.Inject
import javax.inject.Singleton
import com.chicken.bubblefloat.data.settings.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class DefaultAudioController @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsRepository: SettingsRepository
) : AudioController {

    private var musicEnabled: Boolean = settingsRepository.isMusicEnabled()
    private var soundEnabled: Boolean = settingsRepository.isSoundEnabled()

    private var currentMusic: MusicTrack? = null
    private var musicPlayer: MediaPlayer? = null
    private val soundPool: SoundPool
    private val soundIds: Map<SoundCue, Int>
    private val loadedSoundIds = mutableSetOf<Int>()

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .setMaxStreams(4)
            .build()
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds += sampleId
            } else {
                loadedSoundIds -= sampleId
            }
        }
        soundIds = SoundCue.entries.associateWith { cue ->
            soundPool.load(context, cue.resId, 1)
        }
    }

    // ---------------------- PUBLIC API ----------------------

    override fun playMenuMusic() {
        playMusic(MusicTrack.MenuTheme)
    }

    override fun playGameMusic() {
        playMusic(MusicTrack.GameLoop)
    }

    override fun stopMusic() {
        musicPlayer?.run {
            stop()
            release()
        }
        musicPlayer = null
        currentMusic = null
    }

    override fun pauseMusic() {
        musicPlayer?.takeIf { it.isPlaying }?.pause()
    }

    override fun resumeMusic() {
        if (!musicEnabled) return
        musicPlayer?.let { player ->
            currentMusic?.let { track -> player.setVolume(track.normalizedMusicVolume(), track.normalizedMusicVolume()) }
            player.start()
        }
    }

    override fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        currentMusic?.let { track ->
            val adjusted = track.normalizedMusicVolume()
            musicPlayer?.setVolume(adjusted, adjusted)
        }
        if (!enabled) {
            musicPlayer?.pause()
        } else {
            resumeMusic()
        }
    }

    override fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    override fun playGameWin() {
        playSound(SoundCue.VictoryFanfare)
    }

    override fun playChickenHit() {
        playSound(SoundCue.ChickenHit)
    }

    override fun playRareChicken() {
        playSound(SoundCue.RareChicken)
    }

    override fun playChickenPickup() {
        playSound(SoundCue.ChickenPickup)
    }

    // ---------------------- INTERNAL IMPLEMENTATION ----------------------

    private fun playMusic(track: MusicTrack) {
        if (currentMusic == track && musicPlayer != null) {
            val adjusted = track.normalizedMusicVolume()
            musicPlayer?.setVolume(adjusted, adjusted)
            if (musicEnabled && musicPlayer?.isPlaying != true) {
                musicPlayer?.start()
            } else if (!musicEnabled && musicPlayer?.isPlaying == true) {
                musicPlayer?.pause()
            }
            return
        }

        stopMusic()

        musicPlayer = MediaPlayer.create(context, track.resId).apply {
            isLooping = true
            val adjusted = track.normalizedMusicVolume()
            setVolume(adjusted, adjusted)
            setOnCompletionListener(null)
            if (musicEnabled) {
                start()
            }
        }
        currentMusic = track
    }

    private fun playSound(effect: SoundCue) {
        if (!soundEnabled) return
        val soundId = soundIds[effect] ?: return
        if (soundId !in loadedSoundIds) return
        val adjusted = effect.normalizedSoundVolume()
        if (adjusted <= 0f) return
        soundPool.play(soundId, adjusted, adjusted, 1, 0, 1f)
    }

    private fun MusicTrack.normalizedMusicVolume(): Float {
        val base = if (musicEnabled) 1f else 0f
        return base.adjustWith(MUSIC_NORMALIZATION[this])
    }

    private fun SoundCue.normalizedSoundVolume(): Float {
        val base = if (soundEnabled) 1f else 0f
        return base.adjustWith(SOUND_NORMALIZATION[this])
    }

    private fun Float.adjustWith(gain: Float?): Float = (this * (gain ?: 1f)).coerceIn(0f, 1f)

    // ---------------------- ENUMS ПОД res/raw ----------------------

    private enum class MusicTrack(@RawRes val resId: Int) {
        // res/raw/menu_theme.mp3
        MenuTheme(R.raw.menu_theme),

        // res/raw/game_loop.mp3
        GameLoop(R.raw.game_loop)
    }

    private enum class SoundCue(@RawRes val resId: Int) {
        VictoryFanfare(R.raw.sfx_victory_fanfare),

        ChickenHit(R.raw.sfx_chicken_hit),

        RareChicken(R.raw.sfx_rare_chicken_bubble),

        ChickenPickup(R.raw.sfx_chicken_pickup)
    }

    companion object {
        private val MUSIC_NORMALIZATION = mapOf(
            MusicTrack.MenuTheme to 0.8f,
            MusicTrack.GameLoop to 0.75f,
        )

        private val SOUND_NORMALIZATION = mapOf(
            SoundCue.VictoryFanfare to 0.9f,
            SoundCue.ChickenHit to 0.9f,
            SoundCue.RareChicken to 0.9f,
            SoundCue.ChickenPickup to 0.9f,
        )
    }
}
