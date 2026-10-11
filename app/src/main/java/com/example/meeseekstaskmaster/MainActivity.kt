package com.example.meeseekstaskmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.meeseekstaskmaster.navigation.AppNavigation
import com.example.meeseekstaskmaster.navigation.Screen
import com.example.meeseekstaskmaster.service.NotificationService
import com.example.meeseekstaskmaster.view.theme.MeeseeksTaskMasterTheme
import com.example.meeseekstaskmaster.view.theme.SpaceDark

class MainActivity : ComponentActivity() {

    private val pendingCrisisTaskId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleCrisisIntent(intent)

        val app = application as MeeseeksApplication

        setContent {
            MeeseeksTaskMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SpaceDark
                ) {
                    val navController = rememberNavController()

                    // Si se abrió desde una notificación de crisis, navegamos a la pantalla de crisis
                    LaunchedEffect(pendingCrisisTaskId.value) {
                        val taskId = pendingCrisisTaskId.value
                        if (taskId != null) {
                            navController.navigate(Screen.Crisis.createRoute(taskId)) {
                                popUpTo(Screen.Home.route)
                            }
                            pendingCrisisTaskId.value = null
                        }
                    }

                    AppNavigation(
                        navController = navController,
                        taskRepository = app.taskRepository,
                        settingsRepository = app.settingsRepository
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleCrisisIntent(intent)
    }

    private fun handleCrisisIntent(intent: Intent?) {
        if (intent?.action == NotificationService.ACTION_CRISIS) {
            val taskId = intent.getLongExtra(NotificationService.EXTRA_TASK_ID, -1L)
            if (taskId != -1L) {
                pendingCrisisTaskId.value = taskId
            }
        }
    }
}
