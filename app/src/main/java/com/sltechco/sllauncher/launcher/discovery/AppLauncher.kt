package com.sltechco.sllauncher.launcher.discovery

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.sltechco.sllauncher.core.model.LauncherAppInfo

object AppLauncher {
    /** Returns false when the app can no longer be launched (uninstalled, disabled, restricted). */
    fun launch(context: Context, app: LauncherAppInfo): Boolean = try {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(ComponentName(app.packageName, app.activityName))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    } catch (e: SecurityException) {
        false
    }
}
