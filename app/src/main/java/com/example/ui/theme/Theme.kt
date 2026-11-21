package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NariLightColorScheme = lightColorScheme(
    primary = DeepPlum,
    onPrimary = Color.White,
    primaryContainer = Lavender,
    onPrimaryContainer = DeepPlumDark,
    secondary = MutedRoseDark,
    onSecondary = Color.White,
    secondaryContainer = MutedRoseContainer,
    onSecondaryContainer = InkPrimary,
    tertiary = MutedRose,
    onTertiary = InkPrimary,
    tertiaryContainer = LavenderSurface,
    onTertiaryContainer = DeepPlum,
    background = WarmIvory,
    onBackground = InkPrimary,
    surface = WarmCard,
    onSurface = InkPrimary,
    surfaceVariant = Lavender,
    onSurfaceVariant = InkSecondary,
    outline = WarmOutline,
    outlineVariant = Color(0xFFEDE3E4),
    error = SafetyRed,
    onError = Color.White,
    errorContainer = SafetyRedContainer,
    onErrorContainer = SafetyRed,
)

private val NariDarkColorScheme = darkColorScheme(
    primary = DeepPlumLight,
    onPrimary = DeepPlumDark,
    primaryContainer = DeepPlum,
    onPrimaryContainer = Lavender,
    secondary = MutedRose,
    onSecondary = InkPrimary,
    secondaryContainer = MutedRoseDark,
    onSecondaryContainer = WarmIvory,
    tertiary = MutedRose,
    onTertiary = InkPrimary,
    background = DarkBackground,
    onBackground = WarmIvory,
    surface = DarkSurface,
    onSurface = WarmIvory,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFE5DAE5),
    outline = Color(0xFF937F92),
    outlineVariant = Color(0xFF564655),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
)

val NariShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun NariBondhuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) NariDarkColorScheme else NariLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NariTypography,
        shapes = NariShapes,
        content = content,
    )
}
