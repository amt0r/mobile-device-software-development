package com.example.emergencypower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.emergencypower.ui.MainScreen
import com.example.emergencypower.ui.theme.EmergencyPowerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmergencyPowerTheme(dynamicColor = true) {
                MainScreen()
            }
        }
    }
}
