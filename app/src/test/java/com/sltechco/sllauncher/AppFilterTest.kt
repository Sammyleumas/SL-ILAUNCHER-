package com.sltechco.sllauncher

import com.sltechco.sllauncher.core.model.LauncherAppInfo
import com.sltechco.sllauncher.launcher.discovery.AppFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppFilterTest {
    private fun app(label: String, pkg: String = "pkg.$label", act: String = "Main") = LauncherAppInfo(pkg, act, label)

    private val apps = listOf(app("Maps"), app("camera"), app("Chrome"), app("Calculator"))

    @Test fun sortsAlphabeticallyIgnoringCase() {
        assertEquals(listOf("Calculator", "camera", "Chrome", "Maps"), AppFilter.filterAndSort(apps, "").map { it.label })
    }

    @Test fun filtersByLabelCaseInsensitive() {
        assertEquals(listOf("Calculator", "camera"), AppFilter.filterAndSort(apps, "CA").map { it.label })
    }

    @Test fun blankQueryReturnsAll() {
        assertEquals(4, AppFilter.filterAndSort(apps, "   ").size)
    }

    @Test fun noMatchReturnsEmpty() {
        assertTrue(AppFilter.filterAndSort(apps, "zzz").isEmpty())
    }

    @Test fun removesDuplicateComponents() {
        val dup = listOf(app("Maps"), app("Maps"))
        assertEquals(1, AppFilter.filterAndSort(dup, "").size)
    }

    @Test fun sameLabelDifferentPackagesAreBothKept() {
        val same = listOf(app("Notes", "a.notes"), app("Notes", "b.notes"))
        assertEquals(listOf("a.notes", "b.notes"), AppFilter.filterAndSort(same, "").map { it.packageName })
    }
}
