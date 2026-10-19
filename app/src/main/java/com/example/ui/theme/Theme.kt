package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
  darkColorScheme(
    primary = TextPrimary,
    onPrimary = DarkBg,
    primaryContainer = CardElevatedBg,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = TextPrimary,
    secondaryContainer = CardBg,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentOrange,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = CardBg,
    onSurface = TextPrimary,
    surfaceVariant = CardElevatedBg,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = DarkBg.toArgb()
        window.navigationBarColor = DarkBg.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

