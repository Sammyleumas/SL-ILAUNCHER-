package com.sltechco.sllauncher.launcher.discovery

import com.sltechco.sllauncher.core.model.LauncherAppInfo

object AppFilter {
    /** Removes duplicate components, filters by label (case-insensitive) and sorts alphabetically. */
    fun filterAndSort(apps: List<LauncherAppInfo>, query: String): List<LauncherAppInfo> {
        val q = query.trim()
        val matched = if (q.isEmpty()) apps else apps.filter { it.label.contains(q, ignoreCase = true) }
        return matched
            .distinctBy { it.componentKey }
            .sortedWith(
                compareBy<LauncherAppInfo, String>(String.CASE_INSENSITIVE_ORDER) { it.label }
                    .thenBy { it.packageName }
                    .thenBy { it.activityName }
            )
    }
}
