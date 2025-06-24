package com.example.tvmeter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvmeter.ui.components.tvCardFocus
import com.example.tvmeter.viewmodels.AppUsageViewModel

@Composable
fun AppDetailScreen(
    viewModel: AppUsageViewModel,
    onBackPressed: () -> Unit
) {
    val selectedApp by viewModel.selectedApp.collectAsState()
    val backButtonFocusRequester = remember { FocusRequester() }

    // Request focus on back button when screen loads
    LaunchedEffect(Unit) {
        backButtonFocusRequester.requestFocus()
    }

    selectedApp?.let { app ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with back button and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .focusRequester(backButtonFocusRequester)
                        .tvCardFocus { onBackPressed() }
                ) {
                    Text("← Back")
                }

                Text(
                    text = app.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            // App icon and details section
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // App icon card
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .tvCardFocus()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.icon,
                            fontSize = 48.sp
                        )
                    }
                }

                // App details
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Package Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    DetailRow(label = "Package", value = app.packageName)
                    DetailRow(label = "Usage Today", value = "${app.usageMinutes} minutes")
                    DetailRow(label = "Daily Limit", value = "${app.dailyLimit} minutes")
                    DetailRow(label = "Category", value = "Category ${app.categoryId}")

                    // Usage status
                    val usagePercent = if (app.dailyLimit > 0) {
                        (app.usageMinutes.toFloat() / app.dailyLimit * 100).toInt()
                    } else 0

                    DetailRow(
                        label = "Usage Status",
                        value = "$usagePercent% of daily limit",
                        isHighlight = usagePercent > 80
                    )
                }
            }

            // Action buttons row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Handle set limit */ },
                    modifier = Modifier
                        .weight(1f)
                        .tvCardFocus()
                ) {
                    Text("Set Limit")
                }

                Button(
                    onClick = { /* Handle block app */ },
                    modifier = Modifier
                        .weight(1f)
                        .tvCardFocus()
                ) {
                    Text("Block App")
                }

                Button(
                    onClick = { /* Handle view history */ },
                    modifier = Modifier
                        .weight(1f)
                        .tvCardFocus()
                ) {
                    Text("View History")
                }
            }

            // Usage chart placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .tvCardFocus()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📊",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "Usage Chart",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Weekly usage breakdown will appear here",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } ?: run {
        // Empty state when no app is selected
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "No app selected",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .focusRequester(backButtonFocusRequester)
                        .tvCardFocus { onBackPressed() }
                ) {
                    Text("← Go Back")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}