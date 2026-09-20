package com.shubham.enamora.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EnamoraDarkColorScheme = darkColorScheme(
    primary = EnamoraRose,
    onPrimary = EnamoraObsidian,
    primaryContainer = EnamoraWine,
    onPrimaryContainer = EnamoraWarmIvory,

    secondary = EnamoraDustyRose,
    onSecondary = EnamoraObsidian,
    secondaryContainer = EnamoraWineDeep,
    onSecondaryContainer = EnamoraRoseSoft,

    tertiary = EnamoraWarmIvory,
    onTertiary = EnamoraObsidian,

    background = EnamoraObsidian,
    onBackground = EnamoraWarmIvory,

    surface = EnamoraSurface,
    onSurface = EnamoraWarmIvory,
    surfaceVariant = EnamoraSurfaceRaised,
    onSurfaceVariant = EnamoraTextSecondary,

    error = EnamoraError,
    onError = EnamoraObsidian,

    outline = EnamoraOutline,
    outlineVariant = EnamoraSurfaceSoft,
    scrim = EnamoraBlack
)

@Composable
fun EnamoraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EnamoraDarkColorScheme,
        typography = Typography,
        content = content
    )
}