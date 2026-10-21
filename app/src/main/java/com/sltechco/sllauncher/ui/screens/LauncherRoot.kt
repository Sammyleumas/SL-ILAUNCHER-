package com.sltechco.sllauncher.ui.screens

import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sltechco.sllauncher.appearance.theme.SLTheme
import com.sltechco.sllauncher.appearance.theme.SLTokens
import com.sltechco.sllauncher.core.common.ContactActions
import com.sltechco.sllauncher.launcher.discovery.AppLauncher

@Composable
fun LauncherRoot(vm: LauncherViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // Reduced motion: honour the system "remove animations" setting.
    val reducedMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }

    SLTheme(state.themeMode) {
        Box(Modifier.fillMaxSize().background(SLTokens.colors.background.copy(alpha = 0.92f))) {
            HomeScreen(
                themeMode = state.themeMode,
                onThemeChange = vm::setTheme,
                onOpenDrawer = vm::openDrawer,
                onOpenWhatsApp = {
                    if (!ContactActions.openWhatsApp(context)) toast(context, "No app available to open WhatsApp link")
                },
                onOpenEmail = {
                    if (!ContactActions.openEmail(context)) toast(context, "No email app available")
                },
            )
            AnimatedVisibility(
                visible = state.drawerOpen,
                enter = if (reducedMotion) EnterTransition.None else slideInVertically { it / 2 } + fadeIn(),
                exit = if (reducedMotion) ExitTransition.None else slideOutVertically { it / 2 } + fadeOut(),
            ) {
                AppDrawerScreen(
                    state = state,
                    iconLoader = vm.iconLoader,
                    onQueryChange = vm::onQueryChange,
                    onLaunch = { app ->
                        if (AppLauncher.launch(context, app)) vm.closeDrawer()
                        else toast(context, "Couldn't open ${app.label}")
                    },
                    onClose = vm::closeDrawer,
                    onRetry = vm::retry,
                )
            }
        }
    }
    BackHandler(enabled = state.drawerOpen) { vm.closeDrawer() }
}

private fun toast(context: android.content.Context, msg: String) =
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
