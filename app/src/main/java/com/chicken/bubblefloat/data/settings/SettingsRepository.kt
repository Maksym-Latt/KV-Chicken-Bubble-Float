package com.chicken.bubblefloat.data.settings

interface SettingsRepository {
    fun isMusicEnabled(): Boolean
    fun isSoundEnabled(): Boolean
    fun isDebugEnabled(): Boolean

    fun setMusicEnabled(enabled: Boolean)
    fun setSoundEnabled(enabled: Boolean)
    fun setDebugEnabled(enabled: Boolean)
}