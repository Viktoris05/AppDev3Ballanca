package com.example.ballance.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ballance.physics.CellType
import com.example.ballance.ViewModels.MazeViewModel
import com.example.ballance.MusicPlayer
import com.example.ballance.ui.theme.accentColor

@Composable
fun EditorScreen(
    navController: NavController,
    viewModel: MazeViewModel
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(MusicPlayer.isPlaying) }

    val mazeGrid = viewModel.mazeGrid
    var selectedType by remember { mutableStateOf(CellType.WALL) }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar with back and music toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        MusicPlayer.toggle(context)
                        isPlaying = MusicPlayer.isPlaying
                    }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = "Music Toggle",
                        tint = Color.White
                    )
                }
            }

            // Maze editor UI
            Column(
                modifier = Modifier
                    .verticalScroll(verticalScroll)
                    .padding(8.dp)
            ) {
                // Tile selection buttons
                Row {
                    Button(onClick = { selectedType = CellType.EMPTY },colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                        contentColor = Color.White
                    )) {
                        Text("Empty")
                    }
                    Button(onClick = { selectedType = CellType.WALL },colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    )) {
                        Text("Wall")
                    }
                    Button(onClick = { selectedType = CellType.FINISH },colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    )) {
                        Text("Finish")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Maze grid display
                for (row in 0 until viewModel.rows) {
                    Row(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        for (col in 0 until viewModel.cols) {
                            val cellState = mazeGrid[row][col]
                            val cellType by cellState

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        when (cellType) {
                                            CellType.EMPTY -> Color.White
                                            CellType.WALL -> Color.Black
                                            CellType.FINISH -> Color.Green
                                            else -> Color.Magenta
                                        }
                                    )
                                    .border(1.dp, Color.Gray)
                                    .clickable {
                                        viewModel.setCell(row, col, selectedType)
                                    }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Save/Load buttons
                Button(onClick = {
                    viewModel.saveMaze(context)
                    Toast.makeText(context, "Maze saved!", Toast.LENGTH_SHORT).show()
                },colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                )) {
                    Text("Save Maze")
                }

                Button(onClick = {
                    viewModel.loadMaze(context)
                    Toast.makeText(context, "Maze loaded!", Toast.LENGTH_SHORT).show()
                },colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                )) {
                    Text("Load Maze")
                }

                Button(onClick = { navController.popBackStack() },colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                )) {
                    Text("Back to Menu")
                }
            }
        }
    }
}
