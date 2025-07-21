package com.example.ballance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.ballance.ui.navigation.AppNavHost
import com.example.ballance.ui.theme.BallanceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Musik startet beim ersten Toggle im Menü

        setContent {
            BallanceTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayer.stop()
    }
}
