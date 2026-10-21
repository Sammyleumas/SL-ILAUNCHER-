package com.sltechco.sllauncher.core.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object DeveloperInfo {
    const val PRODUCT = "SL-LAUNCHER"
    const val TAGLINE = "Your World. Perfectly Organized."
    const val BRAND = "SL-TECHCO"
    const val DEVELOPER = "Abiodun Samuel Feranmi (SAMMY LEUMAS)"
    const val WHATSAPP_DISPLAY = "09078076906"
    const val WHATSAPP_INTERNATIONAL = "2349078076906"
    const val EMAIL = "abiodunsamuel112@gmail.com"
}

object ContactActions {
    fun openWhatsApp(context: Context): Boolean =
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${DeveloperInfo.WHATSAPP_INTERNATIONAL}")))

    fun openEmail(context: Context): Boolean =
        start(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${DeveloperInfo.EMAIL}")))

    private fun start(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); true
    } catch (e: ActivityNotFoundException) { false }
}
