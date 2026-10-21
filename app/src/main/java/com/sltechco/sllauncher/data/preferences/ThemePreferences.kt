package com.sltechco.sllauncher.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ThemeMode { DARK, LIGHT, MONOCHROME }

private val Context.launcherDataStore by preferencesDataStore(name = "launcher_settings")

class ThemePreferences(private val context: Context) {
    private val key = stringPreferencesKey("theme_mode")

    val themeMode: Flow<ThemeMode> = context.launcherDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs -> prefs[key]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.DARK }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.launcherDataStore.edit { it[key] = mode.name }
    }
}
