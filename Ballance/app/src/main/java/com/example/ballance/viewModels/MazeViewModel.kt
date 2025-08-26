package com.example.ballance.viewModels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.ballance.physics.CellType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ViewModel for managing the editable maze grid.
 *
 * Responsible for:
 * - Storing a 2D grid of cells as observable Compose states
 * - Updating individual cells when user taps in the editor
 * - Saving/loading maze layout to/from local storage as JSON
 *
 * Each cell in the grid is wrapped in [MutableState<CellType>], so Compose will
 * automatically recompose only the changed cell when editing.
 */
class MazeViewModel : ViewModel() {

    /** Number of rows in the maze grid */
    val rows = 16

    /** Number of columns in the maze grid */
    val cols = 36

    /**
     * 2D grid of observable [CellType]s.
     * Each cell is a [MutableState], allowing fine-grained recomposition.
     */
    var mazeGrid: Array<Array<MutableState<CellType>>> = Array(rows) {
        Array(cols) { mutableStateOf(CellType.EMPTY) }
    }
        private set

    /**
     * Updates a specific cell in the maze to a new type.
     *
     * @param row  The row index of the cell to update
     * @param col  The column index of the cell to update
     * @param type The new [CellType] to assign to this cell
     *
     * Triggers recomposition for just that cell.
     */
    fun setCell(row: Int, col: Int, type: CellType) {
        mazeGrid[row][col].value = type
    }

    /** Clears the entire grid to EMPTY (in-memory only). */
    fun clear() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                mazeGrid[r][c].value = CellType.EMPTY
            }
        }
    }

    /** Replace the in-memory grid from a serializable 2D list (no disk I/O). */
    fun setFromList(data: List<List<CellType>>) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                mazeGrid[r][c].value = data[r][c]
            }
        }
    }

    /** Returns a serializable snapshot of the current grid. */
    fun toSerializable(): List<List<CellType>> =
        mazeGrid.map { row -> row.map { it.value } }

    /**
     * Saves the current maze grid to local storage in JSON format.
     *
     * Converts the 2D grid of [MutableState<CellType>] into a serializable
     * 2D list of [CellType]s and writes it to a file named `"maze.json"`.
     *
     * @param context The Android [Context], used to open the file.
     */
    fun saveMaze(context: Context) {
        val jsonString = Json.Default.encodeToString(toSerializable())
        context.openFileOutput("maze.json", Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    /**
     * Loads a previously saved maze from local storage, replacing the current grid values.
     *
     * The saved maze must have the same 16x36 dimensions.
     * Each loaded [CellType] is copied back into the corresponding [MutableState].
     *
     * @param context The Android [Context], used to read the file.
     */
    fun loadMaze(context: Context) {
        try {
            val jsonString = context.openFileInput("maze.json").bufferedReader().readText()
            val loaded: List<List<CellType>> = Json.Default.decodeFromString(jsonString)
            setFromList(loaded)
        } catch (e: Exception) {
            // Likely cause: trying to load before saving anything.
            // No fallback: keep default maze.
        }
    }
}
