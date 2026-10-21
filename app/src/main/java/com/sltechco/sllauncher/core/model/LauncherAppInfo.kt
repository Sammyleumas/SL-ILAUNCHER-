package com.sltechco.sllauncher.core.model

/** A launchable activity discovered through PackageManager. Package name is the stable app identifier. */
data class LauncherAppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
) {
    val componentKey: String get() = "$packageName/$activityName"
}
