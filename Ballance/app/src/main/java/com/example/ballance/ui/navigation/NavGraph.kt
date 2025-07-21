package com.example.ballance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ballance.ui.screens.*

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
            GameScreen(navController)
        }
        composable(Screen.LevelSelect.route) {
            LevelSelectScreen(navController)
        }
        composable(Screen.Editor.route) {
            EditorScreen(navController)
        }
        composable(Screen.Info.route) {
            InfoScreen(navController)
        }
    }
}
