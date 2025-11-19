package com.chicken.bubblefloat.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    enum class Screen { Menu, Game }

    data class UiState(
        val screen: Screen = Screen.Menu,
        val lastScore: Int = 0,
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun startGame() {
        _ui.update { it.copy(screen = Screen.Game) }
    }

    fun backToMenu(score: Int) {
        _ui.update { UiState(screen = Screen.Menu, lastScore = score.coerceAtLeast(0)) }
    }

    fun backToMenu() {
        backToMenu(_ui.value.lastScore)
    }
}
