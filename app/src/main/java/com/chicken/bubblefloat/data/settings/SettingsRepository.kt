package com.chicken.bubblefloat.data.settings

interface SettingsRepository {
    fun musicFlag(): Boolean
    fun effectsFlag(): Boolean
    fun diagnosticsFlag(): Boolean

    fun applyMusicFlag(enabled: Boolean)
    fun applyEffectsFlag(enabled: Boolean)
    fun applyDiagnosticsFlag(enabled: Boolean)
}