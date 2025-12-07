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
class DefaultAudioOrchestrator @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsRepository: SettingsRepository
) : AudioOrchestrator {

    private var bgmAllowed: Boolean = settingsRepository.musicFlag()
    private var fxAllowed: Boolean = settingsRepository.effectsFlag()

    private var activeTheme: MusicTrack? = null
    private var themePlayer: MediaPlayer? = null
    private val fxBank: SoundPool
    private val cueHandles: Map<SoundCue, Int>
    private val preparedCues = mutableSetOf<Int>()

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        fxBank = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .setMaxStreams(4)
            .build()
        fxBank.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                preparedCues += sampleId
            } else {
                preparedCues -= sampleId
            }
        }
        cueHandles = SoundCue.entries.associateWith { cue ->
            fxBank.load(context, cue.resId, 1)
        }
    }

    // ---------------------- PUBLIC API ----------------------

    override fun cueMenuTheme() {
        playMusic(MusicTrack.MenuTheme)
    }

    override fun cueRunTheme() {
        playMusic(MusicTrack.GameLoop)
    }

    override fun silenceThemes() {
        themePlayer?.run {
            stop()
            release()
        }
        themePlayer = null
        activeTheme = null
    }

    override fun suspendThemes() {
        themePlayer?.takeIf { it.isPlaying }?.pause()
    }

    override fun resumeThemes() {
        if (!bgmAllowed) return
        themePlayer?.let { player ->
            activeTheme?.let { track -> player.setVolume(track.normalizedMusicVolume(), track.normalizedMusicVolume()) }
            player.start()
        }
    }

    override fun setMusicActive(enabled: Boolean) {
        bgmAllowed = enabled
        activeTheme?.let { track ->
            val adjusted = track.normalizedMusicVolume()
            themePlayer?.setVolume(adjusted, adjusted)
        }
        if (!enabled) {
            themePlayer?.pause()
        } else {
            resumeThemes()
        }
    }

    override fun setEffectsActive(enabled: Boolean) {
        fxAllowed = enabled
    }

    override fun triggerVictoryTone() {
        playSound(SoundCue.VictoryFanfare)
    }

    override fun triggerCollisionTone() {
        playSound(SoundCue.ChickenHit)
    }

    override fun triggerLegendaryTone() {
        playSound(SoundCue.RareChicken)
    }

    override fun triggerAcquiredTone() {
        playSound(SoundCue.ChickenPickup)
    }

    // ---------------------- INTERNAL IMPLEMENTATION ----------------------

    private fun playMusic(track: MusicTrack) {
        if (activeTheme == track && themePlayer != null) {
            val adjusted = track.normalizedMusicVolume()
            themePlayer?.setVolume(adjusted, adjusted)
            if (bgmAllowed && themePlayer?.isPlaying != true) {
                themePlayer?.start()
            } else if (!bgmAllowed && themePlayer?.isPlaying == true) {
                themePlayer?.pause()
            }
            return
        }

        silenceThemes()

        themePlayer = MediaPlayer.create(context, track.resId).apply {
            isLooping = true
            val adjusted = track.normalizedMusicVolume()
            setVolume(adjusted, adjusted)
            setOnCompletionListener(null)
            if (bgmAllowed) {
                start()
            }
        }
        activeTheme = track
    }

    private fun playSound(effect: SoundCue) {
        if (!fxAllowed) return
        val soundId = cueHandles[effect] ?: return
        if (soundId !in preparedCues) return
        val adjusted = effect.normalizedSoundVolume()
        if (adjusted <= 0f) return
        fxBank.play(soundId, adjusted, adjusted, 1, 0, 1f)
    }

    private fun MusicTrack.normalizedMusicVolume(): Float {
        val base = if (bgmAllowed) 1f else 0f
        return base.adjustWith(MUSIC_NORMALIZATION[this])
    }

    private fun SoundCue.normalizedSoundVolume(): Float {
        val base = if (fxAllowed) 1f else 0f
        return base.adjustWith(SOUND_NORMALIZATION[this])
    }

    private fun Float.adjustWith(gain: Float?): Float = (this * (gain ?: 1f)).coerceIn(0f, 1f)

    // ---------------------- ENUMS ПОД res/raw ----------------------

    private enum class MusicTrack(@RawRes val resId: Int) {
        MenuTheme(R.raw.menu_theme_bf),
        GameLoop(R.raw.game_loop_bf)
    }

    private enum class SoundCue(@RawRes val resId: Int) {
        VictoryFanfare(R.raw.sfx_vc_fanfare_bf),

        ChickenHit(R.raw.sfx_ch_hit_bf),

        RareChicken(R.raw.sfx_rare_ch_bubble_bf),

        ChickenPickup(R.raw.sfx_ch_pickup_bf)
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
