package com.sltechco.sllauncher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.sltechco.sllauncher.appearance.glass.GlassSearchField
import com.sltechco.sllauncher.appearance.theme.SLTheme
import com.sltechco.sllauncher.data.preferences.ThemeMode
import org.junit.Rule
import org.junit.Test

class SearchFieldTest {
    @get:Rule val rule = createComposeRule()

    @Test fun placeholderTypingAndClear() {
        rule.setContent {
            var q by remember { mutableStateOf("") }
            SLTheme(ThemeMode.DARK) { GlassSearchField(q, { q = it }, "Search apps") }
        }
        rule.onNodeWithText("Search apps").assertIsDisplayed()
        rule.onNode(hasSetTextAction()).performTextInput("map")
        rule.onNodeWithContentDescription("Clear search").assertIsDisplayed().performClick()
        rule.onNodeWithText("Search apps").assertIsDisplayed()
    }
}
