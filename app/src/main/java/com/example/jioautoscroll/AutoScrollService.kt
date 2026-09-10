package com.example.jioautoscroll

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoScrollService : AccessibilityService() {

    companion object {
        var instance: AutoScrollService? = null
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var lastSwipeTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isRunning) return

        val root = rootInActiveWindow ?: return

        /*
         * YouTube Shorts की accessibility information
         * से video progress ढूँढने की कोशिश।
         */
        val progress = findProgress(root)

        if (progress != null) {

            val currentTime = progress.first
            val totalTime = progress.second

            /*
             * Video के आखिरी 1 second में पहुँचने पर
             * अगला Short खोलने के लिए swipe।
             */
            if (totalTime > 0 &&
                currentTime >= totalTime - 1
            ) {
                swipeNextShort()
            }
        }
    }

    override fun onInterrupt() {
        stopAutoScroll()
    }

    override fun onDestroy() {
        stopAutoScroll()
        instance = null
        super.onDestroy()
    }

    fun startAutoScroll() {
        if (isRunning) return

        isRunning = true
    }

    fun stopAutoScroll() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun findProgress(
        node: AccessibilityNodeInfo
    ): Pair<Long, Long>? {

        /*
         * Content description और text में
         * video time खोजना।
         */
        val text = buildString {

            node.text?.let {
                append(it)
                append(" ")
            }

            node.contentDescription?.let {
                append(it)
                append(" ")
            }
        }

        val result = parseTime(text)

        if (result != null) {
            return result
        }

        /*
         * सभी child nodes को check करना।
         */
        for (i in 0 until node.childCount) {

            val child = node.getChild(i) ?: continue

            val resultFromChild = findProgress(child)

            child.recycle()

            if (resultFromChild != null) {
                return resultFromChild
            }
        }

        return null
    }

    private fun parseTime(
        text: String
    ): Pair<Long, Long>? {

        /*
         * उदाहरण:
         *
         * 0:15 / 0:30
         * 1:25 / 2:00
         */

        val regex = Regex(
            """(\d{1,2}):(\d{2})\s*/\s*(\d{1,2}):(\d{2})"""
        )

        val match = regex.find(text) ?: return null

        val currentMinutes =
            match.groupValues[1].toLong()

        val currentSeconds =
            match.groupValues[2].toLong()

        val totalMinutes =
            match.groupValues[3].toLong()

        val totalSeconds =
            match.groupValues[4].toLong()

        val currentTime =
            currentMinutes * 60 + currentSeconds

        val totalTime =
            totalMinutes * 60 + totalSeconds

        return Pair(
            currentTime,
            totalTime
        )
    }

    private fun swipeNextShort() {

        val now = System.currentTimeMillis()

        /*
         * एक ही Short पर लगातार कई swipe
         * होने से रोकना।
         */
        if (now - lastSwipeTime < 2500) {
            return
        }

        lastSwipeTime = now

        val display = resources.displayMetrics

        val centerX =
            display.widthPixels / 2f

        val startY =
            display.heightPixels * 0.78f

        val endY =
            display.heightPixels * 0.22f

        val path = Path()

        path.moveTo(
            centerX,
            startY
        )

        path.lineTo(
            centerX,
            endY
        )

        val gesture =
            GestureDescription.Builder()
                .addStroke(
                    GestureDescription.StrokeDescription(
                        path,
                        0,
                        500
                    )
                )
                .build()

        dispatchGesture(
            gesture,
            object : GestureResultCallback() {

                override fun onCompleted(
                    gestureDescription: GestureDescription?
                ) {
                    // Swipe successful
                }

                override fun onCancelled(
                    gestureDescription: GestureDescription?
                ) {
                    // Swipe cancelled
                }
            },
            null
        )
    }
}
