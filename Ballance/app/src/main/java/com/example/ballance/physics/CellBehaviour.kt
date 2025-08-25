package com.example.ballance.physics

import com.example.ballance.ui.screens.GameScreen
import com.example.ballance.ui.screens.utilities.GameCanvas

/**
 * Represents the behavior associated with a specific maze cell.
 *
 * Each `CellType` in the maze maps to a `CellBehavior`, which defines:
 * - Whether the ball is allowed to move into that tile (`allowsMovement`)
 * - What happens when the ball enters the tile (`onEnter`)
 *
 * Subclasses override the behavior as needed. For example:
 * - `Wall` blocks movement.
 * - `Finish` may trigger a victory callback or event.
 */
sealed class CellBehavior {

    /**
     * Returns whether the ball can move into this tile.
     * Default is true (passable).
     */
    open fun allowsMovement(): Boolean = true

    open fun isInBlackHole(): Boolean = false

    /**
     * Optional callback triggered when the ball enters this tile.
     * Default is no effect.
     */
    open fun onEnter(): (() -> Unit)? = null

    /** Represents an empty/passable cell with no effect. */
    object Empty : CellBehavior()

    /** Represents a wall tile: impassable by the ball. */
    object Wall : CellBehavior() {
        override fun allowsMovement() = false
    }

    /** Represents a finish tile: triggers an effect when reached. */
    object Finish : CellBehavior() {
        var onVictoryCallback: (() -> Unit)? = null

        override fun onEnter(): (() -> Unit)? = {
            onVictoryCallback?.invoke()
        }
    }

    /** Represents a slowdown tile: ball touching this tile will slow down significantly */
    object Slowdown : CellBehavior() {

    }

    /** Represents a speedup tile: ball touching this tile will speed up significantly */
    object Speedup : CellBehavior() {

    }

    /** Represents an imposter hole. If the ball falls into this hole the ball will respawn */
    object Blackhole : CellBehavior() {

    }

}
