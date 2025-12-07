package com.chicken.bubblefloat.ui.main.locker

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.chicken.bubblefloat.R
import com.chicken.bubblefloat.model.SkinIds

@Immutable
data class ChickenSkin(
    val id: String,
    val title: String,
    val price: Int,
    @DrawableRes val spriteRes: Int,
    @DrawableRes val bubbleRes: Int,
    val titleGradient: List<Color>,
    val accentColor: Color,
    val priceColor: Color
)

object ChickenSkins {
    val all: List<ChickenSkin> = listOf(
        ChickenSkin(
            id = SkinIds.CLASSIC,
            title = "CHICKEN",
            price = 0,
            spriteRes = R.drawable.chicken_1,
            bubbleRes = R.drawable.chicken_1_happy,
            titleGradient = listOf(Color(0xFFFFF9C4), Color(0xFFFFCA28)),
            accentColor = Color(0xFF00C2B8),
            priceColor = Color(0xFF00B894)
        ),
        ChickenSkin(
            id = SkinIds.TEXTILE,
            title = "TEXTILE\nCHICKEN",
            price = 2400,
            spriteRes = R.drawable.chicken_2,
            bubbleRes = R.drawable.chicken_2_happy,
            titleGradient = listOf(Color(0xFFFAE0FF), Color(0xFF9DD0FF)),
            accentColor = Color(0xFFFD6CA1),
            priceColor = Color(0xFFFA5F8F)
        ),
        ChickenSkin(
            id = SkinIds.GALAXY,
            title = "GALAXY\nCHICKEN",
            price = 6700,
            spriteRes = R.drawable.chicken_3,
            bubbleRes = R.drawable.chicken_3_happy,
            titleGradient = listOf(Color(0xFFCCB8FF), Color(0xFF7A5BFF)),
            accentColor = Color(0xFF5C4CFF),
            priceColor = Color(0xFF5DD65A)
        )
    )

    fun findById(id: String): ChickenSkin {
        return all.firstOrNull { it.id == id } ?: all.first()
    }
}
