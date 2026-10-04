package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VedicPrimary,
    onPrimary = VedicOnPrimary,
    primaryContainer = VedicPrimaryContainer,
    onPrimaryContainer = VedicOnPrimaryContainer,
    secondary = VedicSecondary,
    onSecondary = VedicOnSecondary,
    secondaryContainer = VedicSecondaryContainer,
    onSecondaryContainer = VedicOnSecondaryContainer,
    background = VedicBackground,
    onBackground = VedicOnBackground,
    surface = VedicSurface,
    onSurface = VedicOnSurface,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = StarlightMuted
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB28704),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEEBA),
    onPrimaryContainer = Color(0xFF3E2D00),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    background = Color(0xFFF8F9FD),
    onBackground = Color(0xFF191C24),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C24)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
