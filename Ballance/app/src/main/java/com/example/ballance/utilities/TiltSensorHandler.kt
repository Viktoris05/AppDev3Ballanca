package com.example.ballance.Utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * TiltSensorHandler listens to the accelerometer sensor and provides real-time tilt data.
 *
 * This class acts as a thin wrapper around Android's accelerometer system to track:
 * - Horizontal tilt (tiltX): Negative when tilting left, positive when tilting right
 * - Vertical tilt (tiltY):   Positive when tilting forward/down, negative when tilting backward/up
 *
 * ## Usage
 * - Call [register] when you want to start listening to tilt (e.g., on screen enter)
 * - Call [unregister] to stop receiving updates (e.g., on screen exit)
 *
 * ## Exposed Properties
 * - [tiltX]: Horizontal acceleration (scaled)
 * - [tiltY]: Vertical acceleration (scaled)
 */
class TiltSensorHandler(context: Context) : SensorEventListener {

    // Internal mutable state for horizontal tilt (X-axis)
    private var _tiltX by mutableStateOf(0f)

    // Internal mutable state for vertical tilt (Y-axis)
    private var _tiltY by mutableStateOf(0f)

    /** Public read-only horizontal tilt value */
    val tiltX: Float get() = _tiltX

    /** Public read-only vertical tilt value */
    val tiltY: Float get() = _tiltY

    // System sensor manager to access hardware sensors
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Reference to the device's built-in accelerometer
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    /**
     * Starts listening to accelerometer data.
     * Uses SENSOR_DELAY_GAME for ~50Hz update rate.
     */
    fun register() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    /**
     * Stops listening to accelerometer events.
     */
    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Called when new accelerometer data is available.
     * Converts the raw X and Y axis values into tilt directions for the game.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Read the X-axis tilt (left/right), no inversion:
            // - Tilting right should give a POSITIVE value
            // - Tilting left should give a NEGATIVE value
            // Multiply by 2 to increase horizontal sensitivity
            _tiltX = event.values[1] * 2

            // Read the Y-axis tilt (up/down), no inversion:
            // - Tilting down gives POSITIVE
            // - Tilting up gives NEGATIVE
            // Multiply by 2 to increase vertical sensitivity
            _tiltY = event.values[0] * 2
        }
    }


    /**
     * Not used, but required to implement SensorEventListener.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}