package com.example.meeseekstaskmaster.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.meeseekstaskmaster.util.TaskValidation
import com.example.meeseekstaskmaster.util.TimeUtils
import com.example.meeseekstaskmaster.util.ValidationResult
import com.example.meeseekstaskmaster.view.theme.MeeseeksBlue
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.StressBloodRed
import com.example.meeseekstaskmaster.view.theme.SurfaceDark
import com.example.meeseekstaskmaster.view.theme.TextMuted
import com.example.meeseekstaskmaster.view.theme.TextWhite
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onSaveTask: (title: String, description: String, deadline: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Fecha límite predeterminada: Momento actual + 2 horas
    val calendar = remember {
        Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 2)
        }
    }
    var selectedDeadlineMillis by remember { mutableLongStateOf(calendar.timeInMillis) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(2.dp, PortalGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "¡INVENTAR TAREA!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PortalGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "\"I'm Mr. Meeseeks, look at me! ¿Qué tarea quieres que cumpla?\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MeeseeksBlue,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Campo Título
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Título de la tarea *", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PortalGreen,
                        unfocusedBorderColor = TextMuted,
                        cursorColor = PortalGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)", color = TextMuted) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PortalGreen,
                        unfocusedBorderColor = TextMuted,
                        cursorColor = PortalGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Fecha y Hora Límite:",
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botones selectores de Fecha y Hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MeeseeksBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = TimeUtils.formatDate(selectedDeadlineMillis),
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MeeseeksBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = TimeUtils.formatTime(selectedDeadlineMillis),
                            fontSize = 13.sp
                        )
                    }
                }

                // Mensaje de error de validación
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = StressBloodRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botones Cancelar y Guardar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (isSubmitting) return@Button

                            val validation = TaskValidation.validate(title, selectedDeadlineMillis)
                            if (validation is ValidationResult.Error) {
                                errorMessage = validation.message
                            } else {
                                isSubmitting = true
                                onSaveTask(title, description, selectedDeadlineMillis)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PortalGreen,
                            contentColor = SpaceDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isSubmitting) "Guardando..." else "Guardar tarea",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Diálogo Material3 DatePicker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDeadlineMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { pickedDate ->
                            val currentCal = Calendar.getInstance().apply { timeInMillis = selectedDeadlineMillis }
                            val newCal = Calendar.getInstance().apply {
                                timeInMillis = pickedDate
                                set(Calendar.HOUR_OF_DAY, currentCal.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, currentCal.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                            }
                            selectedDeadlineMillis = newCal.timeInMillis
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = PortalGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Diálogo Material3 TimePicker
    if (showTimePicker) {
        val currentCal = remember(selectedDeadlineMillis) {
            Calendar.getInstance().apply { timeInMillis = selectedDeadlineMillis }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = currentCal.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentCal.get(Calendar.MINUTE),
            is24Hour = true
        )

        Dialog(onDismissRequest = { showTimePicker = false }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Seleccionar Hora",
                        color = PortalGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancelar", color = TextMuted)
                        }
                        TextButton(
                            onClick = {
                                val updatedCal = Calendar.getInstance().apply {
                                    timeInMillis = selectedDeadlineMillis
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                    set(Calendar.SECOND, 0)
                                }
                                selectedDeadlineMillis = updatedCal.timeInMillis
                                showTimePicker = false
                            }
                        ) {
                            Text("Aceptar", color = PortalGreen)
                        }
                    }
                }
            }
        }
    }
}
