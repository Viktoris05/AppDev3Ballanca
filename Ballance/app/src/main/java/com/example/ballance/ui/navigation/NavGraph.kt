package com.example.ballance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ballance.viewModels.GameViewModel
import com.example.ballance.ui.screens.*
import com.example.ballance.viewModels.MazeViewModel

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object Game : Screen("game")
    object LevelSelect : Screen("level_select")
    object Editor : Screen("editor")
    object Info : Screen("info")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.MainMenu.route) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(navController)
        }
        composable(Screen.Game.route) {
            val gameViewModel: GameViewModel = viewModel()
            GameScreen(gameViewModel, navController)
        }
        composable(Screen.LevelSelect.route) {
            LevelSelectScreen(navController)
        }
        composable(Screen.Editor.route) {
            val mazeVm: MazeViewModel = viewModel()
            EditorScreen(navController, mazeVm)
        }
        composable(Screen.Info.route) {
            InfoScreen(navController)
        }
    }
}
