package com.example.ballance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ballance.MusicPlayer
import com.example.ballance.ui.navigation.Screen
import com.example.ballance.ui.theme.*
import com.example.ballance.utilities.LevelSession
import com.example.ballance.utilities.LevelTimesStore

@Composable
fun VictoryScreen(navController: NavController) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(MusicPlayer.isPlaying) }

    // Zeiten holen
    val levelIndex = LevelSession.currentLevelIndex
    val lastRunMs = LevelSession.lastRunMillis
    val bestMs = remember(levelIndex) {
        if (levelIndex != null) LevelTimesStore.getBestTime(context, levelIndex) else null
    }
    fun format(ms: Long): String {
        val m = ms / 60000
        val s = (ms % 60000) / 1000
        val ms3 = ms % 1000
        return "%d:%02d.%03d".format(m, s, ms3)
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            cocoBlackColor,
            darkGreyColor,
            accentColor
        )
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top-Leiste mit Zurück und Musik
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            MusicPlayer.toggle(context)
                            isPlaying = MusicPlayer.isPlaying
                        }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                            contentDescription = "Musik",
                            tint = Color.White
                        )
                    }
                }

                // Sieg-Text
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Win!",
                        fontSize = 28.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Zeiten anzeigen
                    Text(
                        text = "Your time: ${format(lastRunMs)}",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    if (levelIndex != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Best: ${bestMs?.let { format(it) } ?: "--:--.---"}",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Weiter / Zurück-Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.MainMenu.route) },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Zurück zum Menü")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.popBackStack() //lösche die vorherige Instanz
                            navController.navigate(Screen.Game.route)
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Nochmal spielen")
                    }
                }
            }
        }
    }
}
