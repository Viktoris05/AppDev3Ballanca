package com.example.ballance.physics

/**
 * Concrete physics engine that uses tilt input to simulate gravity-based ball motion.
 *
 * This class extends [BaseMazePhysics] and passes in parameters such as ball radius,
 * gravity strength, friction factor, and max speed.
 *
 * It inherits the default four-phase update cycle:
 * 1. Apply tilt-based acceleration with time scaling.
 * 2. Apply velocity changes with per-frame friction.
 * 3. Detect and handle wall collisions (horizontal and vertical).
 * 4. Trigger any tile-specific effects when entering a new cell.
 *
 * You can subclass this further or override individual methods in [BaseMazePhysics]
 * to customize the behavior (e.g., sticky tiles, momentum zones, slippery floors).
 *
 * @param ballRadius       Radius of the ball in pixels.
 * @param gravityStrength  How much tilt affects acceleration (px/s²).
 * @param frictionFactor   Friction applied each second (0.0–1.0). Lower = more drag.
 * @param maxSpeed         Upper limit for ball velocity (px/s).
 */
class TiltGravityPhysics(
    ballRadius: Float = 10f,
    gravityStrength: Float = 20f,
    frictionFactor: Float = 0.50f,
    maxSpeed: Float = 12f
) : BaseMazePhysics(ballRadius, gravityStrength, frictionFactor, maxSpeed) {

    // If needed, override applyPhysics(), resolveHorizontal(), etc. here.
    // For now, this uses BaseMazePhysics exactly as-is. Even creating multiple classes and

}