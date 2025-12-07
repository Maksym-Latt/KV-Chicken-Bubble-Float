package com.chicken.bubblefloat.data.progress

import com.chicken.bubblefloat.model.SkinIds
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {
    val state: StateFlow<ProgressState>

    fun addEggs(amount: Int)
    fun tryPurchaseSkin(skinId: String, cost: Int): Boolean
    fun selectSkin(skinId: String)
}

data class ProgressState(
    val eggs: Int = 0,
    val ownedSkins: Set<String> = setOf(SkinIds.CLASSIC),
    val selectedSkinId: String = SkinIds.CLASSIC
)
