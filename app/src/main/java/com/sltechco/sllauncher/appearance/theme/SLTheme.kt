package com.sltechco.sllauncher.appearance.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.sltechco.sllauncher.data.preferences.ThemeMode

/** Semantic colour tokens. Brand palette: Obsidian, Deep surface, Graphite glass, Frosted light, Silver. */
@Immutable
data class SLColors(
    val background: Color,
    val surface: Color,
    val glass: Color,
    val glassBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val onAccent: Color,
    val isDark: Boolean,
)

private val Obsidian = Color(0xFF090A0D)
private val DeepSurface = Color(0xFF12151B)
private val GraphiteGlass = Color(0xFF272B33)
private val FrostedLight = Color(0xFFE7E9EE)
private val Silver = Color(0xFFD7DCE5)
private val TextLight = Color(0xFFF5F6F8)
private val TextMuted = Color(0xFFAEB5C2)
private val TextDark = Color(0xFF17191F)

private val DarkColors = SLColors(
    background = Obsidian, surface = DeepSurface,
    glass = GraphiteGlass.copy(alpha = 0.55f), glassBorder = Silver.copy(alpha = 0.22f),
    textPrimary = TextLight, textSecondary = TextMuted,
    accent = Color(0xFF4DA3FF), onAccent = TextDark, isDark = true,
)
private val LightColors = SLColors(
    background = FrostedLight, surface = Color.White,
    glass = Color.White.copy(alpha = 0.6f), glassBorder = TextDark.copy(alpha = 0.18f),
    textPrimary = TextDark, textSecondary = Color(0xFF4A5160),
    accent = Color(0xFF1F6FEB), onAccent = Color.White, isDark = false,
)
private val MonoColors = DarkColors.copy(accent = Silver, onAccent = TextDark)

private val LocalSLColors = staticCompositionLocalOf { DarkColors }

object SLTokens {
    val colors: SLColors @Composable @ReadOnlyComposable get() = LocalSLColors.current
}

@Composable
fun SLTheme(mode: ThemeMode, content: @Composable () -> Unit) {
    val c = when (mode) {
        ThemeMode.DARK -> DarkColors
        ThemeMode.LIGHT -> LightColors
        ThemeMode.MONOCHROME -> MonoColors
    }
    val scheme = if (c.isDark) {
        darkColorScheme(primary = c.accent, onPrimary = c.onAccent, background = c.background,
            surface = c.surface, onBackground = c.textPrimary, onSurface = c.textPrimary)
    } else {
        lightColorScheme(primary = c.accent, onPrimary = c.onAccent, background = c.background,
            surface = c.surface, onBackground = c.textPrimary, onSurface = c.textPrimary)
    }
    CompositionLocalProvider(LocalSLColors provides c) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
