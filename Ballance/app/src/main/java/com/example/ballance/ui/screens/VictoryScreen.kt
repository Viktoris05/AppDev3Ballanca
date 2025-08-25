package com.example.ballance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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

@Composable
fun VictoryScreen(navController: NavController) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(MusicPlayer.isPlaying) }

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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurück", tint = Color.White)
                    }

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
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("Zurück zum Menü")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* TODO: nächstes Level oder Neustart */ },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("Nochmal spielen")
                    }
                }
            }
        }
    }
}
