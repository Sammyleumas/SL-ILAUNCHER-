package com.sltechco.sllauncher.launcher.discovery

import android.content.ComponentName
import android.content.pm.PackageManager
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.sltechco.sllauncher.core.model.LauncherAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Loads installed icons off the main thread and keeps a small in-memory cache. */
class IconLoader(private val pm: PackageManager) {
    private val cache = LruCache<String, ImageBitmap>(160)

    private fun key(app: LauncherAppInfo, px: Int) = "${app.componentKey}@$px"

    fun cached(app: LauncherAppInfo, px: Int): ImageBitmap? = cache.get(key(app, px))

    suspend fun load(app: LauncherAppInfo, px: Int): ImageBitmap? {
        cached(app, px)?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val drawable = pm.getActivityIcon(ComponentName(app.packageName, app.activityName))
                drawable.toBitmap(px, px).asImageBitmap().also { cache.put(key(app, px), it) }
            } catch (e: Exception) {
                null // missing or unloadable icon: UI shows a neutral fallback
            }
        }
    }

    fun clear() = cache.evictAll()
}
