package com.chicken.bubblefloat.data.settings

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences
) : SettingsRepository {

    private companion object {
        const val KEY_MUSIC_ENABLED = "music_enabled"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_DEBUG_ENABLED = "debug_hitboxes"

        const val KEY_LEGACY_MUSIC = "music_volume"
        const val KEY_LEGACY_SOUND = "sound_volume"
        const val DEF_MUSIC = 70
        const val DEF_SOUND = 80
    }

    override fun isMusicEnabled(): Boolean {
        return if (prefs.contains(KEY_MUSIC_ENABLED)) {
            prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        } else {
            prefs.getInt(KEY_LEGACY_MUSIC, DEF_MUSIC) > 0
        }
    }

    override fun isSoundEnabled(): Boolean {
        return if (prefs.contains(KEY_SOUND_ENABLED)) {
            prefs.getBoolean(KEY_SOUND_ENABLED, true)
        } else {
            prefs.getInt(KEY_LEGACY_SOUND, DEF_SOUND) > 0
        }
    }

    override fun isDebugEnabled(): Boolean = prefs.getBoolean(KEY_DEBUG_ENABLED, false)

    override fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply()
    }

    override fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    override fun setDebugEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEBUG_ENABLED, enabled).apply()
    }
}