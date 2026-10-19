package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Nothing OS Typography:
// - Space Grotesk / Geometric Sans for Primary Numbers & Display
// - Monospace (Space Mono aesthetic) for Technical Labels, Captions, Tags in ALL CAPS
val Typography =
  Typography(
    displayLarge = TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Medium,
      fontSize = 32.sp,
      lineHeight = 38.sp,
      letterSpacing = (-0.5).sp,
      color = NothingTextDisplay
    ),
    titleLarge = TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = 20.sp,
      lineHeight = 26.sp,
      letterSpacing = 0.sp,
      color = NothingTextDisplay
    ),
    titleMedium = TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = 16.sp,
      lineHeight = 22.sp,
      letterSpacing = 0.sp,
      color = NothingTextDisplay
    ),
    bodyLarge = TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = 15.sp,
      lineHeight = 22.sp,
      letterSpacing = 0.2.sp,
      color = NothingTextPrimary
    ),
    bodyMedium = TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = 13.sp,
      lineHeight = 18.sp,
      letterSpacing = 0.2.sp,
      color = NothingTextSecondary
    ),
    labelSmall = TextStyle(
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Normal,
      fontSize = 11.sp,
      lineHeight = 15.sp,
      letterSpacing = 1.2.sp,
      color = NothingTextSecondary
    ),
    labelMedium = TextStyle(
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Medium,
      fontSize = 12.sp,
      lineHeight = 16.sp,
      letterSpacing = 1.0.sp,
      color = NothingTextPrimary
    )
  )
