package com.example.ballance.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ballance.ui.screens.utilities.GameCanvas
import com.example.ballance.MusicPlayer
import com.example.ballance.utilities.TiltSensorHandler
import com.example.ballance.physics.CellBehavior
import com.example.ballance.ui.navigation.Screen
import com.example.ballance.ui.theme.accentColor
import com.example.ballance.ui.theme.backgroundColor
import com.example.ballance.utilities.BallMovementStore
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

    // control flag for the game loop
    var gameActive by rememberSaveable { mutableStateOf(true) }

    // Prevent duplicate navigations to Victory
    var navigated by rememberSaveable { mutableStateOf(false) }

    // Sensor listener to track tilt (acceleration)
    val sensorHandler = remember { TiltSensorHandler(context) }

    // Register/unregister the sensor listener with lifecycle
    DisposableEffect(sensorHandler) {
        sensorHandler.register()
        onDispose { sensorHandler.unregister() }
    }

    //victory navigation callback
    val navigateToVictory by rememberUpdatedState {
        if (navigated) return@rememberUpdatedState
        navigated = true

        // record time & best before leaving
        viewModel.markVictory()

        //hard stop the game and sensors before navigating to prevent it running in the background
        gameActive = false
        sensorHandler.unregister()

        navController.navigate(Screen.Victory.route) {
            // Ensure back goes straight to Main Menu
            popUpTo(Screen.MainMenu.route) { inclusive = false } // keep MainMenu underneaeth
            launchSingleTop = true      // dont stack multiple Victories
            restoreState = false
        }
    }

    DisposableEffect(Unit) {
        val prev = CellBehavior.Finish.onVictoryCallback
        CellBehavior.Finish.onVictoryCallback = { navigateToVictory() }
        onDispose { CellBehavior.Finish.onVictoryCallback = prev }
    }

    // Load maze and dimensions
    val maze = viewModel.getMaze()
    val cellSize = 66f // size of each cell in pixels
    var velocityX = viewModel.getVelocityX()
    var velocityY = viewModel.getVelocityY()
    // Track ball position in world-space pixels (stateful for Compose redraw)
    var ballX by remember { mutableStateOf(viewModel.ballX) }
    var ballY by remember { mutableStateOf(viewModel.ballY) }
    var ghostX by remember { mutableStateOf(0f) }
    var ghostY by remember { mutableStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }
    var outOfTime by remember { mutableStateOf(false) }

    // timer UI state
    var elapsedMs by remember { mutableStateOf(0L) }

    //Timer start so the Array that tracks ball movement doesn't run out
    var timer by remember {mutableStateOf(0L)} //10 minutes in ms = 6000

    // pause/resume the timer when pause state changes
    LaunchedEffect(isPaused) {
        if (isPaused) viewModel.pauseTimer() else viewModel.resumeTimer()
    }

    // small helper for formatting time
    fun formatTime(ms: Long): String {
        val m = ms / 60000
        val s = (ms % 60000) / 1000
        val ms3 = ms % 1000
        return "%d:%02d.%03d".format(m, s, ms3)
    }

    // Launch a coroutine that starts when this Composable is composed.
    // This will run our real-time physics update loop (60fps-like).
    LaunchedEffect(gameActive) {
        // If the game is not active, do not start the loop
        if (!gameActive) return@LaunchedEffect

        // Get the timestamp of the current frame (in nanoseconds).
        // This "primes" the loop so we can measure deltaTime on the next frame.
        var lastFrameTime = withFrameNanos { it }

        // Start a loop that runs only while the game is active.
        while (gameActive) {
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

                // update timer label every frame (pause-aware via the ViewModel)
                elapsedMs = viewModel.getElapsedMillis()
                timer = 600000 - elapsedMs

                if(timer <= 0){
                    isPaused = true
                    outOfTime = true
                }

                if(!isPaused) {
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

                    val newGhostPos = BallMovementStore.getGhostMovement(elapsedMs)

                    if(newGhostPos != null){
                        ghostX = newGhostPos.first
                        ghostY = newGhostPos.second
                    }

                    //track latest ball movements
                    BallMovementStore.addMovement(elapsedMs,Pair(ballX, ballY))

                    // Read latest velocity for debug display
                    velocityX = viewModel.getVelocityX()
                    velocityY = viewModel.getVelocityY()
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Maze and ball rendering
            GameCanvas(
                maze = maze,
                ballX = ballX,
                ballY = ballY,
                ghostX = ghostX,
                ghostY = ghostY,
                cellSize = cellSize
            )

            if(isPaused || outOfTime){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)), //make background more dim
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        modifier = Modifier
                            .background(backgroundColor)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp))
                    {
                        //Music toggle
                        IconButton(
                            onClick = {
                                MusicPlayer.toggle(context)
                                isPlaying = MusicPlayer.isPlaying
                            },
                            modifier = Modifier.size(15.dp).align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                                contentDescription = "Musik umschalten",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Text that says the time ran out
                        if(outOfTime){
                            Text("Time run out! Restart or exit to main menu")
                        }

                        // Level name in pause menu
                        Text("Level ${viewModel.getNameForCurrent()}", color = Color.White)

                        // best + current time in pause menu
                        val best = viewModel.getBestTimeForCurrent()
                        Text("Highscore: ${best?.let { formatTime(it) } ?: "--:--.---"}", color = Color.White)
                        Text("Current Score: ${formatTime(elapsedMs)}", color = Color.White)

                        if(!outOfTime) {
                            //Resume button
                            Button(
                                onClick = { isPaused = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Resume")
                            }
                        }

                        //Restart Level
                        Button(onClick = {
                            isPaused = false
                            outOfTime = false
                            viewModel.loadMaze()
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = Color.White
                            )) {
                            Text("Restart level")
                        }

                        //Back to Menu
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

            // Debug overlay in upper-right
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {

                if(!outOfTime) {
                    //Pause/Resume Button
                    Button(
                        onClick = {
                            if (!isPaused) {
                                isPaused = true
                            } else {
                                isPaused = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    {
                        Text(
                            if (!isPaused) {
                                ("II")
                            } else {
                                ("▶")
                            }
                        )
                    }
                }

                // live timer on HUD
                Text("time: ${formatTime(elapsedMs)}", color = Color.White, fontSize = 16.sp)
                Text("Time remaining: ${formatTime(timer)}", color = Color.White, fontSize = 16.sp)

                //debug values on screen
                fun Float.format(digits: Int): String = "%.${digits}f".format(this)

                Text("ax: ${sensorHandler.tiltX.format(2)}", color = Color.DarkGray, fontSize = 14.sp)
                Text("ay: ${sensorHandler.tiltY.format(2)}", color = Color.DarkGray, fontSize = 14.sp)
                Text("vx: ${velocityX.format(2)}", color = Color.DarkGray, fontSize = 14.sp)
                Text("vy: ${velocityY.format(2)}", color = Color.DarkGray, fontSize = 14.sp)
                Text("xcor: ${ballX.format(2)}", color = Color.DarkGray, fontSize = 14.sp)
                Text("ycor: ${ballY.format(2)}", color = Color.DarkGray, fontSize = 14.sp)
            }

        }
    }
}
