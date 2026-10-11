package com.example.meeseekstaskmaster.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.model.Task
import com.example.meeseekstaskmaster.model.TaskStatus
import com.example.meeseekstaskmaster.util.TimeUtils
import com.example.meeseekstaskmaster.view.theme.CardBackground
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.StressBloodRed
import com.example.meeseekstaskmaster.view.theme.SurfaceDark
import com.example.meeseekstaskmaster.view.theme.TextMuted
import com.example.meeseekstaskmaster.view.theme.TextWhite
import com.example.meeseekstaskmaster.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val historyTasks by viewModel.historyTasks.collectAsState()

    Scaffold(
        topBar = {
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
                        text = "HISTORIAL DE TAREAS",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tareas completadas y resueltas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                if (historyTasks.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearCompleted() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Limpiar completadas",
                            tint = PortalGreen
                        )
                    }
                }
            }
        },
        bottomBar = {
            MeeseeksBottomNavigation(
                selectedRoute = "history",
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = { /* Ya en Historial */ },
                onNavigateToSettings = onNavigateToSettings
            )
        },
        containerColor = SpaceDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (historyTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay tareas en el historial aún.",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = historyTasks,
                        key = { it.id }
                    ) { task ->
                        HistoryTaskCard(
                            task = task,
                            onDelete = { viewModel.deleteTask(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryTaskCard(
    task: Task,
    onDelete: (Task) -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    val statusColor = if (isCompleted) PortalGreen else StressBloodRed
    val statusText = if (isCompleted) "¡CUMPLIDA! (MEESEEKS LIBERADO)" else "VENCIDA / RESUELTA"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (task.completedAt != null) {
                    Text(
                        text = "Completada el: ${TimeUtils.formatDateTime(task.completedAt)}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = { onDelete(task) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar del historial",
                    tint = TextMuted.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
