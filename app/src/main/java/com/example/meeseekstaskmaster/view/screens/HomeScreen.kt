package com.example.meeseekstaskmaster.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.model.MeeseeksStressLevel
import com.example.meeseekstaskmaster.model.Task
import com.example.meeseekstaskmaster.view.components.CreateTaskDialog
import com.example.meeseekstaskmaster.view.components.MeeseeksAvatar
import com.example.meeseekstaskmaster.view.components.TaskCard
import com.example.meeseekstaskmaster.view.theme.CardBackground
import com.example.meeseekstaskmaster.view.theme.MeeseeksBlue
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.SurfaceDark
import com.example.meeseekstaskmaster.view.theme.TextMuted
import com.example.meeseekstaskmaster.view.theme.TextWhite
import com.example.meeseekstaskmaster.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCrisis: (Long) -> Unit
) {
    val activeTasks by viewModel.activeTasks.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val isCreateDialogOpen by viewModel.isCreateDialogOpen.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeTopBar(
                onSettingsClick = onNavigateToSettings
            )
        },
        bottomBar = {
            MeeseeksBottomNavigation(
                selectedRoute = "home",
                onNavigateToHome = { /* Ya en Home */ },
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
                containerColor = PortalGreen,
                contentColor = SpaceDark,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nueva tarea",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = SpaceDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (activeTasks.isEmpty()) {
                EmptyStateView(onCreateClick = { viewModel.openCreateDialog() })
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = activeTasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            currentTime = currentTime,
                            onComplete = { viewModel.completeTask(it) },
                            onDelete = { viewModel.deleteTask(it) },
                            onOpenCrisis = { onNavigateToCrisis(task.id) }
                        )
                    }
                }
            }
        }
    }

    if (isCreateDialogOpen) {
        CreateTaskDialog(
            onDismiss = { viewModel.closeCreateDialog() },
            onSaveTask = { title, desc, deadline ->
                viewModel.createTask(title, desc, deadline)
            }
        )
    }
}

@Composable
private fun HomeTopBar(
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "MIS TAREAS ACTIVAS",
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Mr. Meeseeks Task Master & Alarm",
                style = MaterialTheme.typography.bodyMedium,
                color = PortalGreen,
                fontSize = 11.sp
            )
        }

        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ajustes",
                tint = TextWhite
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    onCreateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MeeseeksAvatar(
            stressLevel = MeeseeksStressLevel.NORMAL,
            size = 120.dp,
            showQuoteBubble = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¡NO HAY TAREAS ACTIVAS!",
            color = PortalGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Presiona el botón '+' para presionar la Meeseeks Box, convocar a Mr. Meeseeks y asignarle una tarea.",
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun MeeseeksBottomNavigation(
    selectedRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpaceDark,
                selectedTextColor = PortalGreen,
                indicatorColor = PortalGreen,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        NavigationBarItem(
            selected = selectedRoute == "history",
            onClick = onNavigateToHistory,
            icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
            label = { Text("Historial", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpaceDark,
                selectedTextColor = PortalGreen,
                indicatorColor = PortalGreen,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        NavigationBarItem(
            selected = selectedRoute == "settings",
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
            label = { Text("Ajustes", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpaceDark,
                selectedTextColor = PortalGreen,
                indicatorColor = PortalGreen,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )
    }
}
