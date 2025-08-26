package com.example.ballance.ui.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ballance.MusicPlayer
import com.example.ballance.physics.CellType
import com.example.ballance.ui.navigation.Screen
import com.example.ballance.ui.theme.*
import com.example.ballance.utilities.LevelPack
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun LevelSelectScreen(navController: NavController) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(MusicPlayer.isPlaying) }

    // Match MainMenu responsiveness for button look
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val buttonWidthFraction = if (isLandscape) 0.8f else 0.75f
    val buttonVerticalPadding = if (isLandscape) 6.dp else 10.dp
    val buttonTextSize = if (isLandscape) 16.sp else 18.sp

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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

            // more than it can fit on screen thus scrollable
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Text("Levelauswahl", fontSize = 20.sp, color = Color.White)
                    Spacer(Modifier.height(12.dp))
                }

                items(LevelPack.entries) { entry ->
                    //same MenuButton composable as in MainMenu so it matches
                    MenuButton(
                        text = entry.name,
                        color = accentColor,
                        widthFraction = buttonWidthFraction,
                        verticalPadding = buttonVerticalPadding,
                        textSize = buttonTextSize
                    ) {
                        // Load packaged level, save to the same file GameViewModel reads, then navigate basically to assure custom made levels work
                        val data: List<List<CellType>> = LevelPack.load(context, entry.index)
                        val jsonString = Json.Default.encodeToString(data)
                        context.openFileOutput("maze.json", Context.MODE_PRIVATE).use {
                            it.write(jsonString.toByteArray())
                        }
                        navController.navigate(Screen.Game.route)
                    }
                }
            }
        }
    }
}
