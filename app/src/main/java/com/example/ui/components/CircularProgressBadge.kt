package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextPrimary

@Composable
fun CircularProgressBadge(
  number: String,
  progress: Float = 0.75f,
  size: Dp = 54.dp,
  strokeWidth: Dp = 3.dp,
  activeColor: Color = TextPrimary,
  trackColor: Color = Color(0xFF2C2C34),
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val strokePx = strokeWidth.toPx()
      // Draw background track ring
      drawArc(
        color = trackColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = strokePx)
      )
      // Draw progress arc
      drawArc(
        color = activeColor,
        startAngle = -90f,
        sweepAngle = 360f * progress,
        useCenter = false,
        style = Stroke(width = strokePx, cap = StrokeCap.Round)
      )
    }

    Text(
      text = number,
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )
  }
}
