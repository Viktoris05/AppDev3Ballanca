package com.example.ballance.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ballance.UserInterface.GameCanvas
import com.example.ballance.MusicPlayer
import com.example.ballance.Utilities.TiltSensorHandler
import com.example.ballance.ui.theme.accentColor
import com.example.ballance.viewModels.GameViewModel

/**
 * Main game UI screen that:
 * - Renders the maze and the moving ball.
 * - Uses accelerometer input via [TiltSensorHandler] to update ball physics.
 * - Calls [GameViewModel] to process physics and maze rules.
 * - Displays live velocity and acceleration debugging data.
 * - Provides a button to return to the main menu.
 * - Provides toggle button for music playback
 *
 * @param navController Allows navigating back to menu
 * @param viewModel ViewModel that manages maze layout and ball physics.
 */
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(MusicPlayer.isPlaying) }

    // Sensor listener to track tilt (acceleration)
    val sensorHandler = remember { TiltSensorHandler(context) }

    // Register/unregister the sensor listener with lifecycle
    DisposableEffect(sensorHandler) {
        sensorHandler.register()
        onDispose { sensorHandler.unregister() }
    }

    // Load maze and dimensions
    val maze = viewModel.getMaze()
    val rows = maze[0].size
    val cols = maze.size
    val cellSize = 70f // size of each cell in pixels
    var velocityX = viewModel.getVelocityX()
    var velocityY = viewModel.getVelocityY()
    //test
    //test2
    // Track ball position in world-space pixels (stateful for Compose redraw)
    var ballX by remember { mutableStateOf(cellSize * (cols / 2)) }
    var ballY by remember { mutableStateOf(cellSize * (rows / 2)) }

    // Launch a coroutine that starts when this Composable is composed.
    // This will run our real-time physics update loop (60fps-like).
    LaunchedEffect(Unit) {
        // Get the timestamp of the current frame (in nanoseconds).
        // This "primes" the loop so we can measure deltaTime on the next frame.
        var lastFrameTime = withFrameNanos { it }

        // Start an infinite loop: this will run once per frame.
        while (true) {
            // Suspend until the next screen refresh.
            // 'now' is the timestamp of the current frame in nanoseconds.
            withFrameNanos { now ->
                // Calculate time since the last frame in seconds.
                // 1 second = 1,000,000,000 nanoseconds.
                val dt = (now - lastFrameTime) / 1_000_000_000f

                // Save this frame's timestamp so we can measure the next delta.
                lastFrameTime = now

                // Read the current tilt from the accelerometer (x = left/right, y = up/down).
                val ax = sensorHandler.tiltX
                val ay = sensorHandler.tiltY

                // Step the physics simulation with the tilt input and time delta.
                // The ViewModel runs the physics and gives us the updated position.
                val (newX, newY) = viewModel.update(
                    ax = ax,
                    ay = ay,
                    cellSize = cellSize,
                    deltaTime = dt
                )

                // Save the updated position into Compose state.
                // This triggers recomposition and redraw of the ball on screen.
                ballX = newX
                ballY = newY
                // Read latest velocity for debug display
                velocityX = viewModel.getVelocityX()
                velocityY = viewModel.getVelocityY()

            }
        }

    }



    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar with Back and Music toggle
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
                        contentDescription = "Music",
                        tint = Color.White
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // Maze and ball rendering
                GameCanvas(
                    maze = maze,
                    ballX = ballX,
                    ballY = ballY,
                    cellSize = cellSize
                )

                // Debug overlay in upper-right
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top
                ) {
                    //debug values on screen
                    fun Float.format(digits: Int): String = "%.${digits}f".format(this)

                    Text("ax: ${sensorHandler.tiltX.format(2)}", color = Color.White, fontSize = 14.sp)
                    Text("ay: ${sensorHandler.tiltY.format(2)}", color = Color.White, fontSize = 14.sp)
                    Text("vx: ${velocityX.format(2)}", color = Color.White, fontSize = 14.sp)
                    Text("vy: ${velocityY.format(2)}", color = Color.White, fontSize = 14.sp)
                }

                // "Back to Menu" button in lower-center
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )) {
                        Text("Back to Menu")
                    }
                }
            }
        }
    }
}