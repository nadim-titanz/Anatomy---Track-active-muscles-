package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutSession
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

/**
 * 3-Month Dot Matrix activity visualization matching Screenshot 1.
 * Shows activity dots for past months with active workout days highlighted as bright glowing dots.
 */
@Composable
fun ActivityDotMatrix(
  sessions: List<WorkoutSession>,
  modifier: Modifier = Modifier
) {
  val cal = Calendar.getInstance()
  val currentMonth = cal.get(Calendar.MONTH) // 0-indexed
  val currentYear = cal.get(Calendar.YEAR)

  // Get 3 consecutive months ending with current month
  val monthIndices = listOf(
    (currentMonth - 2 + 12) % 12,
    (currentMonth - 1 + 12) % 12,
    currentMonth
  )
  val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

  // Extract set of day-of-year or epoch days that had completed workouts
  val completedEpochDays = sessions
    .filter { it.isCompleted }
    .map { it.dateEpochDay }
    .toSet()

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    monthIndices.forEachIndexed { i, monthIdx ->
      val monthName = monthNames[monthIdx]
      val monthYear = if (monthIdx > currentMonth) currentYear - 1 else currentYear

      MonthDotColumn(
        monthName = monthName,
        monthIndex = monthIdx,
        year = monthYear,
        completedEpochDays = completedEpochDays,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun MonthDotColumn(
  monthName: String,
  monthIndex: Int,
  year: Int,
  completedEpochDays: Set<Long>,
  modifier: Modifier = Modifier
) {
  val cal = Calendar.getInstance().apply {
    set(Calendar.YEAR, year)
    set(Calendar.MONTH, monthIndex)
    set(Calendar.DAY_OF_MONTH, 1)
  }
  val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
  val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday

  Column(
    modifier = modifier.padding(horizontal = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = monthName,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      color = TextSecondary,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    // 4 rows x 7 cols dot grid (28 dots representation)
    val totalGridDots = 28
    val rows = 4
    val cols = 7

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      for (r in 0 until rows) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (c in 0 until cols) {
            val dayIndex = r * cols + c + 1
            val isActive = if (dayIndex <= daysInMonth) {
              cal.set(Calendar.DAY_OF_MONTH, dayIndex)
              val epochDay = cal.timeInMillis / (24L * 60 * 60 * 1000)
              completedEpochDays.contains(epochDay)
            } else {
              false
            }

            val dotColor = if (isActive) {
              TextPrimary // Glowing pure white active dot
            } else {
              Color(0xFF2E2E36) // Muted subtle dark slate inactive dot
            }

            Box(
              modifier = Modifier
                .size(if (isActive) 5.dp else 4.dp)
                .background(dotColor, CircleShape)
            )
          }
        }
      }
    }
  }
}
