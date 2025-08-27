package com.example.ballance.utilities

import android.content.Context

/**
 * Stores the BEST time (in ms) for each packaged level (1..10).
 * - Persistent across app restarts/updates (cleared on uninstall/clear data).
 * - Super simple: SharedPreferences under the hood.
 */
object LevelTimesStore {

    private const val PREFS_NAME = "level_best_times_prefs"
    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun key(levelIndex: Int) = "best_ms_level_$levelIndex"

    /** True only for packaged levels we track. */
    private fun isTrackableLevel(levelIndex: Int) = levelIndex in 1..10

    /**
     * Get the stored best time for this level, or null if no record yet.
     *
     * @param levelIndex 1..10
     */
    fun getBestTime(context: Context, levelIndex: Int): Long? {
        if (!isTrackableLevel(levelIndex)) return null
        val stored = prefs(context).getLong(key(levelIndex), -1L)
        return if (stored >= 0) stored else null
    }

    /**
     * Record [candidateMs] IF it’s better (lower) than the stored time.
     *
     * @return true if this set a NEW RECORD, false otherwise.
     */
    fun recordIfBest(context: Context, levelIndex: Int, candidateMs: Long): Boolean {
        if (!isTrackableLevel(levelIndex)) return false

        val currentBest = getBestTime(context, levelIndex)
        val isNewRecord = (currentBest == null || candidateMs < currentBest)

        if (isNewRecord) {
            prefs(context).edit().putLong(key(levelIndex), candidateMs).apply()
        }
        return isNewRecord
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
