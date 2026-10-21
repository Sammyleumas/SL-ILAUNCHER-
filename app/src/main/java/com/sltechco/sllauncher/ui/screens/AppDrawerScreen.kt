package com.sltechco.sllauncher.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sltechco.sllauncher.appearance.glass.GlassSearchField
import com.sltechco.sllauncher.appearance.theme.SLTokens
import com.sltechco.sllauncher.core.model.LauncherAppInfo
import com.sltechco.sllauncher.launcher.discovery.IconLoader

@Composable
fun AppDrawerScreen(
    state: LauncherUiState,
    iconLoader: IconLoader,
    onQueryChange: (String) -> Unit,
    onLaunch: (LauncherAppInfo) -> Unit,
    onClose: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = SLTokens.colors
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }

    Column(modifier.fillMaxSize().background(c.background.copy(alpha = 0.96f)).statusBarsPadding()) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            GlassSearchField(
                value = state.query, onValueChange = onQueryChange, placeholder = "Search apps",
                modifier = Modifier.weight(1f), focusRequester = focus,
            )
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, "Close app drawer", tint = c.textPrimary) }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val s = state.appsState) {
                AppsState.Loading -> CircularProgressIndicator(color = c.accent)
                is AppsState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Text("Couldn't load apps", color = c.textPrimary)
                    Text(s.message, color = c.textSecondary, textAlign = TextAlign.Center)
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) { Text("Retry") }
                }
                is AppsState.Ready -> if (state.visibleApps.isEmpty()) {
                    Text(
                        if (state.query.isBlank()) "No launchable apps found" else "No apps match \"${state.query.trim()}\"",
                        color = c.textSecondary, modifier = Modifier.padding(24.dp), textAlign = TextAlign.Center,
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(84.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding(),
                    ) {
                        items(state.visibleApps, key = { it.componentKey }) { app ->
                            AppTile(app, iconLoader, s.iconVersion) { onLaunch(app) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppTile(app: LauncherAppInfo, loader: IconLoader, iconVersion: Int, onClick: () -> Unit) {
    val c = SLTokens.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClickLabel = "Open ${app.label}", role = Role.Button, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        AppIcon(app, loader, iconVersion, 52.dp)
        Text(
            app.label, color = c.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center, style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun AppIcon(app: LauncherAppInfo, loader: IconLoader, iconVersion: Int, size: Dp) {
    val px = with(LocalDensity.current) { size.roundToPx() }
    val bitmap by produceState<ImageBitmap?>(loader.cached(app, px), app.componentKey, px, iconVersion) {
        value = loader.load(app, px)
    }
    val b = bitmap
    if (b != null) {
        Image(bitmap = b, contentDescription = null, modifier = Modifier.size(size))
    } else {
        Box(Modifier.size(size).clip(RoundedCornerShape(14.dp)).background(SLTokens.colors.glass))
    }
}
