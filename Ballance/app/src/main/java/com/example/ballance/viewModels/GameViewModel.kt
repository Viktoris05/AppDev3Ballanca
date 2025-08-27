package com.example.ballance.viewModels

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.example.ballance.physics.CellType
import com.example.ballance.physics.TiltGravityPhysics
import com.example.ballance.utilities.BallMovementStore
import kotlinx.serialization.json.Json
import com.example.ballance.utilities.LevelTimesStore
import com.example.ballance.utilities.LevelSession

/**
 * ViewModel for the game screen.
 *
 * Manages:
 * - Loading the maze from persistent storage (JSON).
 * - Holding the ball's current position and delegating physics updates.
 * - Providing maze and velocity info to the UI layer.
 *
 * Physics updates are delegated to a [TiltGravityPhysics] instance.
 */
class GameViewModel(application: Application) : AndroidViewModel(application) {

    // Android context reference for file I/O
    private val context = getApplication<Application>()

    // Maze grid stored as a 2D array of CellType
    private var maze: Array<Array<CellType>> = Array(16) { Array(36) { CellType.EMPTY } } //18:8 because the emulator uses that ratio (16:9 is the most common though)

    // Physics engine for ball motion
    private lateinit var physics: TiltGravityPhysics

    // Ball position in pixel coordinates (world-space)
    var ballX = 0f
        private set
    var ballY = 0f
        private set

    // imer state (per run)
    // Uses elapsedRealtime so it survives frame dropping and isnt tied to frame dt.
    private var timerRunning = false
    private var timerStartRealtime = 0L
    private var timerAccumulatedMs = 0L

    init {
        loadMaze()
    }

    /**
     * Loads a saved maze from internal storage (maze.json), if available.
     * Falls back to an empty maze on failure.
     *
     * Also resets ball position and physics engine.
     */
    fun loadMaze() {
        try {
            val json = context.openFileInput("maze.json").bufferedReader().readText()
            val list = Json.Default.decodeFromString<List<List<CellType>>>(json)
            maze = Array(16) { r -> Array(36) { c -> list[r][c] } }
        } catch (e: Exception) {
            // Leave default empty maze on load failure
        }

        // Initialize ball state and physics engine
        val cellSize = 66f
        physics = TiltGravityPhysics(ballRadius = cellSize / 2.5f)

        // place ball on STARTINGTILE if present; otherwise center using the variables in BaseMazePhysics.kt
        var startRow = -1
        var startCol = -1
        // loop with a found flag to exit both loops
        var found = false
        for (r in maze.indices) {
            for (c in maze[0].indices) {
                if (maze[r][c] == CellType.STARTINGTILE) {
                    startRow = r; startCol = c; found = true
                    break
                }
            }
            if (found) break
        }
        if (startRow >= 0 && startCol >= 0) {
            val spawnX = (startCol + 0.5f) * cellSize
            val spawnY = (startRow + 0.5f) * cellSize
            ballX = spawnX
            ballY = spawnY
            physics.setRespawn(spawnX, spawnY)
        } else {
            val centerX = cellSize * (maze[0].size / 2)
            val centerY = cellSize * (maze.size / 2)
            ballX = centerX
            ballY = centerY
            physics.setRespawn(centerX, centerY)
        }

        BallMovementStore.loadBestMovement(context, LevelSession.currentLevelIndex)

        // Reset run timer for the (re)loaded level; GameScreen resumes it when unpaused.
        resetTimer()
    }

    /**
     * Advances the simulation one step forward using the current tilt input.
     *
     * Delegates to [com.example.ballance.Physics.BaseMazePhysics.update], then updates the internal
     * position state. This method is called from the game loop.
     *
     * @param ax        X-axis tilt (accelerometer).
     * @param ay        Y-axis tilt (accelerometer).
     * @param cellSize  Size of one cell in pixels.
     * @param deltaTime Time since last frame (in seconds).
     * @return New (x, y) position of the ball in pixels.
     */
    fun update(
        ax: Float,
        ay: Float,
        cellSize: Float,
        deltaTime: Float
    ): Pair<Float, Float> {
        if (!::physics.isInitialized) loadMaze()

        val (newX, newY) = physics.update(
            currentX = ballX,
            currentY = ballY,
            tiltX = ax,
            tiltY = ay,
            cellSize = cellSize,
            maze = maze,
            deltaTime = deltaTime
        )

        ballX = newX
        ballY = newY
        return ballX to ballY
    }

    /** Returns the current maze layout. */
    fun getMaze() = maze

    /** Returns the ball's current horizontal velocity (px/s). */
    fun getVelocityX() = if (::physics.isInitialized) physics.velocityX else 0f

    /** Returns the ball's current vertical velocity (px/s). */
    fun getVelocityY() = if (::physics.isInitialized) physics.velocityY else 0f

    // timer additions lowkey api used by game screen

    /** Total elapsed run time in milliseconds (pause-aware). */
    fun getElapsedMillis(): Long {
        return if (timerRunning) {
            timerAccumulatedMs + (SystemClock.elapsedRealtime() - timerStartRealtime)
        } else {
            timerAccumulatedMs
        }
    }

    /** Resume counting time. */
    fun resumeTimer() {
        if (!timerRunning) {
            timerRunning = true
            timerStartRealtime = SystemClock.elapsedRealtime()
        }
    }

    /** Pause counting time. */
    fun pauseTimer() {
        if (timerRunning) {
            timerAccumulatedMs += SystemClock.elapsedRealtime() - timerStartRealtime
            timerRunning = false
        }
    }

    /** Reset timer to 0 and set paused. */
    fun resetTimer() {
        timerRunning = false
        timerStartRealtime = 0L
        timerAccumulatedMs = 0L
    }

    /**
     * Called when the Finish tile is reached (from GameScreen before navigation).
     * - Freezes the timer and stores the last run in LevelSession.
     * - Updates best time (packaged levels 1..10 only).
     */
    fun markVictory() {
        // freeze timer
        pauseTimer()

        val elapsed = getElapsedMillis()
        LevelSession.lastRunMillis = elapsed

        val idx = LevelSession.currentLevelIndex
        if (idx != null && idx in 1..10) {
            LevelTimesStore.recordIfBest(context, idx, elapsed)
        }
    }

    /** For pause UI: best time for current packaged level, or null. */
    fun getBestTimeForCurrent(): Long? {
        val idx = LevelSession.currentLevelIndex
        return if (idx != null && idx in 1..10) {
            LevelTimesStore.getBestTime(context, idx)
        } else null
    }

    /** For pause UI: returns Level Number as a String, or returns "Custom" for custom levels*/
    fun getNameForCurrent(): String? {
        val idx = LevelSession.currentLevelIndex
        if(idx != null && idx in 1 .. 10){
            return "$idx"
        }else{
            return ": Custom"
        }
    }
}
