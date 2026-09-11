package com.example.jioautoscroll

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AutoScrollService : AccessibilityService() {

    companion object {
        var instance: AutoScrollService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Nothing for now
    }

    override fun onInterrupt() {
        // Nothing for now
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun startAutoScroll() {
        // Temporary test
    }

    fun stopAutoScroll() {
        // Temporary test
    }
}
