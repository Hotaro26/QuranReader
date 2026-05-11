package com.hotaro.quranreader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = Color.White,
    onBackground = md_theme_light_onBackground,
    surface = Color.White,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = Color.Black,
    onBackground = md_theme_dark_onBackground,
    surface = Color.Black,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

fun getPalette(darkTheme: Boolean, paletteName: String): ColorScheme {
    val base = if (darkTheme) DarkColors else LightColors
    return when (paletteName) {
        "lavender" -> if (darkTheme) {
            base.copy(primary = lavender_dark_primary, onPrimary = lavender_dark_onPrimary, primaryContainer = lavender_dark_primaryContainer, onPrimaryContainer = lavender_dark_onPrimaryContainer)
        } else {
            base.copy(primary = lavender_light_primary, onPrimary = lavender_light_onPrimary, primaryContainer = lavender_light_primaryContainer, onPrimaryContainer = lavender_light_onPrimaryContainer)
        }
        "pink" -> if (darkTheme) {
            base.copy(primary = pink_dark_primary, onPrimary = pink_dark_onPrimary, primaryContainer = pink_dark_primaryContainer, onPrimaryContainer = pink_dark_onPrimaryContainer)
        } else {
            base.copy(primary = pink_light_primary, onPrimary = pink_light_onPrimary, primaryContainer = pink_light_primaryContainer, onPrimaryContainer = pink_light_onPrimaryContainer)
        }
        "mocha" -> if (darkTheme) {
            base.copy(primary = mocha_dark_primary, onPrimary = mocha_dark_onPrimary, primaryContainer = mocha_dark_primaryContainer, onPrimaryContainer = mocha_dark_onPrimaryContainer)
        } else {
            base.copy(primary = mocha_light_primary, onPrimary = mocha_light_onPrimary, primaryContainer = mocha_light_primaryContainer, onPrimaryContainer = mocha_light_onPrimaryContainer)
        }
        "catppuccin" -> if (darkTheme) {
            base.copy(primary = catppuccin_dark_primary, background = Color.Black, surface = Color.Black)
        } else {
            base.copy(primary = catppuccin_light_primary, background = Color.White, surface = Color.White)
        }
        "monochrome" -> if (darkTheme) {
            base.copy(primary = mono_dark_primary, onPrimary = mono_dark_onPrimary, primaryContainer = mono_dark_primaryContainer, onPrimaryContainer = mono_dark_onPrimaryContainer)
        } else {
            base.copy(primary = mono_light_primary, onPrimary = mono_light_onPrimary, primaryContainer = mono_light_primaryContainer, onPrimaryContainer = mono_light_onPrimaryContainer)
        }
        else -> base
    }
}

@Composable
fun QuranReaderTheme(
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
        paletteName == "dynamic" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamic = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (darkTheme) {
                dynamic.copy(background = Color.Black, surface = Color.Black, surfaceContainer = Color.Black)
            } else {
                dynamic.copy(background = Color.White, surface = Color.White, surfaceContainer = Color.White)
            }
        }
        else -> getPalette(darkTheme, paletteName)
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            
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
