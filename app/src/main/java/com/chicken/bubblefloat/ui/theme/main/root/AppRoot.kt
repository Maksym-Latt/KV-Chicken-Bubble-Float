package com.chicken.bubblefloat.ui.main.root

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chicken.bubblefloat.audio.rememberAudioController
import com.chicken.bubblefloat.ui.main.gamescreen.GameScreen
import com.chicken.bubblefloat.ui.main.menuscreen.MainViewModel
import com.chicken.bubblefloat.ui.main.menuscreen.MenuScreen
import com.chicken.bubblefloat.ui.main.menuscreen.overlay.SettingsOverlay
import com.chicken.bubblefloat.ui.main.menuscreen.overlay.LockerOverlay

@Composable
fun AppRoot(
    vm: MainViewModel = hiltViewModel(),
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    var showMenuSettings by rememberSaveable { mutableStateOf(false) }
    var showLocker by rememberSaveable { mutableStateOf(false) }
    val audio = rememberAudioController()

    LaunchedEffect(ui.screen) {
        if (ui.screen != MainViewModel.Screen.Menu) {
            showMenuSettings = false
            showLocker = false
        }
        when (ui.screen) {
            MainViewModel.Screen.Menu -> audio.playMenuMusic()
            MainViewModel.Screen.Game -> audio.playGameMusic()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4D9))
    ) {
        Crossfade(targetState = ui.screen, label = "root_screen") { screen ->
            when (screen) {
                MainViewModel.Screen.Menu ->
                    Box(Modifier.fillMaxSize()) {
                        MenuScreen(
                            onStartGame = {
                                showMenuSettings = false
                                showLocker = false
                                vm.startGame()
                            },
                            selectedSkinId = ui.selectedSkinId,
                            onOpenSettings = { showMenuSettings = true },
                            onOpenLocker = { showLocker = true }
                        )

                        if (showMenuSettings) {
                            SettingsOverlay(
                                onClose = { showMenuSettings = false },
                            )
                        }

                        if (showLocker) {
                            LockerOverlay(
                                eggs = ui.totalEggs,
                                selectedSkinId = ui.selectedSkinId,
                                ownedSkins = ui.ownedSkins,
                                onSelectSkin = { skinId ->
                                    vm.selectSkin(skinId)
                                },
                                onBuySkin = { skin -> vm.purchaseSkin(skin) },
                                onClose = { showLocker = false }
                            )
                        }
                    }

                MainViewModel.Screen.Game ->
                    GameScreen(
                        onExitToMenu = vm::backToMenu,
                        selectedSkinId = ui.selectedSkinId
                    )
            }
        }
    }
}
