package com.example.ballance.utilities

import android.content.Context
import android.util.Log
import com.example.ballance.physics.BaseMazePhysics
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Stores the BEST ball movement until finish for each packaged level (1..10).
 * - Persistent across app restarts/updates (cleared on uninstall/clear data).*/
object BallMovementStore {
    private const val PREFS_NAME = "level_best_movement_prefs"
    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun key(levelIndex: Int) = "best_ms_level_$levelIndex"

    /** True only for packaged levels we track. */
    private fun isTrackableLevel(levelIndex: Int?) = levelIndex in 1..10

    val movementStore:Array<Pair<Float, Float>?> = Array(60000){ null }
    var bestMovement:Array<Pair<Float, Float>?> = Array(60000){ null }


    fun addMovement(elapsedTime: Long, pos: Pair<Float, Float>){
        movementStore[elapsedTime.toInt()/10] = pos
        Log.d("BallMovementStore", "Add with $elapsedTime: $pos")
    }

    fun getGhostMovement(elapsedTime: Long): Pair<Float, Float>? {
        val pos = bestMovement[elapsedTime.toInt()/10]
        Log.d("BallMovementStore", "Load with $elapsedTime: $pos")
        return pos
    }

    /**
     * Get the stored best movement for this level, or null if no record yet.
     *
     * @param levelIndex 1..10
     */
    fun loadBestMovement(context: Context, levelIndex: Int?) {
        if (!isTrackableLevel(levelIndex)) return
        val fileName = "BestTrackLevel$levelIndex"
        val file = File(context.filesDir, fileName)
        if(!file.exists()) {
            bestMovement = Array(60000) {null}
        }else {
            ObjectInputStream(FileInputStream(file)).use { ois ->
                bestMovement = ois.readObject() as Array<Pair<Float, Float>?>
            }
            for(i in 1..60000 - 1){
                if(bestMovement[i] == null){
                    bestMovement[i] = bestMovement[i - 1]
                }
            }
        }
    }

    /**
     * Record [candidateMs] IF it’s better (lower) than the stored Array.
     *
     * @return true if this set a NEW RECORD, false otherwise.
     */
    fun record(context: Context, levelIndex: Int) {
        val fileName = "BestTrackLevel$levelIndex"
        val file = File(context.filesDir, fileName)
        ObjectOutputStream(FileOutputStream(file)).use { oos ->
            oos.writeObject(movementStore)
        }
    }

    // debugging functions
    fun clearLevel(context: Context, levelIndex: Int) {
        if (!isTrackableLevel(levelIndex)) return
        prefs(context).edit().remove(key(levelIndex)).apply()
    }


    fun clearAll(context: Context) {
        prefs(context).edit().clear().apply()
    }
}