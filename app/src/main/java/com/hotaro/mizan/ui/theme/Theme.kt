package com.hotaro.mizan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Classic Green
val ClassicLightColors = lightColorScheme(
    primary = classic_light_primary,
    onPrimary = classic_light_onPrimary,
    primaryContainer = classic_light_primaryContainer,
    onPrimaryContainer = classic_light_onPrimaryContainer,
    secondary = classic_light_secondary,
    onSecondary = classic_light_onSecondary,
    secondaryContainer = classic_light_secondaryContainer,
    onSecondaryContainer = classic_light_onSecondaryContainer,
    tertiary = classic_light_tertiary,
    onTertiary = classic_light_onTertiary,
    tertiaryContainer = classic_light_tertiaryContainer,
    onTertiaryContainer = classic_light_onTertiaryContainer,
    background = classic_light_background,
    onBackground = classic_light_onBackground,
    surface = classic_light_surface,
    onSurface = classic_light_onSurface,
    surfaceVariant = classic_light_surfaceVariant,
    onSurfaceVariant = classic_light_onSurfaceVariant,
    outline = classic_light_outline,
    outlineVariant = classic_light_outlineVariant,
    surfaceContainerLowest = classic_light_surfaceContainerLowest,
    surfaceContainerLow = classic_light_surfaceContainerLow,
    surfaceContainer = classic_light_surfaceContainer,
    surfaceContainerHigh = classic_light_surfaceContainerHigh,
    surfaceContainerHighest = classic_light_surfaceContainerHighest
)

val ClassicDarkColors = darkColorScheme(
    primary = classic_dark_primary,
    onPrimary = classic_dark_onPrimary,
    primaryContainer = classic_dark_primaryContainer,
    onPrimaryContainer = classic_dark_onPrimaryContainer,
    secondary = classic_dark_secondary,
    onSecondary = classic_dark_onSecondary,
    secondaryContainer = classic_dark_secondaryContainer,
    onSecondaryContainer = classic_dark_onSecondaryContainer,
    tertiary = classic_dark_tertiary,
    onTertiary = classic_dark_onTertiary,
    tertiaryContainer = classic_dark_tertiaryContainer,
    onTertiaryContainer = classic_dark_onTertiaryContainer,
    background = classic_dark_background,
    onBackground = classic_dark_onBackground,
    surface = classic_dark_surface,
    onSurface = classic_dark_onSurface,
    surfaceVariant = classic_dark_surfaceVariant,
    onSurfaceVariant = classic_dark_onSurfaceVariant,
    outline = classic_dark_outline,
    outlineVariant = classic_dark_outlineVariant,
    surfaceContainerLowest = classic_dark_surfaceContainerLowest,
    surfaceContainerLow = classic_dark_surfaceContainerLow,
    surfaceContainer = classic_dark_surfaceContainer,
    surfaceContainerHigh = classic_dark_surfaceContainerHigh,
    surfaceContainerHighest = classic_dark_surfaceContainerHighest
)

// 2. Cherry Red
val CherryLightColors = lightColorScheme(
    primary = cherry_light_primary,
    onPrimary = cherry_light_onPrimary,
    primaryContainer = cherry_light_primaryContainer,
    onPrimaryContainer = cherry_light_onPrimaryContainer,
    secondary = cherry_light_secondary,
    onSecondary = cherry_light_onSecondary,
    secondaryContainer = cherry_light_secondaryContainer,
    onSecondaryContainer = cherry_light_onSecondaryContainer,
    tertiary = cherry_light_tertiary,
    onTertiary = cherry_light_onTertiary,
    tertiaryContainer = cherry_light_tertiaryContainer,
    onTertiaryContainer = cherry_light_onTertiaryContainer,
    background = cherry_light_background,
    onBackground = cherry_light_onBackground,
    surface = cherry_light_surface,
    onSurface = cherry_light_onSurface,
    surfaceVariant = cherry_light_surfaceVariant,
    onSurfaceVariant = cherry_light_onSurfaceVariant,
    outline = cherry_light_outline,
    outlineVariant = cherry_light_outlineVariant,
    surfaceContainerLowest = cherry_light_surfaceContainerLowest,
    surfaceContainerLow = cherry_light_surfaceContainerLow,
    surfaceContainer = cherry_light_surfaceContainer,
    surfaceContainerHigh = cherry_light_surfaceContainerHigh,
    surfaceContainerHighest = cherry_light_surfaceContainerHighest
)

val CherryDarkColors = darkColorScheme(
    primary = cherry_dark_primary,
    onPrimary = cherry_dark_onPrimary,
    primaryContainer = cherry_dark_primaryContainer,
    onPrimaryContainer = cherry_dark_onPrimaryContainer,
    secondary = cherry_dark_secondary,
    onSecondary = cherry_dark_onSecondary,
    secondaryContainer = cherry_dark_secondaryContainer,
    onSecondaryContainer = cherry_dark_onSecondaryContainer,
    tertiary = cherry_dark_tertiary,
    onTertiary = cherry_dark_onTertiary,
    tertiaryContainer = cherry_dark_tertiaryContainer,
    onTertiaryContainer = cherry_dark_onTertiaryContainer,
    background = cherry_dark_background,
    onBackground = cherry_dark_onBackground,
    surface = cherry_dark_surface,
    onSurface = cherry_dark_onSurface,
    surfaceVariant = cherry_dark_surfaceVariant,
    onSurfaceVariant = cherry_dark_onSurfaceVariant,
    outline = cherry_dark_outline,
    outlineVariant = cherry_dark_outlineVariant,
    surfaceContainerLowest = cherry_dark_surfaceContainerLowest,
    surfaceContainerLow = cherry_dark_surfaceContainerLow,
    surfaceContainer = cherry_dark_surfaceContainer,
    surfaceContainerHigh = cherry_dark_surfaceContainerHigh,
    surfaceContainerHighest = cherry_dark_surfaceContainerHighest
)

// 3. Navy Blue
val NavyLightColors = lightColorScheme(
    primary = navy_light_primary,
    onPrimary = navy_light_onPrimary,
    primaryContainer = navy_light_primaryContainer,
    onPrimaryContainer = navy_light_onPrimaryContainer,
    secondary = navy_light_secondary,
    onSecondary = navy_light_onSecondary,
    secondaryContainer = navy_light_secondaryContainer,
    onSecondaryContainer = navy_light_onSecondaryContainer,
    tertiary = navy_light_tertiary,
    onTertiary = navy_light_onTertiary,
    tertiaryContainer = navy_light_tertiaryContainer,
    onTertiaryContainer = navy_light_onTertiaryContainer,
    background = navy_light_background,
    onBackground = navy_light_onBackground,
    surface = navy_light_surface,
    onSurface = navy_light_onSurface,
    surfaceVariant = navy_light_surfaceVariant,
    onSurfaceVariant = navy_light_onSurfaceVariant,
    outline = navy_light_outline,
    outlineVariant = navy_light_outlineVariant,
    surfaceContainerLowest = navy_light_surfaceContainerLowest,
    surfaceContainerLow = navy_light_surfaceContainerLow,
    surfaceContainer = navy_light_surfaceContainer,
    surfaceContainerHigh = navy_light_surfaceContainerHigh,
    surfaceContainerHighest = navy_light_surfaceContainerHighest
)

val NavyDarkColors = darkColorScheme(
    primary = navy_dark_primary,
    onPrimary = navy_dark_onPrimary,
    primaryContainer = navy_dark_primaryContainer,
    onPrimaryContainer = navy_dark_onPrimaryContainer,
    secondary = navy_dark_secondary,
    onSecondary = navy_dark_onSecondary,
    secondaryContainer = navy_dark_secondaryContainer,
    onSecondaryContainer = navy_dark_onSecondaryContainer,
    tertiary = navy_dark_tertiary,
    onTertiary = navy_dark_onTertiary,
    tertiaryContainer = navy_dark_tertiaryContainer,
    onTertiaryContainer = navy_dark_onTertiaryContainer,
    background = navy_dark_background,
    onBackground = navy_dark_onBackground,
    surface = navy_dark_surface,
    onSurface = navy_dark_onSurface,
    surfaceVariant = navy_dark_surfaceVariant,
    onSurfaceVariant = navy_dark_onSurfaceVariant,
    outline = navy_dark_outline,
    outlineVariant = navy_dark_outlineVariant,
    surfaceContainerLowest = navy_dark_surfaceContainerLowest,
    surfaceContainerLow = navy_dark_surfaceContainerLow,
    surfaceContainer = navy_dark_surfaceContainer,
    surfaceContainerHigh = navy_dark_surfaceContainerHigh,
    surfaceContainerHighest = navy_dark_surfaceContainerHighest
)

// 4. Lavender
val LavenderLightColors = lightColorScheme(
    primary = lavender_light_primary,
    onPrimary = lavender_light_onPrimary,
    primaryContainer = lavender_light_primaryContainer,
    onPrimaryContainer = lavender_light_onPrimaryContainer,
    secondary = lavender_light_secondary,
    onSecondary = lavender_light_onSecondary,
    secondaryContainer = lavender_light_secondaryContainer,
    onSecondaryContainer = lavender_light_onSecondaryContainer,
    tertiary = lavender_light_tertiary,
    onTertiary = lavender_light_onTertiary,
    tertiaryContainer = lavender_light_tertiaryContainer,
    onTertiaryContainer = lavender_light_onTertiaryContainer,
    background = lavender_light_background,
    onBackground = lavender_light_onBackground,
    surface = lavender_light_surface,
    onSurface = lavender_light_onSurface,
    surfaceVariant = lavender_light_surfaceVariant,
    onSurfaceVariant = lavender_light_onSurfaceVariant,
    outline = lavender_light_outline,
    outlineVariant = lavender_light_outlineVariant,
    surfaceContainerLowest = lavender_light_surfaceContainerLowest,
    surfaceContainerLow = lavender_light_surfaceContainerLow,
    surfaceContainer = lavender_light_surfaceContainer,
    surfaceContainerHigh = lavender_light_surfaceContainerHigh,
    surfaceContainerHighest = lavender_light_surfaceContainerHighest
)

val LavenderDarkColors = darkColorScheme(
    primary = lavender_dark_primary,
    onPrimary = lavender_dark_onPrimary,
    primaryContainer = lavender_dark_primaryContainer,
    onPrimaryContainer = lavender_dark_onPrimaryContainer,
    secondary = lavender_dark_secondary,
    onSecondary = lavender_dark_onSecondary,
    secondaryContainer = lavender_dark_secondaryContainer,
    onSecondaryContainer = lavender_dark_onSecondaryContainer,
    tertiary = lavender_dark_tertiary,
    onTertiary = lavender_dark_onTertiary,
    tertiaryContainer = lavender_dark_tertiaryContainer,
    onTertiaryContainer = lavender_dark_onTertiaryContainer,
    background = lavender_dark_background,
    onBackground = lavender_dark_onBackground,
    surface = lavender_dark_surface,
    onSurface = lavender_dark_onSurface,
    surfaceVariant = lavender_dark_surfaceVariant,
    onSurfaceVariant = lavender_dark_onSurfaceVariant,
    outline = lavender_dark_outline,
    outlineVariant = lavender_dark_outlineVariant,
    surfaceContainerLowest = lavender_dark_surfaceContainerLowest,
    surfaceContainerLow = lavender_dark_surfaceContainerLow,
    surfaceContainer = lavender_dark_surfaceContainer,
    surfaceContainerHigh = lavender_dark_surfaceContainerHigh,
    surfaceContainerHighest = lavender_dark_surfaceContainerHighest
)

// 5. Pink
val PinkLightColors = lightColorScheme(
    primary = pink_light_primary,
    onPrimary = pink_light_onPrimary,
    primaryContainer = pink_light_primaryContainer,
    onPrimaryContainer = pink_light_onPrimaryContainer,
    secondary = pink_light_secondary,
    onSecondary = pink_light_onSecondary,
    secondaryContainer = pink_light_secondaryContainer,
    onSecondaryContainer = pink_light_onSecondaryContainer,
    tertiary = pink_light_tertiary,
    onTertiary = pink_light_onTertiary,
    tertiaryContainer = pink_light_tertiaryContainer,
    onTertiaryContainer = pink_light_onTertiaryContainer,
    background = pink_light_background,
    onBackground = pink_light_onBackground,
    surface = pink_light_surface,
    onSurface = pink_light_onSurface,
    surfaceVariant = pink_light_surfaceVariant,
    onSurfaceVariant = pink_light_onSurfaceVariant,
    outline = pink_light_outline,
    outlineVariant = pink_light_outlineVariant,
    surfaceContainerLowest = pink_light_surfaceContainerLowest,
    surfaceContainerLow = pink_light_surfaceContainerLow,
    surfaceContainer = pink_light_surfaceContainer,
    surfaceContainerHigh = pink_light_surfaceContainerHigh,
    surfaceContainerHighest = pink_light_surfaceContainerHighest
)

val PinkDarkColors = darkColorScheme(
    primary = pink_dark_primary,
    onPrimary = pink_dark_onPrimary,
    primaryContainer = pink_dark_primaryContainer,
    onPrimaryContainer = pink_dark_onPrimaryContainer,
    secondary = pink_dark_secondary,
    onSecondary = pink_dark_onSecondary,
    secondaryContainer = pink_dark_secondaryContainer,
    onSecondaryContainer = pink_dark_onSecondaryContainer,
    tertiary = pink_dark_tertiary,
    onTertiary = pink_dark_onTertiary,
    tertiaryContainer = pink_dark_tertiaryContainer,
    onTertiaryContainer = pink_dark_onTertiaryContainer,
    background = pink_dark_background,
    onBackground = pink_dark_onBackground,
    surface = pink_dark_surface,
    onSurface = pink_dark_onSurface,
    surfaceVariant = pink_dark_surfaceVariant,
    onSurfaceVariant = pink_dark_onSurfaceVariant,
    outline = pink_dark_outline,
    outlineVariant = pink_dark_outlineVariant,
    surfaceContainerLowest = pink_dark_surfaceContainerLowest,
    surfaceContainerLow = pink_dark_surfaceContainerLow,
    surfaceContainer = pink_dark_surfaceContainer,
    surfaceContainerHigh = pink_dark_surfaceContainerHigh,
    surfaceContainerHighest = pink_dark_surfaceContainerHighest
)

// 6. Mocha
val MochaLightColors = lightColorScheme(
    primary = mocha_light_primary,
    onPrimary = mocha_light_onPrimary,
    primaryContainer = mocha_light_primaryContainer,
    onPrimaryContainer = mocha_light_onPrimaryContainer,
    secondary = mocha_light_secondary,
    onSecondary = mocha_light_onSecondary,
    secondaryContainer = mocha_light_secondaryContainer,
    onSecondaryContainer = mocha_light_onSecondaryContainer,
    tertiary = mocha_light_tertiary,
    onTertiary = mocha_light_onTertiary,
    tertiaryContainer = mocha_light_tertiaryContainer,
    onTertiaryContainer = mocha_light_onTertiaryContainer,
    background = mocha_light_background,
    onBackground = mocha_light_onBackground,
    surface = mocha_light_surface,
    onSurface = mocha_light_onSurface,
    surfaceVariant = mocha_light_surfaceVariant,
    onSurfaceVariant = mocha_light_onSurfaceVariant,
    outline = mocha_light_outline,
    outlineVariant = mocha_light_outlineVariant,
    surfaceContainerLowest = mocha_light_surfaceContainerLowest,
    surfaceContainerLow = mocha_light_surfaceContainerLow,
    surfaceContainer = mocha_light_surfaceContainer,
    surfaceContainerHigh = mocha_light_surfaceContainerHigh,
    surfaceContainerHighest = mocha_light_surfaceContainerHighest
)

val MochaDarkColors = darkColorScheme(
    primary = mocha_dark_primary,
    onPrimary = mocha_dark_onPrimary,
    primaryContainer = mocha_dark_primaryContainer,
    onPrimaryContainer = mocha_dark_onPrimaryContainer,
    secondary = mocha_dark_secondary,
    onSecondary = mocha_dark_onSecondary,
    secondaryContainer = mocha_dark_secondaryContainer,
    onSecondaryContainer = mocha_dark_onSecondaryContainer,
    tertiary = mocha_dark_tertiary,
    onTertiary = mocha_dark_onTertiary,
    tertiaryContainer = mocha_dark_tertiaryContainer,
    onTertiaryContainer = mocha_dark_onTertiaryContainer,
    background = mocha_dark_background,
    onBackground = mocha_dark_onBackground,
    surface = mocha_dark_surface,
    onSurface = mocha_dark_onSurface,
    surfaceVariant = mocha_dark_surfaceVariant,
    onSurfaceVariant = mocha_dark_onSurfaceVariant,
    outline = mocha_dark_outline,
    outlineVariant = mocha_dark_outlineVariant,
    surfaceContainerLowest = mocha_dark_surfaceContainerLowest,
    surfaceContainerLow = mocha_dark_surfaceContainerLow,
    surfaceContainer = mocha_dark_surfaceContainer,
    surfaceContainerHigh = mocha_dark_surfaceContainerHigh,
    surfaceContainerHighest = mocha_dark_surfaceContainerHighest
)

// 7. Catppuccin (Latte / Mocha)
val CatppuccinLightColors = lightColorScheme(
    primary = catppuccin_light_primary,
    onPrimary = catppuccin_light_onPrimary,
    primaryContainer = catppuccin_light_primaryContainer,
    onPrimaryContainer = catppuccin_light_onPrimaryContainer,
    secondary = catppuccin_light_secondary,
    onSecondary = catppuccin_light_onSecondary,
    secondaryContainer = catppuccin_light_secondaryContainer,
    onSecondaryContainer = catppuccin_light_onSecondaryContainer,
    tertiary = catppuccin_light_tertiary,
    onTertiary = catppuccin_light_onTertiary,
    tertiaryContainer = catppuccin_light_tertiaryContainer,
    onTertiaryContainer = catppuccin_light_onTertiaryContainer,
    background = catppuccin_light_background,
    onBackground = catppuccin_light_onBackground,
    surface = catppuccin_light_surface,
    onSurface = catppuccin_light_onSurface,
    surfaceVariant = catppuccin_light_surfaceVariant,
    onSurfaceVariant = catppuccin_light_onSurfaceVariant,
    outline = catppuccin_light_outline,
    outlineVariant = catppuccin_light_outlineVariant,
    surfaceContainerLowest = catppuccin_light_surfaceContainerLowest,
    surfaceContainerLow = catppuccin_light_surfaceContainerLow,
    surfaceContainer = catppuccin_light_surfaceContainer,
    surfaceContainerHigh = catppuccin_light_surfaceContainerHigh,
    surfaceContainerHighest = catppuccin_light_surfaceContainerHighest
)

val CatppuccinDarkColors = darkColorScheme(
    primary = catppuccin_dark_primary,
    onPrimary = catppuccin_dark_onPrimary,
    primaryContainer = catppuccin_dark_primaryContainer,
    onPrimaryContainer = catppuccin_dark_onPrimaryContainer,
    secondary = catppuccin_dark_secondary,
    onSecondary = catppuccin_dark_onSecondary,
    secondaryContainer = catppuccin_dark_secondaryContainer,
    onSecondaryContainer = catppuccin_dark_onSecondaryContainer,
    tertiary = catppuccin_dark_tertiary,
    onTertiary = catppuccin_dark_onTertiary,
    tertiaryContainer = catppuccin_dark_tertiaryContainer,
    onTertiaryContainer = catppuccin_dark_onTertiaryContainer,
    background = catppuccin_dark_background,
    onBackground = catppuccin_dark_onBackground,
    surface = catppuccin_dark_surface,
    onSurface = catppuccin_dark_onSurface,
    surfaceVariant = catppuccin_dark_surfaceVariant,
    onSurfaceVariant = catppuccin_dark_onSurfaceVariant,
    outline = catppuccin_dark_outline,
    outlineVariant = catppuccin_dark_outlineVariant,
    surfaceContainerLowest = catppuccin_dark_surfaceContainerLowest,
    surfaceContainerLow = catppuccin_dark_surfaceContainerLow,
    surfaceContainer = catppuccin_dark_surfaceContainer,
    surfaceContainerHigh = catppuccin_dark_surfaceContainerHigh,
    surfaceContainerHighest = catppuccin_dark_surfaceContainerHighest
)

// 8. Monochrome
val MonochromeLightColors = lightColorScheme(
    primary = mono_light_primary,
    onPrimary = mono_light_onPrimary,
    primaryContainer = mono_light_primaryContainer,
    onPrimaryContainer = mono_light_onPrimaryContainer,
    secondary = mono_light_secondary,
    onSecondary = mono_light_onSecondary,
    secondaryContainer = mono_light_secondaryContainer,
    onSecondaryContainer = mono_light_onSecondaryContainer,
    tertiary = mono_light_tertiary,
    onTertiary = mono_light_onTertiary,
    tertiaryContainer = mono_light_tertiaryContainer,
    onTertiaryContainer = mono_light_onTertiaryContainer,
    background = mono_light_background,
    onBackground = mono_light_onBackground,
    surface = mono_light_surface,
    onSurface = mono_light_onSurface,
    surfaceVariant = mono_light_surfaceVariant,
    onSurfaceVariant = mono_light_onSurfaceVariant,
    outline = mono_light_outline,
    outlineVariant = mono_light_outlineVariant,
    surfaceContainerLowest = mono_light_surfaceContainerLowest,
    surfaceContainerLow = mono_light_surfaceContainerLow,
    surfaceContainer = mono_light_surfaceContainer,
    surfaceContainerHigh = mono_light_surfaceContainerHigh,
    surfaceContainerHighest = mono_light_surfaceContainerHighest
)

val MonochromeDarkColors = darkColorScheme(
    primary = mono_dark_primary,
    onPrimary = mono_dark_onPrimary,
    primaryContainer = mono_dark_primaryContainer,
    onPrimaryContainer = mono_dark_onPrimaryContainer,
    secondary = mono_dark_secondary,
    onSecondary = mono_dark_onSecondary,
    secondaryContainer = mono_dark_secondaryContainer,
    onSecondaryContainer = mono_dark_onSecondaryContainer,
    tertiary = mono_dark_tertiary,
    onTertiary = mono_dark_onTertiary,
    tertiaryContainer = mono_dark_tertiaryContainer,
    onTertiaryContainer = mono_dark_onTertiaryContainer,
    background = mono_dark_background,
    onBackground = mono_dark_onBackground,
    surface = mono_dark_surface,
    onSurface = mono_dark_onSurface,
    surfaceVariant = mono_dark_surfaceVariant,
    onSurfaceVariant = mono_dark_onSurfaceVariant,
    outline = mono_dark_outline,
    outlineVariant = mono_dark_outlineVariant,
    surfaceContainerLowest = mono_dark_surfaceContainerLowest,
    surfaceContainerLow = mono_dark_surfaceContainerLow,
    surfaceContainer = mono_dark_surfaceContainer,
    surfaceContainerHigh = mono_dark_surfaceContainerHigh,
    surfaceContainerHighest = mono_dark_surfaceContainerHighest
)

fun getPalette(darkTheme: Boolean, paletteName: String): ColorScheme {
    return when (paletteName) {
        "cherry", "cherry_red" -> if (darkTheme) CherryDarkColors else CherryLightColors
        "navy", "navy_blue" -> if (darkTheme) NavyDarkColors else NavyLightColors
        "lavender" -> if (darkTheme) LavenderDarkColors else LavenderLightColors
        "pink" -> if (darkTheme) PinkDarkColors else PinkLightColors
        "mocha" -> if (darkTheme) MochaDarkColors else MochaLightColors
        "catppuccin" -> if (darkTheme) CatppuccinDarkColors else CatppuccinLightColors
        "monochrome" -> if (darkTheme) MonochromeDarkColors else MonochromeLightColors
        "classic" -> if (darkTheme) ClassicDarkColors else ClassicLightColors
        else -> if (darkTheme) ClassicDarkColors else ClassicLightColors
    }
}

@Composable
fun MizanTheme(
    themeMode: Int = 0, // 0: System, 1: Light, 2: Dark
    paletteName: String = "dynamic",
    appFont: String = "default",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        (paletteName == "dynamic" || paletteName == "default") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamic = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (darkTheme) {
                dynamic.copy(
                    background = Color.Black,
                    surface = Color.Black,
                    surfaceContainerLowest = Color(0xFF0C0D0E),
                    surfaceContainerLow = Color(0xFF141517),
                    surfaceContainer = Color(0xFF1A1B1E),
                    surfaceContainerHigh = Color(0xFF222427),
                    surfaceContainerHighest = Color(0xFF2B2D31)
                )
            } else {
                dynamic
            }
        }
        else -> getPalette(darkTheme, paletteName)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(appFont),
        content = content
    )
}

@Composable
fun QuranReaderTheme(
    themeMode: Int = 0,
    paletteName: String = "dynamic",
    appFont: String = "default",
    content: @Composable () -> Unit
) = MizanTheme(themeMode, paletteName, appFont, content)
