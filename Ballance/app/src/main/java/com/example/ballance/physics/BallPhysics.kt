package com.example.ballance.physics

/**
 *
 * A BallPhysics implementation is responsible for:
 *  1. Consuming the current ball position and device tilt input.
 *  2. Integrating acceleration and friction over a real elapsed time (deltaTime).
 *  3. Performing collision checks against the maze grid.
 *  4. Producing a new, updated ball position.
 *
 * By accepting a deltaTime parameter, implementations remain framerate independent
 * and produce consistent behavior whether running at 30fps or 120fps.
 */
interface BallPhysics {

    /**
     * Advance the simulation by one timestep.
     *
     * @param currentX   Current horizontal position of the ball in pixels.
     * @param currentY   Current vertical   position of the ball in pixels.
     * @param tiltX      Accelerometer X value (left/right tilt). Positive = tilt right.
     * @param tiltY      Accelerometer Y value (up/down tilt).  Positive = tilt down.
     * @param cellSize   Size (in pixels) of one maze cell; used to detect which cells
     *                   the ball overlaps for collision.
     * @param maze       2D array of [com.example.ballance.physics.CellType] defining the maze layout and cell behaviors.
     * @param deltaTime  Time elapsed since the last update, in seconds. Ensures
     *                   acceleration and friction scale correctly with real time.
     *
     * @return A [Pair] of (newX, newY), the ball’s updated position in pixels.
     */
    fun update(
        currentX: Float,
        currentY: Float,
        tiltX: Float,
        tiltY: Float,
        cellSize: Float,
        maze: Array<Array<CellType>>,
        deltaTime: Float
    ): Pair<Float, Float>
}