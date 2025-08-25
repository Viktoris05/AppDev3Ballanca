package com.example.ballance.physics
import kotlin.math.hypot
import kotlin.math.pow

/**
 * Abstract base for tilt-based maze physics.
 *
 * This "engine" simulates ball movement using:
 *  - tilt based acceleration (tiltX, tiltY)
 *  - per second clamped acceleration (maxAccel) - clamping refers to additional calculations to refine movement from raw data input from the accelerator
 *  - friction (frictionPerSecond raised to deltaTime)
 *  - collision detection against maze walls
 *  - optional tile behavior
 *
 * The physics update occurs in 4 phases:
 *  1. applyPhysics:      integrate acceleration and apply friction
 *  2. resolveHorizontal: move left/right and handle wall collision
 *  3. resolveVertical:   move up/down and handle floor/ceiling collision
 *  4. triggerEffect:     call the onEnter() function of the current tile
 *
 * Subclasses can override any of these phases.
 *
 * @param ballRadius        Radius of the ball in pixels (used for collision detection)
 * @param gravityStrength   How strong tilt affects acceleration (px/s²)
 * @param frictionPerSecond Speed retention factor per second (0.0–1.0) raised to deltaTime
 * @param maxSpeed          Maximum allowed velocity (px/s)
 */
abstract class BaseMazePhysics(
    private val ballRadius: Float = 10f,
    private val gravityStrength: Float = 20f,
    private val frictionPerSecond: Float = 0.70f,
    private val maxSpeed: Float = 12f
) : BallPhysics {

    // Velocity components (in pixels per frame)
    var velocityX = 0f
    var velocityY = 0f

    // Max acceleration in px/s², clamped per frame using deltaTime
    private val maxAccel = 8f

    /** Simple mutable 2D position holder */
    data class Pos(var x: Float, var y: Float)

    /**
     * Full simulation step for a single frame.
     * Called externally once per frame.
     *
     * @param currentX  Current X position of ball (px)
     * @param currentY  Current Y position of ball (px)
     * @param tiltX     Horizontal tilt from accelerometer
     * @param tiltY     Vertical tilt from accelerometer
     * @param cellSize  Size of each maze tile in pixels
     * @param maze      Maze grid (2D array of tiles)
     * @param deltaTime Time elapsed since last frame (seconds)
     * @return Updated (x, y) position after applying physics
     */
    override fun update(
        currentX: Float,
        currentY: Float,
        tiltX: Float,
        tiltY: Float,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        deltaTime: Float
    ): Pair<Float, Float> {
        val rowCount = maze.size
        val colCount = maze[0].size
        val pos = Pos(currentX, currentY)

        // Apply acceleration, friction, and clamp speed
        applyPhysics(tiltX, tiltY, deltaTime)
        resolveSlowDown(pos, cellSize, maze, rowCount, colCount)
        resolveSpeedUp(pos, cellSize, maze, rowCount, colCount)

        // Move horizontally and handle wall collisions
        resolveHorizontal(pos, cellSize, maze, rowCount, colCount)

        // Move vertically and handle floor/ceiling collisions
        resolveVertical(pos, cellSize, maze, rowCount, colCount)

        // Trigger cell-specific effects like goal detection
        triggerEffect(pos, cellSize, maze, rowCount, colCount)

        // Check if the ball touches a Red Wall
        resolveRedWall(pos, cellSize, maze, rowCount, colCount)

        return pos.x to pos.y
    }

    /**
     * Integrate acceleration into velocity, apply time-scaled friction,
     * and clamp final speed to maxSpeed.
     *
     * Tilt direction affects acceleration, which is clamped to maxAccel
     * to avoid instant high speeds. Friction is applied as:
     *   velocity *= frictionPerSecond^deltaTime
     */
    open fun applyPhysics(tiltX: Float, tiltY: Float, deltaTime: Float) {
        // Convert tilt into raw change in velocity (px/frame)
        val dvxRaw = tiltX * gravityStrength * deltaTime
        val dvyRaw = tiltY * gravityStrength * deltaTime

        // Clamp acceleration this frame to avoid huge jumps
        val maxDvThisFrame = maxAccel * deltaTime
        val dvx = dvxRaw.coerceIn(-maxDvThisFrame, maxDvThisFrame)
        val dvy = dvyRaw.coerceIn(-maxDvThisFrame, maxDvThisFrame)

        // Update velocity
        velocityX += dvx
        velocityY += dvy

        // Apply per-frame friction based on real time
        val frameFriction = frictionPerSecond.pow(deltaTime)
        velocityX *= frameFriction
        velocityY *= frameFriction

        // Clamp total speed to maxSpeed
        val speed = hypot(velocityX, velocityY)
        if (speed > maxSpeed) {
            val scale = maxSpeed / speed
            velocityX *= scale
            velocityY *= scale
        }
    }

    /**
     * Apply horizontal movement using velocityX.
     * If there's a wall, bounce and reverse velocity.
     */
    open fun resolveHorizontal(
        pos: Pos,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        rows: Int,
        cols: Int
    ) {
        val tentativeX = pos.x + velocityX

        // Which rows the ball touches
        val topRow = ((pos.y - ballRadius) / cellSize).toInt().coerceIn(0, rows - 1)
        val bottomRow = ((pos.y + ballRadius) / cellSize).toInt().coerceIn(0, rows - 1)

        // Which column it hits on left/right
        val leftCol = ((tentativeX - ballRadius) / cellSize).toInt().coerceIn(0, cols - 1)
        val rightCol = ((tentativeX + ballRadius) / cellSize).toInt().coerceIn(0, cols - 1)

        // Check wall in direction of motion
        val hitWall = (velocityX < 0 && blocksMovement(
            maze[topRow][leftCol], maze[bottomRow][leftCol]
        )) || (velocityX > 0 && blocksMovement(
            maze[topRow][rightCol], maze[bottomRow][rightCol]
        ))

        if (hitWall) {
            // Bounce with energy loss
            velocityX *= -0.4f
        } else {
            pos.x = tentativeX
        }
    }

    /**
     * Apply vertical movement using velocityY.
     * Bounce on ceiling/floor collision.
     */
    open fun resolveVertical(
        pos: Pos,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        rows: Int,
        cols: Int
    ) {
        val tentativeY = pos.y + velocityY

        // Which columns the ball overlaps
        val leftCol = ((pos.x - ballRadius) / cellSize).toInt().coerceIn(0, cols - 1)
        val rightCol = ((pos.x + ballRadius) / cellSize).toInt().coerceIn(0, cols - 1)

        // Which rows top/bottom
        val topRow = ((tentativeY - ballRadius) / cellSize).toInt().coerceIn(0, rows - 1)
        val bottomRow = ((tentativeY + ballRadius) / cellSize).toInt().coerceIn(0, rows - 1)

        val hitCeilingOrFloor = (velocityY < 0 && (
                blocksMovement(maze[topRow][leftCol], maze[topRow][rightCol])))
                || (velocityY > 0 && blocksMovement(
            maze[bottomRow][leftCol], maze[bottomRow][rightCol]
        ))

        if (hitCeilingOrFloor) {
            velocityY *= -0.4f
        } else {
            pos.y = tentativeY
        }
    }

    /**
     * Return true if any of the given tiles block ball movement.
     */
    open fun blocksMovement(vararg tiles: CellType): Boolean {
        return tiles.any { !it.toBehavior().allowsMovement() }
    }

    /**
     * Return true if any of the given tiles is a red wall.
     */
    open fun redWall(vararg tiles: CellType): Boolean{
        return tiles.any { it.toBehavior().isInRedWall() }
    }

    /**
     * Check if the ball touches a Red Wall.
     * Respawn the ball is yes
     */
    open fun resolveRedWall(
        pos: Pos,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        rowCount: Int,
        colCount: Int
    ){

        // Which columns the ball overlaps
        val leftCol = ((pos.x - ballRadius) / cellSize).toInt().coerceIn(0, colCount - 1)
        val rightCol = ((pos.x + ballRadius) / cellSize).toInt().coerceIn(0, colCount - 1)

        // Which rows top/bottom
        val topRow = ((pos.y - ballRadius) / cellSize).toInt().coerceIn(0, rowCount - 1)
        val bottomRow = ((pos.y + ballRadius) / cellSize).toInt().coerceIn(0, rowCount - 1)

        val touchedRedWall = (redWall(maze[topRow][leftCol],maze[bottomRow][rightCol]))

        if(touchedRedWall){
            pos.x = cellSize * (maze[0].size / 2)
            pos.y = cellSize * (maze.size / 2)
        }
    }

    /**
     * Returns true if the ball touches a SlowDown tile
     */
    open fun inSlowDown(vararg tiles: CellType): Boolean{
        return tiles.any { it.toBehavior().SlowDownOn() }
    }

    /**
     * Checks for the Ball getting into a SlowDown tile and slows the ball down
     */
    open fun resolveSlowDown(
        pos: Pos,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        rowCount: Int,
        colCount: Int
    ) {

        // Which columns the ball overlaps
        val leftCol = ((pos.x - ballRadius) / cellSize).toInt().coerceIn(0, colCount - 1)
        val rightCol = ((pos.x + ballRadius) / cellSize).toInt().coerceIn(0, colCount - 1)

        // Which rows top/bottom
        val topRow = ((pos.y - ballRadius) / cellSize).toInt().coerceIn(0, rowCount - 1)
        val bottomRow = ((pos.y + ballRadius) / cellSize).toInt().coerceIn(0, rowCount - 1)

        val touchedSlowDown = (inSlowDown(maze[topRow][leftCol],maze[bottomRow][rightCol]))

        if(touchedSlowDown){
            velocityX = velocityX / 2
            velocityY = velocityY / 2
        }
    }

    /**
     * Returns true if the ball touches a SpeedUp tile
     */
    open fun inSpeedUp(vararg tiles: CellType): Boolean{
        return tiles.any { it.toBehavior().SpeedUpOn() }
    }

    /**
     * Checks for the Ball getting into a SpeedUp tile and makes the ball faster
     */
    open fun resolveSpeedUp(
        pos: Pos,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        rowCount: Int,
        colCount: Int
    ) {

        // Which columns the ball overlaps
        val leftCol = ((pos.x - ballRadius) / cellSize).toInt().coerceIn(0, colCount - 1)
        val rightCol = ((pos.x + ballRadius) / cellSize).toInt().coerceIn(0, colCount - 1)

        // Which rows top/bottom
        val topRow = ((pos.y - ballRadius) / cellSize).toInt().coerceIn(0, rowCount - 1)
        val bottomRow = ((pos.y + ballRadius) / cellSize).toInt().coerceIn(0, rowCount - 1)

        val touchedSpeedUp = (inSpeedUp(maze[topRow][leftCol],maze[bottomRow][rightCol]))

        if(touchedSpeedUp){
            velocityX *= 2
            velocityY *= 2
        }
    }

    /**
     * Trigger the onEnter callback for the tile currently under the ball’s center.
     * Used to handle game logic like reaching a goal tile.
     */
    open fun triggerEffect(
        pos: Pos,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        rows: Int,
        cols: Int
    ) {
        val row = (pos.y / cellSize).toInt().coerceIn(0, rows - 1)
        val col = (pos.x / cellSize).toInt().coerceIn(0, cols - 1)
        maze[row][col].toBehavior().onEnter()?.invoke()
    }
}