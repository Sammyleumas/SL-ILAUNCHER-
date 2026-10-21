package com.sltechco.sllauncher.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.format.DateFormat
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sltechco.sllauncher.appearance.glass.GlassCard
import com.sltechco.sllauncher.appearance.theme.SLTokens
import com.sltechco.sllauncher.core.common.DeveloperInfo
import com.sltechco.sllauncher.data.preferences.ThemeMode
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Updates once per minute and on time/zone changes using system broadcasts (no polling). */
@Composable
private fun rememberNow(): State<ZonedDateTime> {
    val context = LocalContext.current
    val now = remember { mutableStateOf(ZonedDateTime.now()) }
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, i: Intent?) { now.value = ZonedDateTime.now() }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        context.registerReceiver(receiver, filter)
        now.value = ZonedDateTime.now()
        onDispose { context.unregisterReceiver(receiver) }
    }
    return now
}

@Composable
fun HomeScreen(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    onOpenEmail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = SLTokens.colors
    val context = LocalContext.current
    val now by rememberNow()
    val locale = Locale.getDefault()
    val timePattern = if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm a"
    val time = now.format(DateTimeFormatter.ofPattern(timePattern, locale))
    val date = now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .pointerInput(Unit) { // default gesture: swipe up opens the drawer (configurable in Stage 3)
                var total = 0f
                detectVerticalDragGestures(
                    onDragStart = { total = 0f },
                    onVerticalDrag = { _, delta -> total += delta },
                    onDragEnd = { if (total < -120f) onOpenDrawer() },
                )
            },
    ) {
        Spacer(Modifier.height(48.dp))
        Text(time, color = c.textPrimary, fontSize = 72.sp, fontWeight = FontWeight.Light)
        Text(date, color = c.textSecondary, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(32.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 28.dp, onClick = onOpenDrawer) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = c.textSecondary)
                Spacer(Modifier.padding(horizontal = 6.dp))
                Text("Search apps or open all apps", color = c.textSecondary)
            }
        }

        Spacer(Modifier.weight(1f))

        Text("Theme", color = c.textSecondary, style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = mode == themeMode,
                    onClick = { onThemeChange(mode) },
                    label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(DeveloperInfo.PRODUCT, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(DeveloperInfo.TAGLINE, color = c.textSecondary, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Text("By ${DeveloperInfo.DEVELOPER} · ${DeveloperInfo.BRAND}", color = c.textSecondary,
                    style = MaterialTheme.typography.bodySmall)
                Row {
                    TextButton(onClick = onOpenWhatsApp) { Text("WhatsApp ${DeveloperInfo.WHATSAPP_DISPLAY}") }
                    TextButton(onClick = onOpenEmail) { Text("Email") }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 28.dp, onClick = onOpenDrawer) {
            Text("All apps", color = c.textPrimary, modifier = Modifier.padding(16.dp).fillMaxWidth(),
                fontWeight = FontWeight.Medium)
        }
    }
}
