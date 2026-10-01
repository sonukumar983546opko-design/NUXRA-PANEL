package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NexusDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberVoid,
    primaryContainer = CyberSurfaceElevated,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = TextPrimary,
    secondaryContainer = CyberSurfaceElevated,
    onSecondaryContainer = NeonPurple,
    tertiary = NeonPink,
    onTertiary = TextPrimary,
    background = CyberVoid,
    onBackground = TextPrimary,
    surface = CyberBackground,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurface,
    onSurfaceVariant = TextSecondary,
    outline = CyberSurfaceBorder,
    error = NeonRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark gaming theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NexusDarkColorScheme,
        typography = Typography,
        content = content
    )
}
