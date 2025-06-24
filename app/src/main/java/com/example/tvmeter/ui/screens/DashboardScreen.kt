package com.example.tvmeter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import com.example.tvmeter.ui.components.tvCardFocus
import com.example.tvmeter.viewmodels.AppInfo
import com.example.tvmeter.viewmodels.AppUsageViewModel

@Composable
fun DashboardScreen(
    viewModel: AppUsageViewModel,
    onAppSelected: (AppInfo) -> Unit = {}
) {
    val topWeeklyApps by viewModel.topWeeklyApps.collectAsState()
    val totalTodayUsage by viewModel.totalTodayUsage.collectAsState()
    val totalWeeklyUsage by viewModel.totalWeeklyUsage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val overLimitApps by viewModel.getOverLimitApps().collectAsState()

    val firstCardFocusRequester = remember { FocusRequester() }
    val appsRowFocusRequester = remember { FocusRequester() }

    TvLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Screen Time Summary",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 4.dp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .focusRequester(firstCardFocusRequester) // ✅ Attach
                        .tvCardFocus { viewModel.refreshUsageData() },
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalTodayUsage > 300)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Today", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = viewModel.formatUsageTime(totalTodayUsage),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (totalTodayUsage > 300) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("High usage!", fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .tvCardFocus(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Weekly Average", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = viewModel.formatUsageTime(totalWeeklyUsage / 7),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Per day", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (overLimitApps.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().tvCardFocus(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("⚠️ Apps Over Daily Limit", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        overLimitApps.take(3).forEach { app ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(app.icon, fontSize = 20.sp, modifier = Modifier.width(40.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(app.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(
                                        "${viewModel.formatUsageTime(app.usageMinutes)} / ${viewModel.formatUsageTime(app.dailyLimit)}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp).tvCardFocus(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Usage Trends", fontSize = 22.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Chart implementation coming soon",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Most Used Apps This Week",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            if (topWeeklyApps.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp).tvCardFocus(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📱", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No usage data available yet", fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Please ensure usage access permission is granted", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                TvLazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    itemsIndexed(topWeeklyApps) { index, app ->
                        AppUsageCard(
                            app = app,
                            viewModel = viewModel,
                            onAppSelected = onAppSelected,
                            focusRequester = if (index == 0) appsRowFocusRequester else null
                        )
                    }
                }
            }
        }
    }

    // ✅ Safely request focus after composition
    LaunchedEffect(topWeeklyApps.isNotEmpty()) {
        if (topWeeklyApps.isNotEmpty()) {
            kotlinx.coroutines.yield()
            try {
                firstCardFocusRequester.requestFocus()
            } catch (e: IllegalStateException) {
                // Focus requester not attached yet
            }
        }
    }

}

@Composable
private fun AppUsageCard(
    app: AppInfo,
    viewModel: AppUsageViewModel,
    onAppSelected: (AppInfo) -> Unit,
    focusRequester: FocusRequester? = null
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(180.dp)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .tvCardFocus {
                viewModel.selectApp(app)
                onAppSelected(app)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(app.icon, fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = app.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Today: ${viewModel.formatUsageTime(app.usageMinutes)}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Weekly: ${viewModel.formatUsageTime(app.weeklyUsageMinutes)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                val progress = (app.usageMinutes.toFloat() / app.dailyLimit.toFloat()).coerceAtMost(1f)
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = if (progress > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("${(progress * 100).toInt()}% of limit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
