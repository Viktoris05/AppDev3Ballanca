package com.example.ballance.utilities

/** Holds the currently selected packaged level (1..10) and last run time. */
object LevelSession {
    var currentLevelIndex: Int? = null
    var lastRunMillis: Long = 0L
}
