package com.example.tvmeter.ui.navigation

data class NavigationItem(
    val route: String,
    val title: String,
    val icon: String
)

val navigationItems = listOf(
    NavigationItem(NavigationRoutes.DASHBOARD, "Dashboard", "🏠"),
    NavigationItem(NavigationRoutes.APP_USAGE, "App Usage", "📊"),
    NavigationItem(NavigationRoutes.CATEGORIES, "Categories", "📂"),
    NavigationItem(NavigationRoutes.AUTO_LOCK, "Auto Lock/Shutdown", "🔒"),
    NavigationItem(NavigationRoutes.UNINSTALL_PROTECTION, "Uninstall Protection", "🛡️")
)