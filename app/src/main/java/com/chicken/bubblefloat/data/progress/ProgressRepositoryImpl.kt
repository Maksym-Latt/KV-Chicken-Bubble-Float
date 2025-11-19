package com.chicken.bubblefloat.data.progress

import android.content.SharedPreferences
import com.chicken.bubblefloat.model.SkinIds
import java.util.HashSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences
) : ProgressRepository {

    private companion object {
        const val KEY_EGGS = "progress_eggs"
        const val KEY_SKINS = "progress_skins"
        const val KEY_SELECTED_SKIN = "progress_selected_skin"
    }

    private val _state = MutableStateFlow(loadState())
    override val state = _state.asStateFlow()

    private fun loadState(): ProgressState {
        val eggs = prefs.getInt(KEY_EGGS, 0)
        val storedSkins = prefs.getStringSet(KEY_SKINS, null)?.toSet().orEmpty()
        val owned = if (storedSkins.isEmpty()) setOf(SkinIds.CLASSIC) else storedSkins
        val selected = prefs.getString(KEY_SELECTED_SKIN, null).takeUnless { it.isNullOrBlank() }
            ?.takeIf { owned.contains(it) }
            ?: SkinIds.CLASSIC
        if (!owned.contains(selected)) {
            val fixedOwned = owned + selected
            saveState(eggs, fixedOwned, selected)
            return ProgressState(eggs = eggs, ownedSkins = fixedOwned, selectedSkinId = selected)
        }
        return ProgressState(eggs = eggs, ownedSkins = owned, selectedSkinId = selected)
    }

    private fun saveState(eggs: Int, owned: Set<String>, selected: String) {
        prefs.edit()
            .putInt(KEY_EGGS, eggs)
            .putStringSet(KEY_SKINS, HashSet(owned))
            .putString(KEY_SELECTED_SKIN, selected)
            .apply()
    }

    override fun addEggs(amount: Int) {
        if (amount <= 0) return
        updateState { current ->
            val updated = current.copy(eggs = current.eggs + amount)
            saveState(updated.eggs, updated.ownedSkins, updated.selectedSkinId)
            updated
        }
    }

    override fun tryPurchaseSkin(skinId: String, cost: Int): Boolean {
        if (skinId.isBlank()) return false
        var purchased = false
        updateState { current ->
            if (current.ownedSkins.contains(skinId)) {
                return@updateState current
            }
            if (current.eggs < cost) {
                purchased = false
                return@updateState current
            }
            purchased = true
            val updated = current.copy(
                eggs = current.eggs - cost,
                ownedSkins = current.ownedSkins + skinId
            )
            saveState(updated.eggs, updated.ownedSkins, updated.selectedSkinId)
            updated
        }
        return purchased
    }

    override fun selectSkin(skinId: String) {
        updateState { current ->
            if (!current.ownedSkins.contains(skinId) || current.selectedSkinId == skinId) {
                return@updateState current
            }
            val updated = current.copy(selectedSkinId = skinId)
            saveState(updated.eggs, updated.ownedSkins, updated.selectedSkinId)
            updated
        }
    }

    private fun updateState(block: (ProgressState) -> ProgressState) {
        _state.value = block(_state.value)
    }
}
