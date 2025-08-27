package com.example.ballance.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ballance.physics.CellType
import com.example.ballance.viewModels.MazeViewModel
import com.example.ballance.MusicPlayer
import com.example.ballance.ui.theme.*
import com.example.ballance.ui.navigation.Screen
import com.example.ballance.utilities.BallMovementStore
import com.example.ballance.utilities.LevelSession

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

                // save/load + play in the top bar
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.saveMaze(context)
                            Toast.makeText(context, "Maze saved!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) { Text("Save Maze") }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.loadMaze(context)
                            Toast.makeText(context, "Maze loaded!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) { Text("Load Maze") }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Always save latest edits into custom_maze.json
                            viewModel.saveMaze(context)
                            // Copy custom_maze.json -> maze.json (runtime file GameScreen uses)
                            try {
                                val custom = context.openFileInput("custom_maze.json").bufferedReader().readText()
                                context.openFileOutput("maze.json", Context.MODE_PRIVATE).use {
                                    it.write(custom.toByteArray())
                                }
                                // Mark "custom" (not one of the 10 packaged)
                                LevelSession.currentLevelIndex = null
                                BallMovementStore.loadBestMovement(context,null)
                                navController.navigate(Screen.Game.route)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Please save your level first.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) { Text("Play") }
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
            Row(
                modifier = Modifier
                    .verticalScroll(verticalScroll)
                    .padding(8.dp)
                    .fillMaxSize()
            ) {

                // Tile selection buttons
                Column {
                    Button(
                        onClick = { selectedType = CellType.EMPTY },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedType == CellType.EMPTY) accentColorSelected else accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Empty")
                    }
                    Button(
                        onClick = { selectedType = CellType.WALL },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedType == CellType.WALL) accentColorSelected else accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Wall")
                    }
                    Button(
                        onClick = { selectedType = CellType.FINISH },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedType == CellType.FINISH) accentColorSelected else accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Finish")
                    }
                    Button(
                        onClick = { selectedType = CellType.SLOWDOWN },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedType == CellType.SLOWDOWN) accentColorSelected else accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Slowdown")
                    }
                    Button(
                        onClick = { selectedType = CellType.SPEEDUP },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedType == CellType.SPEEDUP) accentColorSelected else accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Speedup")
                    }
                    Button(
                        onClick = { selectedType = CellType.REDWALL },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedType == CellType.REDWALL) accentColorSelected else accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Red Wall")
                    }
                    Button(
                        onClick = { selectedType = CellType.STARTINGTILE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedType == CellType.STARTINGTILE) accentColorSelected else accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Start")
                    }
                }

                // Maze grid display
                val cellSize = 19.dp
                val gridWidth = cellSize * viewModel.cols
                val gridHeight = cellSize * viewModel.rows

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(width = gridWidth, height = gridHeight)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val row = (offset.y / cellSize.toPx()).toInt()
                                    val col = (offset.x / cellSize.toPx()).toInt()
                                    if (row in 0 until viewModel.rows && col in 0 until viewModel.cols) {
                                        viewModel.setCell(row, col, selectedType)
                                    }
                                },
                                onDrag = { change, _ ->
                                    val row = (change.position.y / cellSize.toPx()).toInt()
                                    val col = (change.position.x / cellSize.toPx()).toInt()
                                    if (row in 0 until viewModel.rows && col in 0 until viewModel.cols) {
                                        viewModel.setCell(row, col, selectedType)
                                    }
                                    change.consume()
                                })

                        }
                ) {
                    Row {
                        for (col in 0 until viewModel.cols) {
                            Column {
                                for (row in 0 until viewModel.rows) {
                                    val cellState = mazeGrid[row][col]
                                    val cellType by cellState

                                    Box(
                                        modifier = Modifier
                                            .size(19.dp)
                                            .background(
                                                when (cellType) {
                                                    CellType.EMPTY -> Color.White
                                                    CellType.WALL -> accentColor
                                                    CellType.FINISH -> finishColor
                                                    CellType.SLOWDOWN -> slowdownColor
                                                    CellType.SPEEDUP -> speedupColor
                                                    CellType.REDWALL -> redWallColor
                                                    CellType.STARTINGTILE -> Color.DarkGray
                                                }
                                            )
                                            .border(1.dp, Color.Gray)

                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
