package com.chicken.bubblefloat.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RunSummary(
    val heightMeters: Int,
    val bubbles: Int
)

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    enum class Screen { Menu, Game }

    data class UiState(
        val screen: Screen = Screen.Menu,
        val lastRun: RunSummary? = null,
        val bestHeight: Int = 0,
        val bestBubbles: Int = 0
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun startGame() {
        _ui.update { it.copy(screen = Screen.Game) }
    }

    fun backToMenu(result: RunSummary) {
        _ui.update { current ->
            current.copy(
                screen = Screen.Menu,
                lastRun = result,
                bestHeight = maxOf(current.bestHeight, result.heightMeters),
                bestBubbles = maxOf(current.bestBubbles, result.bubbles)
            )
        }
    }

    fun backToMenu() {
        _ui.update { it.copy(screen = Screen.Menu) }
    }
}
