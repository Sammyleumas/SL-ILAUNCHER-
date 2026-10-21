package com.sltechco.sllauncher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.sltechco.sllauncher.ui.screens.LauncherRoot
import com.sltechco.sllauncher.ui.screens.LauncherViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { LauncherRoot(viewModel) }
    }

    /** Pressing Home while already on Home returns to the clean home surface. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME)) viewModel.closeDrawer()
    }
}
