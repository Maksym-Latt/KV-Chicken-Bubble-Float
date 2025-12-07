package com.chicken.bubblefloat.ui.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.bubblefloat.audio.AudioOrchestrator
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
    private val audio: AudioOrchestrator
) : ViewModel() {

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui

    init {
        val musicEnabled = repo.musicFlag()
        val soundEnabled = repo.effectsFlag()
        val debugEnabled = repo.diagnosticsFlag()
        _ui.value = SettingsUiState(
            musicEnabled = musicEnabled,
            soundEnabled = soundEnabled,
            debugHitboxesEnabled = debugEnabled
        )
        audio.setMusicActive(musicEnabled)
        audio.setEffectsActive(soundEnabled)
    }

    fun setMusicEnabled(enabled: Boolean) {
        _ui.update { it.copy(musicEnabled = enabled) }
        viewModelScope.launch { repo.applyMusicFlag(enabled) }
        audio.setMusicActive(enabled)
    }

    fun setSoundEnabled(enabled: Boolean) {
        _ui.update { it.copy(soundEnabled = enabled) }
        viewModelScope.launch { repo.applyEffectsFlag(enabled) }
        audio.setEffectsActive(enabled)
    }

    fun setDebugHitboxes(enabled: Boolean) {
        _ui.update { it.copy(debugHitboxesEnabled = enabled) }
        viewModelScope.launch { repo.applyDiagnosticsFlag(enabled) }
    }
}
