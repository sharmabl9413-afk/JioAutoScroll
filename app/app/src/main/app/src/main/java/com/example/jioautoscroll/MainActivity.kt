package com.example.jioautoscroll

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        status = findViewById(R.id.status)

        val startButton: Button = findViewById(R.id.startButton)
        val stopButton: Button = findViewById(R.id.stopButton)

        startButton.setOnClickListener {

            val service = AutoScrollService.instance

            if (service == null) {

                status.text =
                    "Accessibility Service ON karo"

                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )

            } else {

                service.startAutoScroll()

                status.text =
                    "Auto Scroll: ON"
            }
        }

        stopButton.setOnClickListener {

            AutoScrollService.instance?.stopAutoScroll()

            status.text =
                "Auto Scroll: OFF"
        }
    }

    override fun onResume() {
        super.onResume()

        if (::status.isInitialized) {

            if (AutoScrollService.instance != null) {
                status.text = "Accessibility Ready"
            }
        }
    }
}
