package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =================================================================
// Nothing OS Design System Tokens
// Monochromatic OLED canvas, technical typography, and signature Red
// =================================================================

// Canvas & Surfaces (OLED True Black per Nothing OS)
val NothingOledBlack = Color(0xFF000000)
val NothingSurface = Color(0xFF111111)
val NothingSurfaceElevated = Color(0xFF161616)
val NothingSurfaceHover = Color(0xFF1F1F1F)
val NothingBorder = Color(0xFF262626)
val NothingBorderSubtle = Color(0xFF1C1C1C)
val NothingDotInactive = Color(0xFF1C1C1E)

// Typography Levels per nothing-design (Space Grotesk + Space Mono)
val NothingTextDisplay = Color(0xFFFFFFFF)     // 100% Display / Hero
val NothingTextPrimary = Color(0xFFEEEEEE)     // 90% Body / Values
val NothingTextSecondary = Color(0xFF8E8E93)   // 60% Labels / Captions
val NothingTextDisabled = Color(0xFF48484A)    // 40% Hints / System

// Nothing Signature Red Accent (Interrupt / Live Event)
val NothingRed = Color(0xFFD71921)
val NothingRedDim = Color(0x33D71921)
val NothingRedGlow = Color(0x66D71921)

// Data Status Colors
val StatusRecentRed = Color(0xFFD71921)        // 1-3 days (Recent)
val StatusWarningAmber = Color(0xFFF5A623)     // > 3 days (Warning)
val StatusGreyRecovered = Color(0xFF71717A)    // 5-7 days (Recovered)
val StatusUntrainedBlack = Color(0xFF111111)   // 8+ days / Untrained

// Aliases for compatibility across existing screens
val DarkBg = NothingOledBlack
val CardBg = NothingSurface
val CardElevatedBg = NothingSurfaceElevated
val CardBorder = NothingBorder
val TextPrimary = NothingTextDisplay
val TextSecondary = NothingTextSecondary
val TextTertiary = NothingTextDisabled

val HeatmapRed = StatusRecentRed
val HeatmapOrange = StatusWarningAmber
val AccentOrange = StatusWarningAmber
val AccentRed = NothingRed
val HeatmapGray = StatusGreyRecovered
val HeatmapBodyBase = StatusUntrainedBlack
val HeatmapBodyContour = Color(0xFF2E2E32)
val HeatmapPlatform = Color(0xFF000000)

val AccentEmerald = Color(0xFF10B981)
val AccentBlue = Color(0xFF38BDF8)
val AccentPurple = Color(0xFFA855F7)
