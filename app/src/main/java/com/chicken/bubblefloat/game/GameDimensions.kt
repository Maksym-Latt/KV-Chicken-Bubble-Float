package com.chicken.bubblefloat.game

object GameDimensions {

    data class Element(
        val spriteSize: Float,
        val hitboxScale: Float = 0.7f
    )

    val Player = Element(spriteSize = 0.26f, hitboxScale = 0.60f)
    val Thorn = Element(spriteSize = 0.22f)
    val Crow = Element(spriteSize = 0.3f, hitboxScale = 0.65f)
    val Egg = Element(spriteSize = 0.13f, hitboxScale = 0.8f)
    val Bubble = Element(spriteSize = 0.18f, hitboxScale = 0.85f)
}
