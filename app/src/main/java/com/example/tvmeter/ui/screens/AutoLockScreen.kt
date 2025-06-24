package com.example.tvmeter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvmeter.ui.components.tvFocusable
import com.example.tvmeter.viewmodels.AutoLockViewModel

@Composable
fun AutoLockScreen(
    viewModel: AutoLockViewModel
) {
    val autoLockEnabled by viewModel.autoLockEnabled.collectAsState()
    val hours by viewModel.hours.collectAsState()
    val minutes by viewModel.minutes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Auto Lock / Shutdown",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .tvFocusable()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable Auto Lock",
                    fontSize = 18.sp
                )

                Switch(
                    checked = autoLockEnabled,
                    onCheckedChange = { viewModel.toggleAutoLock() },
                    modifier = Modifier.tvFocusable()
                )
            }
        }

        if (autoLockEnabled) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Set Lock Timer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hours
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { viewModel.incrementHours() },
                                modifier = Modifier.tvFocusable()
                            ) {
                                Text("+")
                            }

                            Text(
                                text = hours.toString().padStart(2, '0'),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Button(
                                onClick = { viewModel.decrementHours() },
                                modifier = Modifier.tvFocusable()
                            ) {
                                Text("-")
                            }
                        }

                        Text(":", fontSize = 24.sp)

                        // Minutes
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { viewModel.incrementMinutes() },
                                modifier = Modifier.tvFocusable()
                            ) {
                                Text("+")
                            }

                            Text(
                                text = minutes.toString().padStart(2, '0'),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Button(
                                onClick = { viewModel.decrementMinutes() },
                                modifier = Modifier.tvFocusable()
                            ) {
                                Text("-")
                            }
                        }
                    }
                }
            }
        }
    }
}