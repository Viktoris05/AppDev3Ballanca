package com.example.ballance.physics
import kotlinx.serialization.Serializable

/**
 * Enum representing the different types of cells in the maze grid.
 *
 * Each CellType corresponds to a tile with a distinct visual and interactive behavior:
 * - [EMPTY]: A passable tile with no side effects.
 * - [WALL]: An impassable barrier that blocks the ball.
 * - [FINISH]: A goal tile that triggers a level-complete effect when the ball enters.
 *
 * This enum is marked [Serializable] for saving/loading maze layouts as JSON.
 */
@Serializable
enum class CellType {
    EMPTY,   // Free space; the ball can pass through.
    WALL,    // Solid block; the ball bounces back on collision.
    FINISH   // Goal tile; reaching this may end the level or trigger a win condition.
}

/**
 * Maps a [CellType] to its corresponding [com.example.Ballance.Physics.CellBehavior], defining how the tile behaves.
 *
 * This allows rendering and logic code to remain independent of raw enum values,
 * and instead work with the polymorphic [com.example.Ballance.Physics.CellBehavior] interface.
 */
fun CellType.toBehavior(): CellBehavior = when (this) {
    CellType.EMPTY -> CellBehavior.Empty
    CellType.WALL -> CellBehavior.Wall
    CellType.FINISH -> CellBehavior.Finish
}

