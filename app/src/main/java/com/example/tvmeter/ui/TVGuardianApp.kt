package com.example.tvmeter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.*
import com.example.tvmeter.data.database.AppDatabase
import com.example.tvmeter.data.datastore.SettingsDataStore
import com.example.tvmeter.ui.components.tvFocusable
import com.example.tvmeter.ui.navigation.NavigationRoutes
import com.example.tvmeter.ui.navigation.navigationItems
import com.example.tvmeter.ui.screens.AppDetailScreen
import com.example.tvmeter.ui.screens.AppUsageScreen
import com.example.tvmeter.ui.screens.AutoLockScreen
import com.example.tvmeter.ui.screens.CategoriesScreen
import com.example.tvmeter.ui.screens.DashboardScreen
import com.example.tvmeter.ui.screens.UninstallProtectionScreen
import com.example.tvmeter.viewmodels.AppUsageViewModel
import com.example.tvmeter.viewmodels.AutoLockViewModel
import com.example.tvmeter.viewmodels.CategoriesViewModel
import com.example.tvmeter.viewmodels.UninstallViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModel
import com.example.tvmeter.data.database.dao.AppUsageDao
import com.example.tvmeter.data.database.dao.CategoryDao
import com.example.tvmeter.usage.UsageStatsCollector


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TVGuardianApp() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current

    // Initialize database and datastore
    val database = remember { AppDatabase.getDatabase(context) }
    val settingsDataStore = remember { SettingsDataStore(context) }

    // ViewModels

    val usageStatsCollector = remember { UsageStatsCollector(
        context,
        appUsageDao = database.appUsageDao()
    ) }

    val appUsageViewModel: AppUsageViewModel = viewModel {
        AppUsageViewModel(
            appUsageDao = database.appUsageDao(),
            categoryDao = database.categoryDao(),
            usageStatsCollector = usageStatsCollector
        )
    }

    val categoriesViewModel: CategoriesViewModel = viewModel {
        CategoriesViewModel(database.categoryDao())
    }
    val autoLockViewModel: AutoLockViewModel = viewModel {
        AutoLockViewModel(settingsDataStore)
    }
    val uninstallViewModel: UninstallViewModel = viewModel {
        UninstallViewModel(settingsDataStore)
    }

    NavigationDrawer(
        drawerContent = {
            NavigationDrawerContent(
                navController = navController,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        },
        drawerState = drawerState
    ) {
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.DASHBOARD,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(NavigationRoutes.DASHBOARD) {
                DashboardScreen(viewModel = appUsageViewModel)
            }


            composable(NavigationRoutes.APP_USAGE) {
                AppUsageScreen(
                    viewModel = appUsageViewModel,
                    onAppSelected = {
                        navController.navigate(NavigationRoutes.APP_DETAIL)
                    }
                )
            }

            composable(NavigationRoutes.APP_DETAIL) {
                AppDetailScreen(
                    viewModel = appUsageViewModel,
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavigationRoutes.CATEGORIES) {
                CategoriesScreen(viewModel = categoriesViewModel)
            }

            composable(NavigationRoutes.AUTO_LOCK) {
                AutoLockScreen(viewModel = autoLockViewModel)
            }

            composable(NavigationRoutes.UNINSTALL_PROTECTION) {
                UninstallProtectionScreen(viewModel = uninstallViewModel)
            }
        }
    }
}

@Composable
fun NavigationDrawerContent(
    navController: NavHostController,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // App Title
        Text(
            text = "🛡️ TV Guardian",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Navigation Items using Cards (more reliable for TV)
        navigationItems.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tvFocusable()
                    .clickable { onNavigate(item.route) },
                colors = CardDefaults.cardColors(
                    containerColor = if (navController.currentDestination?.route == item.route)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = item.icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


