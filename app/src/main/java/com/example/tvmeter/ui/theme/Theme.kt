package com.example.tvmeter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TVMeterTheme(
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        darkColorScheme(
            // TV-optimized dark colors for better contrast on TV screens
            primary = TVBlue,
            onPrimary = TVOnPrimary,
            primaryContainer = TVBlueDark,
            onPrimaryContainer = TVOnPrimaryContainer,
            secondary = TVSecondary,
            onSecondary = TVOnSecondary,
            tertiary = TVAccent,
            surface = TVSurfaceDark,
            onSurface = TVOnSurfaceDark,
            surfaceVariant = TVSurfaceVariantDark,
            onSurfaceVariant = TVOnSurfaceVariantDark,
            background = TVBackgroundDark,
            onBackground = TVOnBackgroundDark,
        )
    } else {
        lightColorScheme(
            // TV-optimized light colors (though dark theme is preferred for TV)
            primary = TVBlueDark,
            onPrimary = TVOnPrimary,
            primaryContainer = TVBlue,
            onPrimaryContainer = TVOnPrimaryContainer,
            secondary = TVSecondaryDark,
            onSecondary = TVOnSecondary,
            tertiary = TVAccentDark,
            surface = TVSurfaceLight,
            onSurface = TVOnSurfaceLight,
            surfaceVariant = TVSurfaceVariantLight,
            onSurfaceVariant = TVOnSurfaceVariantLight,
            background = TVBackgroundLight,
            onBackground = TVOnBackgroundLight,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = TVShapes,
        content = content
    )
}