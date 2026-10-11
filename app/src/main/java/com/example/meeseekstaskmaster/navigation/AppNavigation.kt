package com.example.meeseekstaskmaster.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.meeseekstaskmaster.data.repository.SettingsRepository
import com.example.meeseekstaskmaster.data.repository.TaskRepository
import com.example.meeseekstaskmaster.view.screens.CrisisAlarmScreen
import com.example.meeseekstaskmaster.view.screens.HistoryScreen
import com.example.meeseekstaskmaster.view.screens.HomeScreen
import com.example.meeseekstaskmaster.view.screens.SettingsScreen
import com.example.meeseekstaskmaster.view.screens.SplashScreen
import com.example.meeseekstaskmaster.viewmodel.CrisisViewModel
import com.example.meeseekstaskmaster.viewmodel.HistoryViewModel
import com.example.meeseekstaskmaster.viewmodel.HomeViewModel
import com.example.meeseekstaskmaster.viewmodel.SettingsViewModel
import com.example.meeseekstaskmaster.viewmodel.ViewModelFactory

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Crisis : Screen("crisis/{taskId}") {
        fun createRoute(taskId: Long): String = "crisis/$taskId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    taskRepository: TaskRepository,
    settingsRepository: SettingsRepository,
    initialRoute: String = Screen.Splash.route
) {
    val factory = ViewModelFactory(taskRepository, settingsRepository)

    NavHost(
        navController = navController,
        startDestination = initialRoute
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateToCrisis = { taskId ->
                    navController.navigate(Screen.Crisis.createRoute(taskId))
                }
            )
        }

        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel(factory = factory)
            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Crisis.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            val crisisViewModel: CrisisViewModel = viewModel(factory = factory)
            CrisisAlarmScreen(
                taskId = taskId,
                viewModel = crisisViewModel,
                onNavigateBack = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
