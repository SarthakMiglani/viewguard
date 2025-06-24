package com.example.tvmeter.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import com.example.tvmeter.ui.components.tvCardFocus
import com.example.tvmeter.viewmodels.AppInfo
import com.example.tvmeter.viewmodels.AppUsageViewModel

@Composable
fun AppUsageScreen(
    viewModel: AppUsageViewModel,
    onAppSelected: (AppInfo) -> Unit
) {
    val appList by viewModel.appUsageList.collectAsState()
    val firstItemFocusRequester = remember { FocusRequester() }

    // Request focus on the first item when screen loads
    LaunchedEffect(appList) {
        if (appList.isNotEmpty()) {
            firstItemFocusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "App Usage",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (appList.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No app usage data available",
                    fontSize = 16.sp
                )
            }
        } else {
            TvLazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(appList) { app ->
                    val isFirst = appList.indexOf(app) == 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isFirst) {
                                    Modifier.focusRequester(firstItemFocusRequester)
                                } else {
                                    Modifier
                                }
                            )
                            .tvCardFocus {
                                viewModel.selectApp(app)
                                onAppSelected(app)
                            }
                            .selectable(
                                selected = false,
                                onClick = {
                                    viewModel.selectApp(app)
                                    onAppSelected(app)
                                },
                                role = Role.Button
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = app.icon,
                                    fontSize = 24.sp
                                )
                                Column {
                                    Text(
                                        text = app.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Category ${app.categoryId}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "${app.usageMinutes}m",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Limit: ${app.dailyLimit}m",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}