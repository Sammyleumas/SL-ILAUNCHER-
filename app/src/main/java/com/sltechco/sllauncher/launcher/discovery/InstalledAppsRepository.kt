package com.sltechco.sllauncher.launcher.discovery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import com.sltechco.sllauncher.core.model.LauncherAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class InstalledAppsRepository(private val context: Context) {

    /** Emits the current app list immediately and again after every package add/remove/replace. */
    fun observe(): Flow<List<LauncherAppInfo>> = packageChanges()
        .conflate()
        .map { queryLaunchableApps() }
        .flowOn(Dispatchers.IO)

    private fun packageChanges(): Flow<Unit> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, i: Intent?) { trySend(Unit) }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        context.registerReceiver(receiver, filter)
        trySend(Unit)
        awaitClose { context.unregisterReceiver(receiver) }
    }

    @Suppress("DEPRECATION")
    private fun queryLaunchableApps(): List<LauncherAppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val results = if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            pm.queryIntentActivities(intent, 0)
        }
        return results.mapNotNull { ri ->
            val ai = ri.activityInfo ?: return@mapNotNull null
            if (ai.packageName == context.packageName) return@mapNotNull null
            LauncherAppInfo(ai.packageName, ai.name, ri.loadLabel(pm)?.toString().orEmpty().ifBlank { ai.packageName })
        }.distinctBy { it.componentKey }
    }
}
