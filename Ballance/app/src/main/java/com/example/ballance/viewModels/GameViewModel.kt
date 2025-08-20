package com.example.ballance.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ballance.physics.CellType
import com.example.ballance.physics.TiltGravityPhysics
import kotlinx.serialization.json.Json

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
    private var maze: Array<Array<CellType>> = Array(15) { Array(15) { CellType.EMPTY } }

    // Physics engine for ball motion
    private lateinit var physics: TiltGravityPhysics

    // Ball position in pixel coordinates (world-space)
    private var ballX = 0f
    private var ballY = 0f

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
            maze = Array(15) { r -> Array(15) { c -> list[r][c] } }
        } catch (e: Exception) {
            // Leave default empty maze on load failure
        }

        // Initialize ball state and physics engine
        val cellSize = 70f
        physics = TiltGravityPhysics(ballRadius = cellSize / 2.5f)
        ballX = cellSize * (maze[0].size / 2)
        ballY = cellSize * (maze.size / 2)
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
}