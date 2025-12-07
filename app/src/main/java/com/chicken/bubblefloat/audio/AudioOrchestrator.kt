package com.chicken.bubblefloat.audio

interface AudioOrchestrator {
    fun cueMenuTheme()
    fun cueRunTheme()
    fun silenceThemes()
    fun suspendThemes()
    fun resumeThemes()

    fun setMusicActive(enabled: Boolean)
    fun setEffectsActive(enabled: Boolean)

    fun triggerVictoryTone()
    fun triggerCollisionTone()
    fun triggerLegendaryTone()
    fun triggerAcquiredTone()
}
