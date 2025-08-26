package com.example.ballance.utilities

import android.content.Context
import com.example.ballance.R
import com.example.ballance.physics.CellType
import kotlinx.serialization.json.Json

/**
 * Loads default levels from res/raw as JSON grids (List<List<CellType>>).
 * IMPORTANT !!! Place files in res/raw: level01.json ... level10.json pay attention to format..
 */
object LevelPack {

    data class Entry(val index: Int, val name: String, val resId: Int)

    // Register packaged levels here (IDs must exist in res/raw).
    val entries: List<Entry> = listOf(
        Entry(1,  "Level 1", R.raw.level01),
        Entry(2,  "Level 2", R.raw.level02),
        Entry(3,  "Level 3", R.raw.level03),
        Entry(4,  "Level 4", R.raw.level04),
        Entry(5,  "Level 5", R.raw.level05),
        Entry(6,  "Level 6", R.raw.level06),
        Entry(7,  "Level 7", R.raw.level07),
        Entry(8,  "Level 8", R.raw.level08),
        Entry(9,  "Level 9", R.raw.level09),
        Entry(10, "Level 10", R.raw.level10),
    )


    fun load(context: Context, index: Int): List<List<CellType>> {
        var resId = 0
        for (e in entries) {
            if (e.index == index) { resId = e.resId; break }
        }
        if (resId == 0) throw IllegalArgumentException("Invalid level index: $index")

        val json = context.resources.openRawResource(resId)
            .bufferedReader()
            .use { it.readText() }

        return Json.Default.decodeFromString<List<List<CellType>>>(json)
    }

}