package com.chicken.bubblefloat.ui.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.bubblefloat.audio.AudioController
import com.chicken.bubblefloat.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    private val audio: AudioController
) : ViewModel() {

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui

    init {
        val musicEnabled = repo.isMusicEnabled()
        val soundEnabled = repo.isSoundEnabled()
        val debugEnabled = repo.isDebugEnabled()
        _ui.value = SettingsUiState(
            musicEnabled = musicEnabled,
            soundEnabled = soundEnabled,
            debugHitboxesEnabled = debugEnabled
        )
        audio.setMusicEnabled(musicEnabled)
        audio.setSoundEnabled(soundEnabled)
    }

    fun setMusicEnabled(enabled: Boolean) {
        _ui.update { it.copy(musicEnabled = enabled) }
        viewModelScope.launch { repo.setMusicEnabled(enabled) }
        audio.setMusicEnabled(enabled)
    }

    fun setSoundEnabled(enabled: Boolean) {
        _ui.update { it.copy(soundEnabled = enabled) }
        viewModelScope.launch { repo.setSoundEnabled(enabled) }
        audio.setSoundEnabled(enabled)
    }

    fun setDebugHitboxes(enabled: Boolean) {
        _ui.update { it.copy(debugHitboxesEnabled = enabled) }
        viewModelScope.launch { repo.setDebugEnabled(enabled) }
    }
}
