package com.example.tvmeter.ui.theme

import androidx.compose.ui.graphics.Color

// Legacy colors (keep for compatibility)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// TV-optimized color palette
// Primary colors - High contrast blue for TV visibility
val TVBlue = Color(0xFF00BCD4)           // Cyan - great for TV focus
val TVBlueDark = Color(0xFF0097A7)       // Darker cyan
val TVOnPrimary = Color(0xFF000000)      // Black text on primary
val TVOnPrimaryContainer = Color(0xFFFFFFFF) // White text on primary container

// Secondary colors
val TVSecondary = Color(0xFF4CAF50)      // Green - good secondary color for TV
val TVSecondaryDark = Color(0xFF388E3C)  // Darker green
val TVOnSecondary = Color(0xFF000000)    // Black text on secondary

// Accent colors
val TVAccent = Color(0xFFFF5722)         // Orange accent - high visibility
val TVAccentDark = Color(0xFFD84315)     // Darker orange

// Dark theme colors (primary for TV)
val TVBackgroundDark = Color(0xFF121212)        // Very dark background
val TVOnBackgroundDark = Color(0xFFFFFFFF)      // White text
val TVSurfaceDark = Color(0xFF1E1E1E)           // Card/surface color
val TVOnSurfaceDark = Color(0xFFFFFFFF)         // White text on surface
val TVSurfaceVariantDark = Color(0xFF2D2D2D)    // Elevated surface
val TVOnSurfaceVariantDark = Color(0xFFE0E0E0)  // Light gray text
val TVOutlineDark = Color(0xFF616161)           // Border/outline color
val TVInverseSurface = Color(0xFFE6E1E5)        // Light surface for contrast
val TVInverseOnSurface = Color(0xFF313033)      // Dark text on light surface

// Light theme colors (backup - not ideal for TV)
val TVBackgroundLight = Color(0xFFFFFBFE)       // Off-white background
val TVOnBackgroundLight = Color(0xFF1C1B1F)     // Dark text
val TVSurfaceLight = Color(0xFFFFFFFF)          // White surface
val TVOnSurfaceLight = Color(0xFF1C1B1F)        // Dark text on surface
val TVSurfaceVariantLight = Color(0xFFF3F3F3)   // Light gray surface
val TVOnSurfaceVariantLight = Color(0xFF49454F) // Medium gray text
val TVOutlineLight = Color(0xFF79747E)          // Medium gray outline
val TVInverseSurfaceLight = Color(0xFF313033)   // Dark surface for contrast
val TVInverseOnSurfaceLight = Color(0xFFF4EFF4) // Light text on dark surface

// Focus and selection colors for TV navigation
val TVFocusColor = Color(0xFF00E5FF)     // Bright cyan for focus indication
val TVFocusColorDark = Color(0xFF00ACC1) // Darker focus color
val TVSelectionColor = Color(0x3300BCD4) // Semi-transparent selection