package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = EmeraldOnPrimaryDark,
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = OnEmeraldContainerDark,
    secondary = AmberSecondaryDark,
    onSecondary = AmberOnSecondaryDark,
    secondaryContainer = AmberContainerDark,
    onSecondaryContainer = OnAmberContainerDark,
    tertiary = CoralTertiaryDark,
    onTertiary = CoralOnTertiaryDark,
    tertiaryContainer = CoralContainerDark,
    onTertiaryContainer = OnCoralContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = EmeraldOnPrimaryLight,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = OnEmeraldContainerLight,
    secondary = AmberSecondaryLight,
    onSecondary = AmberOnSecondaryLight,
    secondaryContainer = AmberContainerLight,
    onSecondaryContainer = OnAmberContainerLight,
    tertiary = CoralTertiaryLight,
    onTertiary = CoralOnTertiaryLight,
    tertiaryContainer = CoralContainerLight,
    onTertiaryContainer = OnCoralContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
  )

@Composable
fun PantryPalTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  PantryPalTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

