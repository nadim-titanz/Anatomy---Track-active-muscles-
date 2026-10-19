package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutSession
import com.example.ui.WorkoutViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📅 Calendar Screen: Nothing OS Aesthetic
 * - Dot-Matrix Top Bar
 * - High-contrast monochromatic calendar matrix
 * - Nothing Red workout indicators
 * - Industrial session logs and pill actions
 */
@Composable
fun CalendarScreen(
  viewModel: WorkoutViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val completedSessions by viewModel.completedSessions.collectAsState()
  val selectedDateEpochDay by viewModel.selectedDateEpochDay.collectAsState()

  // Calendar month state
  val calendar = remember { Calendar.getInstance() }
  var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
  var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) } // 0-based

  // Map epoch days to sessions
  val sessionsByDay = remember(completedSessions) {
    completedSessions.groupBy { it.dateEpochDay }
  }

  // Calculate days in month
  val daysInMonth = remember(currentYear, currentMonth) {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, currentYear)
    cal.set(Calendar.MONTH, currentMonth)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0: Sunday, 1: Monday...
    Pair(totalDays, firstDayOfWeek)
  }

  val monthName = remember(currentMonth, currentYear) {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, currentMonth)
    cal.set(Calendar.YEAR, currentYear)
    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
  }

  val selectedDaySessions = sessionsByDay[selectedDateEpochDay] ?: emptyList()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 0. Top Title / Header: Scrolls with content, NOT fixed position
    item {
      NothingTopBar(
        title = "CALENDAR",
        subtitle = "WORKOUT HISTORY & TIMELINE",
        categoryCode = "NOTHING // LOGS",
        trailingContent = {
          NothingPillButton(
            text = "+ LOG",
            onClick = { viewModel.openAddWorkout(selectedDateEpochDay) },
            isPrimary = true
          )
        }
      )
    }

    // Calendar Matrix Card
    item {
        NothingCard(modifier = Modifier.fillMaxWidth()) {
          // Month switcher
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = monthName.uppercase(),
              fontSize = 15.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = NothingTextDisplay
            )

            Row {
              IconButton(
                onClick = {
                  if (currentMonth == 0) {
                    currentMonth = 11
                    currentYear -= 1
                  } else {
                    currentMonth -= 1
                  }
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = NothingTextDisplay)
              }

              IconButton(
                onClick = {
                  if (currentMonth == 11) {
                    currentMonth = 0
                    currentYear += 1
                  } else {
                    currentMonth += 1
                  }
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = NothingTextDisplay)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Weekday headers (S M T W T F S) in monospace
          val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")
          Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
              Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NothingTextDisabled
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Calendar Days Grid
          val (totalDays, offset) = daysInMonth
          val totalCells = totalDays + offset
          val rows = (totalCells + 6) / 7

          for (r in 0 until rows) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
            ) {
              for (c in 0 until 7) {
                val cellIndex = r * 7 + c
                val dayNum = cellIndex - offset + 1

                if (dayNum in 1..totalDays) {
                  val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.DAY_OF_MONTH, dayNum)
                    set(Calendar.HOUR_OF_DAY, 12)
                  }
                  val cellEpochDay = cal.timeInMillis / (24L * 60 * 60 * 1000)
                  val isSelected = cellEpochDay == selectedDateEpochDay
                  val hasWorkout = sessionsByDay.containsKey(cellEpochDay)

                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .aspectRatio(1f)
                      .padding(2.dp)
                      .clip(CircleShape)
                      .background(
                        when {
                          isSelected -> NothingTextDisplay
                          hasWorkout -> NothingSurfaceElevated
                          else -> Color.Transparent
                        }
                      )
                      .border(
                        width = if (hasWorkout && !isSelected) 1.dp else 0.dp,
                        color = if (hasWorkout && !isSelected) NothingBorder else Color.Transparent,
                        shape = CircleShape
                      )
                      .clickable { viewModel.selectDate(cellEpochDay) },
                    contentAlignment = Alignment.Center
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "$dayNum",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected || hasWorkout) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                          isSelected -> NothingOledBlack
                          hasWorkout -> NothingTextDisplay
                          else -> NothingTextDisabled
                        }
                      )
                      if (hasWorkout && !isSelected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                          modifier = Modifier
                            .size(3.5.dp)
                            .background(NothingRed, CircleShape)
                        )
                      }
                    }
                  }
                } else {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }
        }
      }

      // Selected Date Section
      val calSelected = Calendar.getInstance().apply {
        timeInMillis = selectedDateEpochDay * 24L * 60 * 60 * 1000 + (12 * 60 * 60 * 1000)
      }
      val selectedDateStr = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(calSelected.time)

      item {
        NothingSectionHeader(
          index = "DATE",
          title = selectedDateStr.uppercase(),
          trailingText = "${selectedDaySessions.size} LOGGED"
        )
      }

      if (selectedDaySessions.isEmpty()) {
        item {
          NothingCard(
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              NothingGlyphCircle(size = 64.dp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "REST DAY // NO LOGS",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = NothingTextDisplay
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "No training sessions recorded for this date",
                fontSize = 12.sp,
                color = NothingTextSecondary
              )
              Spacer(modifier = Modifier.height(14.dp))
              NothingPillButton(
                text = "LOG WORKOUT ON THIS DAY",
                onClick = { viewModel.openAddWorkout(selectedDateEpochDay) }
              )
            }
          }
        }
      } else {
        items(selectedDaySessions) { session ->
          val muscles = session.getMuscleList()
          NothingCard(
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = session.name,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Medium,
                  color = NothingTextDisplay
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${muscles.size} MUSCLE GROUPS TARGETED".uppercase(),
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  letterSpacing = 0.5.sp,
                  color = NothingRed
                )
              }

              Row {
                IconButton(
                  onClick = { viewModel.shareWorkoutSummary(context, session) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = NothingTextSecondary,
                    modifier = Modifier.size(16.dp)
                  )
                }

                Spacer(modifier = Modifier.width(6.dp))

                NothingPillButton(
                  text = "EDIT",
                  onClick = { viewModel.openEditWorkout(session) },
                  isPrimary = false
                )
              }
            }

            if (muscles.isNotEmpty()) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = muscles.joinToString(" · ") { it.displayName.uppercase() },
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp,
                color = NothingTextSecondary
              )
            }

            if (session.notes.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "// ${session.notes}",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = NothingTextDisabled
              )
            }
          }
        }
      }
    }
}
