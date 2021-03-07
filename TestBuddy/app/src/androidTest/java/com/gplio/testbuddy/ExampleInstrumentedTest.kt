package com.gplio.testbuddy

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.UiAutomation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var device: UiDevice
    private lateinit var auto: UiAutomation
    private lateinit var context: Context
    private lateinit var arguments: Bundle

    @Before
    fun prepare() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        auto = InstrumentationRegistry.getInstrumentation().uiAutomation
        context = InstrumentationRegistry.getInstrumentation().targetContext
        arguments = InstrumentationRegistry.getArguments()

    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.gplio.testbuddy", appContext.packageName)
    }

    @Test
    fun dumpViews() {
        val info = auto.serviceInfo
        info.flags = info.flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        auto.serviceInfo = info

        val allWindows = auto.windows
        val allWindowsSize = allWindows.size
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager


        log("windows: $allWindowsSize displayManager: $displayManager")
        for (window in allWindows) {
            dumpWindow(window)
        }

    }

    private fun dumpWindow(window: AccessibilityWindowInfo) {
        val title = window.title ?: ""
        log("[WINDOWS]: $title")

        for (idx in 0 until window.childCount) {
            val child = window.getChild(idx)

            if (child == null) {
                log("null window child in window: $window idx: $idx")
                continue
            }

            dumpWindow(child)

            child.recycle()
        }

        val root = window.root ?: return

        dumpNode(root)
        root.recycle()
    }

    private fun dumpNode(node: AccessibilityNodeInfo) {
        val count = node.childCount

        log("[NODE] ${node.className} ${node.viewIdResourceName} :: ${node.text}")
        for (idx in 0 until count) {
            val child = node.getChild(idx)

            if (child == null) {
                log("null child at idx: $idx root: $node")
                continue
            }

            dumpNode(child)
            child.recycle()
        }

    }

    private fun log(message: String) {
        Log.d("tbuddy", message)
    }
}