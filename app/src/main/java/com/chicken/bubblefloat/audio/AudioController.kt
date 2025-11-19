package com.chicken.bubblefloat.audio

interface AudioController {
    fun playMenuMusic()
    fun playGameMusic()
    fun stopMusic()
    fun pauseMusic()
    fun resumeMusic()

    fun setMusicVolume(percent: Int)
    fun setSoundVolume(percent: Int)

    fun playGameWin()
    fun playChickenHit()
    fun playRareChicken()
    fun playChickenEscape()
}
