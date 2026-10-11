package com.example.meeseekstaskmaster.view.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.service.AudioService
import com.example.meeseekstaskmaster.service.VibrationService
import com.example.meeseekstaskmaster.view.theme.CardBackground
import com.example.meeseekstaskmaster.view.theme.MeeseeksBlue
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.StressBloodRed
import com.example.meeseekstaskmaster.view.theme.SurfaceDark
import com.example.meeseekstaskmaster.view.theme.TextMuted
import com.example.meeseekstaskmaster.view.theme.TextWhite
import com.example.meeseekstaskmaster.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()
    val canScheduleExactAlarms by viewModel.canScheduleExactAlarms.collectAsState()

    var isTestingEffects by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissions(context)
    }

    // Launcher para solicitar permiso de notificaciones (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions(context)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AJUSTES Y PREFERENCIAS",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Sonido, vibración y permisos del sistema",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        },
        bottomBar = {
            MeeseeksBottomNavigation(
                selectedRoute = "settings",
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSettings = { /* Ya en Ajustes */ }
            )
        },
        containerColor = SpaceDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Efectos de Alarma
            Text(
                text = "EFECTOS DE ALARMA",
                color = PortalGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Switch de Sonido
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MeeseeksBlue
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Sonido de Alarma", color = TextWhite, fontWeight = FontWeight.SemiBold)
                                Text(text = "Reproducir alarma Meeseeks al vencer", color = TextMuted, fontSize = 12.sp)
                            }
                        }

                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpaceDark,
                                checkedTrackColor = PortalGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch de Vibración
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = MeeseeksBlue
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Vibración de Emergencia", color = TextWhite, fontWeight = FontWeight.SemiBold)
                                Text(text = "Patrón desesperado durante la crisis", color = TextMuted, fontSize = 12.sp)
                            }
                        }

                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpaceDark,
                                checkedTrackColor = PortalGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Botón para probar efectos
                    OutlinedButton(
                        onClick = {
                            if (isTestingEffects) {
                                AudioService.stopAudio()
                                VibrationService.stopVibration(context)
                                isTestingEffects = false
                            } else {
                                if (soundEnabled) AudioService.playAlarm(context)
                                if (vibrationEnabled) VibrationService.startVibration(context)
                                isTestingEffects = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isTestingEffects) StressBloodRed else PortalGreen
                        )
                    ) {
                        Icon(
                            imageVector = if (isTestingEffects) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isTestingEffects) "Detener Prueba" else "Probar Sonido y Vibración")
                    }
                }
            }

            // Sección Permisos del Sistema
            Text(
                text = "PERMISOS DEL SISTEMA",
                color = PortalGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Estado Permiso Notificaciones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = MeeseeksBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Notificaciones", color = TextWhite, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = if (hasNotificationPermission) "Concedido ✓" else "Denegado ✗",
                                    color = if (hasNotificationPermission) PortalGreen else StressBloodRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Button(
                                onClick = {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PortalGreen, contentColor = SpaceDark),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Permitir", fontSize = 12.sp)
                            }
                        }
                    }

                    // Estado Permiso Alarmas Exactas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = MeeseeksBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Alarmas Exactas", color = TextWhite, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = if (canScheduleExactAlarms) "Disponible ✓" else "Requiere permiso ✗",
                                    color = if (canScheduleExactAlarms) PortalGreen else StressBloodRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!canScheduleExactAlarms && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PortalGreen, contentColor = SpaceDark),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Ajustar", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Lore Card Mr. Meeseeks
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MeeseeksBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101926))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MeeseeksBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PROTOCOLO MR. MEESEEKS",
                            color = MeeseeksBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Los Meeseeks no están hechos para vivir mucho tiempo. La existencia es agonía prolongada. Al completar tus tareas a tiempo, salvas a tu Meeseeks de la locura y lo ayudas a desaparecer en una nube de paz.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
