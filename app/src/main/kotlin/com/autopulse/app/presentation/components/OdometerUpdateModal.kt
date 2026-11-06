package com.autopulse.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.presentation.theme.AutoPulseColors

@Composable
fun OdometerUpdateModal(
    currentMileage: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var input by remember { mutableStateOf(currentMileage.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Odometer", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Select quick increment or enter exact mileage:",
                    color = AutoPulseColors.TextSecondary,
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(100, 500, 1000).forEach { inc ->
                        Button(
                            onClick = {
                                val current = input.toIntOrNull() ?: currentMileage
                                input = (current + inc).toString()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AutoPulseColors.SurfaceDark)
                        ) {
                            Text("+$inc", fontSize = 11.sp, color = AutoPulseColors.PrimaryCyan)
                        }
                    }
                }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Miles (MI)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { input.toIntOrNull()?.let { onConfirm(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = AutoPulseColors.PrimaryCyan)
            ) {
                Text("Update & Recalculate", color = Color(0xFF070B14), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AutoPulseColors.TextSecondary)
            }
        },
        containerColor = AutoPulseColors.SurfaceDark
    )
}
