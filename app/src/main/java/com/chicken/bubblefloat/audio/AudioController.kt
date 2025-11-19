package com.chicken.bubblefloat.audio

interface AudioController {
    fun playMenuMusic()
    fun playGameMusic()
    fun stopMusic()
    fun pauseMusic()
    fun resumeMusic()

    fun setMusicEnabled(enabled: Boolean)
    fun setSoundEnabled(enabled: Boolean)

    fun playGameWin()
    fun playChickenHit()
    fun playRareChicken()
    fun playChickenPickup()
}
