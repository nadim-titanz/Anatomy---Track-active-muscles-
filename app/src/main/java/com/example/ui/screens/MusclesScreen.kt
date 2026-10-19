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
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.data.RecencyState
import com.example.ui.WorkoutViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 💪 Muscles Screen: Nothing OS Aesthetic
 * - Dot-Matrix Top Bar
 * - Technical attention list
 * - High-contrast monochromatic coverage radar table
 */
@Composable
fun MusclesScreen(
  viewModel: WorkoutViewModel,
  modifier: Modifier = Modifier
) {
  val allMusclesCoverage by viewModel.allMusclesCoverage.collectAsState()
  val neglected by viewModel.neglectedMuscles.collectAsState()

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
        title = "MUSCLE RADAR",
        subtitle = "RECOVERY & TRAINING FREQUENCY",
        categoryCode = "NOTHING // METRICS"
      )
    }

    // Section 01: Needs Attention / Untrained
      if (neglected.isNotEmpty()) {
        item {
          NothingSectionHeader(
            index = "01",
            title = "NEEDS ATTENTION",
            trailingText = "${neglected.size} AREAS"
          )
        }

        item {
          NothingCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              neglected.take(4).forEach { info ->
                val daysLabel = when (info.daysAgo) {
                  null -> "NEVER"
                  0L -> "TODAY"
                  1L -> "1D AGO"
                  else -> "${info.daysAgo}D AGO"
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = NothingSurfaceElevated,
                  border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectMuscle(info.muscleGroup) }
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
                          .size(5.dp)
                          .background(NothingRed, CircleShape)
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(
                        text = info.muscleGroup.displayName.uppercase(),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = NothingTextDisplay
                      )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = daysLabel,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NothingRed
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = NothingTextDisabled,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Section 02: All Muscles Coverage Matrix
      item {
        val nextIdx = if (neglected.isNotEmpty()) "02" else "01"
        NothingSectionHeader(
          index = nextIdx,
          title = "ALL TARGET GROUPS",
          trailingText = "${allMusclesCoverage.size} TOTAL"
        )
      }

      // Table Header Row
      item {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = NothingSurface,
          border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "TARGET",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = NothingTextDisabled,
              modifier = Modifier.weight(1.8f)
            )
            Text(
              text = "LAST LOGGED",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = NothingTextDisabled,
              modifier = Modifier.weight(1.6f)
            )
            Text(
              text = "STATUS",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = NothingTextDisabled,
              modifier = Modifier.weight(1.2f)
            )
          }
        }
      }

      // Table Data Rows
      items(allMusclesCoverage) { info ->
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        val lastTrainedStr = info.lastTrainedTimestamp?.let { sdf.format(Date(it)).uppercase() } ?: "—"
        val daysAgoStr = info.daysAgo?.let { "${it}D" } ?: "NEVER"

        NothingCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("muscle_coverage_row_${info.muscleGroup.name}")
            .clickable { viewModel.selectMuscle(info.muscleGroup) }
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Target Name + Status Dot
            Row(
              modifier = Modifier.weight(1.8f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .background(
                    if (info.state == RecencyState.NEEDS_ATTENTION) NothingTextDisabled else info.state.color,
                    CircleShape
                  )
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = info.muscleGroup.displayName.uppercase(),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = NothingTextDisplay
              )
            }

            // Last Logged
            Text(
              text = lastTrainedStr,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = NothingTextSecondary,
              modifier = Modifier.weight(1.6f)
            )

            // Status Pill
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = NothingSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
              modifier = Modifier.weight(1.2f)
            ) {
              Text(
                text = "[ $daysAgoStr ]",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = if (info.state == RecencyState.NEEDS_ATTENTION) NothingTextDisabled else info.state.color,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }
        }
      }
    }
}
