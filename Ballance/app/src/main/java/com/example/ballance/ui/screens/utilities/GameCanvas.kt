package com.example.ballance.ui.screens.utilities

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.example.ballance.physics.CellType
import com.example.ballance.ui.theme.accentColor
import com.example.ballance.ui.theme.finishColor
import com.example.ballance.ui.theme.redWallColor
import com.example.ballance.ui.theme.slowdownColor
import com.example.ballance.ui.theme.speedupColor

/**
 * Composable responsible for drawing the current state of the maze and ball.
 *
 * This includes:
 * - Rendering the 2D grid of [CellType] tiles.
 * - Drawing the ball at its current (x, y) world coordinates.
 * - Mapping world-space positions to screen-space via [WorldToScreenMapper].
 *
 * @param maze     2D array representing the logical structure of the maze.
 * @param ballX    Ball's current horizontal position in world-space pixels.
 * @param ballY    Ball's current vertical position in world-space pixels.
 * @param cellSize Size of one maze cell in pixels (world units).
 */
@Composable
fun GameCanvas(
    maze: Array<Array<CellType>>,
    ballX: Float,
    ballY: Float,
    cellSize: Float
) {
    Canvas(Modifier.fillMaxSize()) {
        // Screen dimensions (in pixels) at draw time
        val canvasSize = IntSize(size.width.toInt(), size.height.toInt())

        // Converts between world coordinates and screen pixels
        val mapper = WorldToScreenMapper(
            cellSize = cellSize,
            canvasSize = canvasSize,
            numCols = maze[0].size,
            numRows = maze.size
        )

        // Draw maze tiles
        for (row in maze.indices) {
            for (col in maze[row].indices) {
                // Determine top-left pixel of this tile on screen
                val topLeft = mapper.cellTopLeft(row, col)

                // Color by type
                val color = when (maze[row][col]) {
                    CellType.EMPTY -> Color.Black
                    CellType.WALL -> accentColor
                    CellType.FINISH -> finishColor
                    CellType.SLOWDOWN -> slowdownColor
                    CellType.SPEEDUP -> speedupColor
                    CellType.REDWALL -> redWallColor
                    CellType.STARTINGTILE -> Color.Transparent
                }

                // Draw the rectangle for the tile
                drawRect(
                    color = color,
                    topLeft = topLeft,
                    size = mapper.cellSize()
                )
            }
        }

        // Draw the ball at its screen-mapped position
        val ballCenter = mapper.toScreen(ballX, ballY)
        drawCircle(
            color = Color.Blue,
            radius = cellSize / 2.5f, // ball is smaller than a cell
            center = ballCenter
        )
    }
}
