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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MuscleRecoveryInfo
import com.example.data.WorkoutSession
import com.example.ui.WorkoutViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📱 Overview Screen: Nothing OS Aesthetic
 * - Dot-Matrix System Top Bar
 * - Anatomical Heatmap with LED grid backdrop
 * - Technical index headers (01 // RECENTLY TRAINED, 02 // LOGGED SESSIONS)
 * - Industrial telemetry cards and Nothing Pill buttons
 */
@Composable
fun OverviewScreen(
  viewModel: WorkoutViewModel,
  onOpenAddWorkout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val recoveryMap by viewModel.recoveryMap.collectAsState()
  val selectedMuscle by viewModel.selectedMuscle.collectAsState()
  val recentlyTrained by viewModel.recentlyTrainedMuscles.collectAsState()
  val fewDaysAgo by viewModel.fewDaysAgoMuscles.collectAsState()
  val completedSessions by viewModel.completedSessions.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 0. Top Title / Header: Scrolls naturally with the page, NOT fixed position
    item {
      NothingTopBar(
        title = "BODY HEATMAP",
        subtitle = "16 TARGET GROUPS MONITORED",
        categoryCode = "NOTHING // RECOVERY",
        trailingContent = {
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = NothingSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(5.dp)
                  .background(NothingRed, CircleShape)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "LIVE",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = NothingTextDisplay
              )
            }
          }
        }
      )
    }

    // 1. Visual Human Muscle Map (Nothing Card container)
    item {
      NothingCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("overview_body_map_card")
      ) {
        AnatomicalHeatmap(
          recoveryMap = recoveryMap,
          selectedMuscle = selectedMuscle,
          onMuscleSelected = { viewModel.selectMuscle(it) },
          onLogMuscleWorkout = { muscle ->
            viewModel.selectMuscle(null)
            viewModel.openAddWorkout()
          }
        )
      }
    }

      // 2. Section 01: Recently Trained Muscles (1-3 days ago = Red)
      item {
        NothingSectionHeader(
          index = "01",
          title = "RECENTLY TRAINED",
          trailingText = "${recentlyTrained.size} ACTIVE"
        )
      }

      item {
        NothingCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("recently_trained_card")
        ) {
          if (recentlyTrained.isEmpty()) {
            Text(
              text = "// NO ACTIVE LOGS IN LAST 3 DAYS",
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace,
              color = NothingTextDisabled
            )
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              recentlyTrained.forEach { info ->
                MuscleStatusRow(
                  info = info,
                  onClick = { viewModel.selectMuscle(info.muscleGroup) }
                )
              }
            }
          }
        }
      }

      // 3. Section 02: Trained a few days ago (3-4 days ago)
      if (fewDaysAgo.isNotEmpty()) {
        item {
          NothingSectionHeader(
            index = "02",
            title = "TRAINED 3–4D AGO",
            trailingText = "${fewDaysAgo.size} GROUPS"
          )
        }

        item {
          NothingCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              fewDaysAgo.forEach { info ->
                MuscleStatusRow(
                  info = info,
                  onClick = { viewModel.selectMuscle(info.muscleGroup) }
                )
              }
            }
          }
        }
      }

      // 4. Section 03: Recent Logged Workouts
      item {
        val nextIndex = if (fewDaysAgo.isNotEmpty()) "03" else "02"
        NothingSectionHeader(
          index = nextIndex,
          title = "RECENT LOGS",
          trailingText = "TAP TO EDIT"
        )
      }

      if (completedSessions.isEmpty()) {
        item {
          NothingCard(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenAddWorkout() }
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              NothingGlyphCircle(size = 72.dp)
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "NO WORKOUTS RECORDED",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = NothingTextDisplay
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap to initialize your muscle map",
                fontSize = 12.sp,
                color = NothingTextSecondary
              )
              Spacer(modifier = Modifier.height(16.dp))
              NothingPillButton(
                text = "LOG FIRST WORKOUT",
                onClick = onOpenAddWorkout
              )
            }
          }
        }
      } else {
        items(completedSessions.take(4)) { session ->
          WorkoutItemCard(
            session = session,
            onEdit = { viewModel.openEditWorkout(session) }
          )
        }
      }
    }
}

@Composable
private fun MuscleStatusRow(
  info: MuscleRecoveryInfo,
  onClick: () -> Unit
) {
  val daysLabel = when (info.daysAgo) {
    null -> "UNTRAINED"
    0L -> "TODAY"
    1L -> "1D AGO"
    else -> "${info.daysAgo}D AGO"
  }

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = NothingSurfaceElevated,
    border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .background(info.state.color, CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = info.muscleGroup.displayName.uppercase(),
          fontSize = 13.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Medium,
          letterSpacing = 0.8.sp,
          color = NothingTextDisplay
        )
      }

      Surface(
        shape = RoundedCornerShape(4.dp),
        color = NothingSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)
      ) {
        Text(
          text = "[ $daysLabel ]",
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp,
          color = info.state.color,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
      }
    }
  }
}

@Composable
private fun WorkoutItemCard(
  session: WorkoutSession,
  onEdit: () -> Unit
) {
  val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
  val dateFormatted = sdf.format(Date(session.timestamp))
  val muscles = session.getMuscleList()

  NothingCard(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onEdit() }
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "// $dateFormatted • ${session.dayOfWeek}".uppercase(),
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp,
          color = NothingTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = session.name,
          fontSize = 17.sp,
          fontWeight = FontWeight.Medium,
          color = NothingTextDisplay
        )
      }

      NothingPillButton(
        text = "EDIT",
        onClick = onEdit,
        isPrimary = false
      )
    }

    if (muscles.isNotEmpty()) {
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = muscles.joinToString(" · ") { it.displayName.uppercase() },
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 0.6.sp,
        color = NothingTextSecondary
      )
    }
  }
}
