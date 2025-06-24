package com.example.tvmeter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvmeter.ui.components.tvFocusable
import com.example.tvmeter.viewmodels.UninstallViewModel

@Composable
fun UninstallProtectionScreen(
    viewModel: UninstallViewModel
) {
    val pin by viewModel.pin.collectAsState()
    val isValidated by viewModel.isValidated.collectAsState()
    val currentFocusIndex by viewModel.currentFocusIndex.collectAsState()

    val focusRequesters = remember { List(4) { FocusRequester() } }

    LaunchedEffect(currentFocusIndex) {
        if (currentFocusIndex in 0..3) {
            focusRequesters[currentFocusIndex].requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Uninstall Protection",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Enter 4-digit PIN",
            fontSize = 18.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            pin.forEachIndexed { index, digit ->
                OutlinedTextField(
                    value = digit,
                    onValueChange = { newValue ->
                        viewModel.updateDigit(index, newValue)
                    },
                    modifier = Modifier
                        .width(60.dp)
                        .focusRequester(focusRequesters[index])
                        .tvFocusable(),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.clearPin() },
                modifier = Modifier.tvFocusable()
            ) {
                Text("Clear")
            }

            Button(
                onClick = { viewModel.validatePin() },
                enabled = pin.all { it.isNotEmpty() },
                modifier = Modifier.tvFocusable()
            ) {
                Text("Confirm")
            }
        }

        if (isValidated) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = "✓ PIN Validated Successfully!",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}