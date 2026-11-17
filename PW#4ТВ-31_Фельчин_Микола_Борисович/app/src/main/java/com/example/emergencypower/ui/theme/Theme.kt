package com.example.emergencypower.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val IndustrialDarkColorScheme = darkColorScheme(
    primary = AmberEnergyPrimary,
    onPrimary = IndustrialBlack,
    primaryContainer = IndustrialSurfaceVariant,
    onPrimaryContainer = AmberEnergyPrimary,
    secondary = CyanTelemetrySecondary,
    onSecondary = IndustrialBlack,
    secondaryContainer = IndustrialSurfaceVariant,
    onSecondaryContainer = CyanTelemetrySecondary,
    tertiary = GridGreen,
    onTertiary = IndustrialBlack,
    error = EmergencyRed,
    onError = TextLight,
    errorContainer = EmergencyDarkRed,
    onErrorContainer = TextLight,
    background = IndustrialBlack,
    onBackground = TextLight,
    surface = IndustrialDarkSurface,
    onSurface = TextLight,
    surfaceVariant = IndustrialSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = BorderGray
)

@Composable
fun EmergencyPowerTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicColors = dynamicDarkColorScheme(context)
        dynamicColors.copy(
            error = EmergencyRed,
            errorContainer = EmergencyDarkRed,
            onError = TextLight,
            onErrorContainer = TextLight
        )
    } else {
        IndustrialDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = RectangularShapes,
        content = content
    )
}
