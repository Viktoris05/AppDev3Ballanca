package com.example.ballance.UserInterface.Utilities

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize

/**
 * Utility class to convert coordinates between maze-space (game logic) and screen-space (Canvas rendering).
 *
 * Maze-space:
 *   - The logical coordinate system where the maze, ball, and tile logic live.
 *   - X and Y positions are based on maze cell layout, typically starting from (0,0).
 *
 * Screen-space:
 *   - The pixel coordinate system used by Jetpack Compose Canvas for drawing.
 *   - Extra padding or centering to align the maze within a larger screen area.
 *
 * @param cellSize Size of each maze cell in pixels.
 * @param canvasSize Full canvas dimensions (in pixels), passed from `DrawScope.size`.
 * @param numCols Number of columns in the maze (horizontal cells).
 * @param numRows Number of rows in the maze (vertical cells).
 *
 * This mapper precomputes the maze’s on-screen size and its offset to center it on the canvas.
 */
class WorldToScreenMapper(
    private val cellSize: Float,
    canvasSize: IntSize,
    private val numCols: Int,
    private val numRows: Int
) {
    // Total maze width/height in pixels
    private val mazeWidth = numCols * cellSize
    private val mazeHeight = numRows * cellSize

    // Offset to center the maze in the available canvas
    private val offsetX = (canvasSize.width - mazeWidth) / 2f
    private val offsetY = (canvasSize.height - mazeHeight) / 2f

    /**
     * Converts a maze-space position (x, y in pixels) to screen-space.
     *
     * Used for rendering the ball or other dynamic elements.
     *
     * @param x X position in maze-space (typically ballX).
     * @param y Y position in maze-space (typically ballY).
     * @return A screen-space Offset used for drawing on Canvas.
     */
    fun toScreen(x: Float, y: Float): Offset {
        return Offset(x + offsetX, y + offsetY)
    }

    /**
     * Computes the top-left corner of a maze cell in screen-space.
     *
     * Used when drawing static tiles like walls or finish zones.
     *
     * @param row The maze row index (0-based).
     * @param col The maze column index (0-based).
     * @return A screen-space Offset for the top-left corner of the cell.
     */
    fun cellTopLeft(row: Int, col: Int): Offset {
        val left = col * cellSize + offsetX
        val top = row * cellSize + offsetY
        return Offset(left, top)
    }

    /**
     * Returns the on-screen size of a single maze cell.
     *
     * Since the maze is uniform, this is the same for all cells.
     *
     * @return A `Size` object with width and height equal to cellSize.
     */
    fun cellSize(): Size = Size(cellSize, cellSize)
}