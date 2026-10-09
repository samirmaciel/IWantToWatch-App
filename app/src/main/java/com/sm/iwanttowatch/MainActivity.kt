package com.sm.iwanttowatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sm.iwanttowatch.presentation.WantToWatchApp
import com.sm.iwanttowatch.ui.theme.IWantToWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IWantToWatchTheme {
                WantToWatchApp()
            }
        }
    }
}
