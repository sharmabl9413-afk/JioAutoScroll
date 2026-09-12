package com.example.jioautoscroll

import android.app.Activity
import android.os.Bundle
import android.provider.Settings
import android.content.Intent
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        status = findViewById(R.id.status)

        val startButton = findViewById<Button>(R.id.startButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        startButton.setOnClickListener {
            val service = AutoScrollService.instance

            if (service == null) {
                status.text = "Accessibility Service ON karo"

                try {
                    startActivity(
                        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    )
                } catch (e: Exception) {
                    status.text = "Settings open nahi hui"
                }

            } else {
                service.startAutoScroll()
                status.text = "Auto Scroll: ON"
            }
        }

        stopButton.setOnClickListener {
            AutoScrollService.instance?.stopAutoScroll()
            status.text = "Auto Scroll: OFF"
        }
    }
}
