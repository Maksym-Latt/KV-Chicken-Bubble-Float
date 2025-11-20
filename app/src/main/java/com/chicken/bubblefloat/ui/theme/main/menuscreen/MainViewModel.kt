package com.chicken.bubblefloat.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.bubblefloat.data.progress.ProgressRepository
import com.chicken.bubblefloat.model.SkinIds
import com.chicken.bubblefloat.ui.main.locker.ChickenSkin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RunSummary(
    val heightMeters: Int,
    val eggs: Int
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    enum class Screen { Menu, Game }

    data class UiState(
        val screen: Screen = Screen.Menu,
        val lastRun: RunSummary? = null,
        val bestHeight: Int = 0,
        val bestEggs: Int = 0,
        val totalEggs: Int = 0,
        val ownedSkins: Set<String> = setOf(SkinIds.CLASSIC),
        val selectedSkinId: String = SkinIds.CLASSIC
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.state.collectLatest { progress ->
                _ui.update { current ->
                    current.copy(
                        totalEggs = progress.eggs,
                        ownedSkins = progress.ownedSkins,
                        selectedSkinId = progress.selectedSkinId
                    )
                }
            }
        }
    }

    fun startGame() {
        _ui.update { it.copy(screen = Screen.Game) }
    }

    fun backToMenu(result: RunSummary) {
        recordRun(result)
        _ui.update { current -> current.copy(screen = Screen.Menu) }
    }

    fun backToMenu() {
        _ui.update { it.copy(screen = Screen.Menu) }
    }

    fun recordRun(result: RunSummary) {
        progressRepository.addEggs(result.eggs)
        _ui.update { current ->
            current.copy(
                lastRun = result,
                bestHeight = maxOf(current.bestHeight, result.heightMeters),
                bestEggs = maxOf(current.bestEggs, result.eggs)
            )
        }
    }

    fun purchaseSkin(skin: ChickenSkin): Boolean {
        return progressRepository.tryPurchaseSkin(skin.id, skin.price)
    }

    fun selectSkin(skinId: String) {
        progressRepository.selectSkin(skinId)
    }
}
