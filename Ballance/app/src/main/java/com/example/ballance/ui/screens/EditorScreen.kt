package com.example.ballance.ui.screens

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
                                                CellType.REDWALL -> Color.Black
                                            }
                                        )
                                        .border(1.dp, Color.Gray)

                                )
                            }
                        }
                    }
                }
            }



                Column{
                    Button(
                        onClick = {
                            viewModel.saveMaze(context)
                            Toast.makeText(context, "Maze saved!", Toast.LENGTH_SHORT).show()
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save Maze")
                    }

                    Spacer(modifier = Modifier.size(5.dp))

                    Button(
                        onClick = {
                            viewModel.loadMaze(context)
                            Toast.makeText(context, "Maze loaded!", Toast.LENGTH_SHORT).show()
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Load Maze")
                    }

                    Spacer(modifier = Modifier.size(5.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Back to Menu")
                    }
                }
            }

        }
    }
}
