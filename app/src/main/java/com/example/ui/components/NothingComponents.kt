package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// =================================================================
// Nothing OS 5x7 Dot-Matrix Font & Renderer
// Replicates the iconic Ndot LED matrix display font seen in Nothing Phone UI
// =================================================================

object DotMatrixFont {
  fun getGlyph(c: Char): IntArray {
    return when (c.uppercaseChar()) {
      'A' -> intArrayOf(0b01110, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001)
      'B' -> intArrayOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10001, 0b10001, 0b11110)
      'C' -> intArrayOf(0b01111, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b01111)
      'D' -> intArrayOf(0b11110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b11110)
      'E' -> intArrayOf(0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b11111)
      'F' -> intArrayOf(0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b10000)
      'G' -> intArrayOf(0b01111, 0b10000, 0b10000, 0b10111, 0b10001, 0b10001, 0b01111)
      'H' -> intArrayOf(0b10001, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001)
      'I' -> intArrayOf(0b11111, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b11111)
      'J' -> intArrayOf(0b00111, 0b00010, 0b00010, 0b00010, 0b00010, 0b10010, 0b01100)
      'K' -> intArrayOf(0b10001, 0b10010, 0b10100, 0b11000, 0b10100, 0b10010, 0b10001)
      'L' -> intArrayOf(0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b11111)
      'M' -> intArrayOf(0b10001, 0b11011, 0b10101, 0b10101, 0b10001, 0b10001, 0b10001)
      'N' -> intArrayOf(0b10001, 0b11001, 0b10101, 0b10011, 0b10001, 0b10001, 0b10001)
      'O' -> intArrayOf(0b01110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110)
      'P' -> intArrayOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10000, 0b10000, 0b10000)
      'Q' -> intArrayOf(0b01110, 0b10001, 0b10001, 0b10001, 0b10101, 0b10010, 0b01101)
      'R' -> intArrayOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10100, 0b10010, 0b10001)
      'S' -> intArrayOf(0b01111, 0b10000, 0b10000, 0b01110, 0b00001, 0b00001, 0b11110)
      'T' -> intArrayOf(0b11111, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100)
      'U' -> intArrayOf(0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110)
      'V' -> intArrayOf(0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01010, 0b00100)
      'W' -> intArrayOf(0b10001, 0b10001, 0b10001, 0b10101, 0b10101, 0b11011, 0b10001)
      'X' -> intArrayOf(0b10001, 0b10001, 0b01010, 0b00100, 0b01010, 0b10001, 0b10001)
      'Y' -> intArrayOf(0b10001, 0b10001, 0b01010, 0b00100, 0b00100, 0b00100, 0b00100)
      'Z' -> intArrayOf(0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b10000, 0b11111)
      '0' -> intArrayOf(0b01110, 0b10011, 0b10101, 0b10101, 0b11001, 0b10001, 0b01110)
      '1' -> intArrayOf(0b00100, 0b01100, 0b00100, 0b00100, 0b00100, 0b00100, 0b01110)
      '2' -> intArrayOf(0b01110, 0b10001, 0b00001, 0b00110, 0b01000, 0b10000, 0b11111)
      '3' -> intArrayOf(0b11110, 0b00001, 0b00001, 0b01110, 0b00001, 0b00001, 0b11110)
      '4' -> intArrayOf(0b00010, 0b00110, 0b01010, 0b10010, 0b11111, 0b00010, 0b00010)
      '5' -> intArrayOf(0b11111, 0b10000, 0b11110, 0b00001, 0b00001, 0b10001, 0b01110)
      '6' -> intArrayOf(0b00110, 0b01000, 0b10000, 0b11110, 0b10001, 0b10001, 0b01110)
      '7' -> intArrayOf(0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b01000, 0b01000)
      '8' -> intArrayOf(0b01110, 0b10001, 0b10001, 0b01110, 0b10001, 0b10001, 0b01110)
      '9' -> intArrayOf(0b01110, 0b10001, 0b10001, 0b01111, 0b00001, 0b00010, 0b01100)
      ':' -> intArrayOf(0b00000, 0b01100, 0b01100, 0b00000, 0b01100, 0b01100, 0b00000)
      '.' -> intArrayOf(0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b01100, 0b01100)
      '-' -> intArrayOf(0b00000, 0b00000, 0b00000, 0b11111, 0b00000, 0b00000, 0b00000)
      '+' -> intArrayOf(0b00000, 0b00100, 0b00100, 0b11111, 0b00100, 0b00100, 0b00000)
      '/' -> intArrayOf(0b00001, 0b00010, 0b00100, 0b00100, 0b01000, 0b10000, 0b00000)
      '(' -> intArrayOf(0b00010, 0b00100, 0b01000, 0b01000, 0b01000, 0b00100, 0b00010)
      ')' -> intArrayOf(0b01000, 0b00100, 0b00010, 0b00010, 0b00010, 0b00100, 0b01000)
      '[' -> intArrayOf(0b01110, 0b01000, 0b01000, 0b01000, 0b01000, 0b01000, 0b01110)
      ']' -> intArrayOf(0b01110, 0b00010, 0b00010, 0b00010, 0b00010, 0b00010, 0b01110)
      else -> intArrayOf(0, 0, 0, 0, 0, 0, 0)
    }
  }
}

/**
 * Authentic Nothing OS Dot-Matrix LED Text
 */
@Composable
fun DotMatrixText(
  text: String,
  modifier: Modifier = Modifier,
  dotSize: Dp = 2.4.dp,
  dotSpacing: Dp = 1.2.dp,
  charSpacing: Dp = 4.dp,
  activeColor: Color = NothingTextDisplay,
  inactiveColor: Color = Color.Transparent
) {
  val density = LocalDensity.current
  val dotSizePx = with(density) { dotSize.toPx() }
  val dotSpacingPx = with(density) { dotSpacing.toPx() }
  val charSpacingPx = with(density) { charSpacing.toPx() }

  val charWidthPx = 5 * dotSizePx + 4 * dotSpacingPx
  val totalWidthPx = text.length * charWidthPx + maxOf(0, text.length - 1) * charSpacingPx
  val totalHeightPx = 7 * dotSizePx + 6 * dotSpacingPx

  val totalWidthDp = with(density) { totalWidthPx.toDp() }
  val totalHeightDp = with(density) { totalHeightPx.toDp() }

  Canvas(
    modifier = modifier
      .width(totalWidthDp)
      .height(totalHeightDp)
  ) {
    var startX = 0f
    for (char in text) {
      if (char == ' ') {
        startX += charWidthPx + charSpacingPx
        continue
      }
      val glyph = DotMatrixFont.getGlyph(char)
      for (row in 0 until 7) {
        val rowBits = glyph[row]
        for (col in 0 until 5) {
          val isActive = (rowBits and (1 shl (4 - col))) != 0
          val cx = startX + col * (dotSizePx + dotSpacingPx) + dotSizePx / 2f
          val cy = row * (dotSizePx + dotSpacingPx) + dotSizePx / 2f
          val color = if (isActive) activeColor else inactiveColor
          if (color != Color.Transparent) {
            drawCircle(
              color = color,
              radius = dotSizePx / 2f,
              center = Offset(cx, cy)
            )
          }
        }
      }
      startX += charWidthPx + charSpacingPx
    }
  }
}

/**
 * Nothing OS Standard Top Bar
 */
@Composable
fun NothingTopBar(
  title: String,
  subtitle: String? = null,
  categoryCode: String = "NOTHING // RECOVERY",
  trailingContent: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(top = 16.dp, bottom = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Red technical dot indicator (Nothing signature)
        Box(
          modifier = Modifier
            .size(6.dp)
            .background(NothingRed, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = categoryCode,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.5.sp,
          color = NothingTextSecondary
        )
      }

      trailingContent?.invoke()
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Dot Matrix Title
    DotMatrixText(
      text = title.uppercase(),
      dotSize = 2.6.dp,
      dotSpacing = 1.2.dp,
      charSpacing = 4.5.dp,
      activeColor = NothingTextDisplay
    )

    if (subtitle != null) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = subtitle,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 0.5.sp,
        color = NothingTextSecondary
      )
    }
  }
}

/**
 * Nothing OS Container Card:
 * Max 16dp radius, OLED surface, 1px technical border, zero shadow
 */
@Composable
fun NothingCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val clickModifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(NothingSurface)
      .border(1.dp, NothingBorder, RoundedCornerShape(16.dp))
      .then(clickModifier)
      .padding(18.dp),
    content = content
  )
}

/**
 * Nothing OS Technical Pill Button (like "MORE" in Nothing Phone UI)
 */
@Composable
fun NothingPillButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isPrimary: Boolean = true
) {
  val bgColor = if (isPrimary) NothingTextDisplay else NothingSurface
  val textColor = if (isPrimary) NothingOledBlack else NothingTextDisplay
  val border = if (isPrimary) null else androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(999.dp))
      .background(bgColor)
      .then(if (border != null) Modifier.border(border.width, border.brush, RoundedCornerShape(999.dp)) else Modifier)
      .clickable { onClick() }
      .padding(horizontal = 18.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text.uppercase(),
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.2.sp,
      color = textColor
    )
  }
}

/**
 * Nothing OS Section Header with technical index (e.g. "01 // RECENTLY TRAINED")
 */
@Composable
fun NothingSectionHeader(
  index: String,
  title: String,
  modifier: Modifier = Modifier,
  trailingText: String? = null
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "$index //",
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp,
        color = NothingRed
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = title.uppercase(),
        fontSize = 13.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = NothingTextDisplay
      )
    }

    if (trailingText != null) {
      Text(
        text = trailingText,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        color = NothingTextSecondary
      )
    }
  }
}

/**
 * Nothing OS Circular Glyph Matrix (matches the circular widgets in Nothing Phone UI)
 */
@Composable
fun NothingGlyphCircle(
  modifier: Modifier = Modifier,
  size: Dp = 90.dp,
  activeIntensity: Float = 0.85f
) {
  val density = LocalDensity.current
  Canvas(modifier = modifier.size(size)) {
    val r = this.size.minDimension / 2f
    val center = Offset(r, r)
    val dotCount = 14
    val step = this.size.minDimension / dotCount

    // Draw background boundary ring
    drawCircle(
      color = NothingBorder,
      radius = r,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
    )

    // Draw circular grid of LED dots
    for (i in 0 until dotCount) {
      for (j in 0 until dotCount) {
        val dx = (i + 0.5f) * step - r
        val dy = (j + 0.5f) * step - r
        val dist = kotlin.math.sqrt(dx * dx + dy * dy)
        if (dist <= r - 4.dp.toPx()) {
          val isGlyphActive = ((i in 4..9 && j in 3..10) || (i + j) % 3 == 0) && (dist > r * 0.35f)
          val dotColor = if (isGlyphActive) NothingTextDisplay.copy(alpha = activeIntensity) else NothingDotInactive
          drawCircle(
            color = dotColor,
            radius = 1.4.dp.toPx(),
            center = Offset(center.x + dx, center.y + dy)
          )
        }
      }
    }
  }
}

/**
 * Nothing OS Pagination Dots (as seen in the screenshot at the bottom: • • • • •)
 */
@Composable
fun NothingPaginationDots(
  total: Int = 5,
  active: Int = 1,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 0 until total) {
      val isSelected = i == active
      Box(
        modifier = Modifier
          .size(if (isSelected) 6.dp else 4.dp)
          .background(if (isSelected) NothingTextDisplay else NothingTextDisabled, CircleShape)
      )
    }
  }
}
