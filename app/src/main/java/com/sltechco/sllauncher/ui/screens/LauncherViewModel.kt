package com.sltechco.sllauncher.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sltechco.sllauncher.core.model.LauncherAppInfo
import com.sltechco.sllauncher.data.preferences.ThemeMode
import com.sltechco.sllauncher.data.preferences.ThemePreferences
import com.sltechco.sllauncher.launcher.discovery.AppFilter
import com.sltechco.sllauncher.launcher.discovery.IconLoader
import com.sltechco.sllauncher.launcher.discovery.InstalledAppsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppsState {
    data object Loading : AppsState
    data class Error(val message: String) : AppsState
    data class Ready(val apps: List<LauncherAppInfo>, val iconVersion: Int) : AppsState
}

data class LauncherUiState(
    val appsState: AppsState = AppsState.Loading,
    val query: String = "",
    val drawerOpen: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val visibleApps: List<LauncherAppInfo> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class LauncherViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = InstalledAppsRepository(app)
    private val prefs = ThemePreferences(app)
    val iconLoader = IconLoader(app.packageManager)

    private val query = MutableStateFlow("")
    private val drawerOpen = MutableStateFlow(false)
    private val refreshTrigger = MutableStateFlow(0)
    private val iconVersion = java.util.concurrent.atomic.AtomicInteger(0)

    private val appsState: Flow<AppsState> = refreshTrigger.flatMapLatest {
        repository.observe()
            .map<List<LauncherAppInfo>, AppsState> {
                iconLoader.clear() // package set changed: invalidate cached icons
                AppsState.Ready(it, iconVersion.incrementAndGet())
            }
            .catch { emit(AppsState.Error(it.message ?: "Unknown error")) }
            .onStart { emit(AppsState.Loading) }
    }

    val state: StateFlow<LauncherUiState> =
        combine(appsState, query, drawerOpen, prefs.themeMode) { apps, q, open, theme ->
            LauncherUiState(
                appsState = apps, query = q, drawerOpen = open, themeMode = theme,
                visibleApps = if (apps is AppsState.Ready) AppFilter.filterAndSort(apps.apps, q) else emptyList(),
            )
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LauncherUiState())

    fun openDrawer() { drawerOpen.value = true }
    fun closeDrawer() { drawerOpen.value = false; query.value = "" }
    fun onQueryChange(value: String) { query.value = value }
    fun retry() { refreshTrigger.value += 1 }
    fun setTheme(mode: ThemeMode) { viewModelScope.launch { prefs.setThemeMode(mode) } }
}
