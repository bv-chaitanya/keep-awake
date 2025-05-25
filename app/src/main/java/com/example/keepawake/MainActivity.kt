package com.example.keepawake

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Open Battery Optimization settings
        findViewById<Button>(R.id.btnBattery).setOnClickListener {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .also { startActivity(it) }
        }

        // Show instructions to add the tile manually
        findViewById<Button>(R.id.btnAddTile).setOnClickListener {
            Toast.makeText(
                this,
                "To add the Keep-Awake tile:\n" +
                        "1. Swipe down twice from the top to open Quick Settings.\n" +
                        "2. Tap the pencil (Edit) icon.\n" +
                        "3. Find “Keep-Awake” and drag it into your active tiles.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
