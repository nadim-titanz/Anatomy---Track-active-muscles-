package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WorkoutViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * ⚙️ Settings Screen: Nothing OS Aesthetic
 * - Dot-Matrix Top Bar
 * - Technical telemetry thresholds
 * - Industrial data storage management
 */
@Composable
fun SettingsScreen(
  viewModel: WorkoutViewModel,
  modifier: Modifier = Modifier
) {
  var showResetConfirmation by remember { mutableStateOf(false) }
  var showClearConfirmation by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 0. Top Title: Scrolls naturally with the page, NOT fixed position
    NothingTopBar(
      title = "SETTINGS",
      subtitle = "DATA PREFERENCES & TELEMETRY",
      categoryCode = "NOTHING // SYSTEM"
    )

    // Section 01: Muscle Map Legend & Thresholds
      NothingSectionHeader(
        index = "01",
        title = "STATUS THRESHOLDS",
        trailingText = "RECENCY"
      )

      NothingCard(modifier = Modifier.fillMaxWidth()) {
        ThresholdRow(
          dotColor = NothingRed,
          title = "RECENT [1–3 DAYS]",
          subtitle = "Trained within the last 1 to 3 days"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ThresholdRow(
          dotColor = StatusWarningAmber,
          title = "MORE THAN 3 DAYS",
          subtitle = "Trained 4 days ago"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ThresholdRow(
          dotColor = StatusGreyRecovered,
          title = "5–7 DAYS AGO",
          subtitle = "Trained 5 to 7 days ago"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ThresholdRow(
          dotColor = NothingBorder,
          title = "8+ DAYS / UNTRAINED",
          subtitle = "Needs attention (8+ days ago or never logged)"
        )
      }

      // Section 02: Data Management
      NothingSectionHeader(
        index = "02",
        title = "DATA MANAGEMENT",
        trailingText = "LOCAL STORAGE"
      )

      NothingCard(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "// LOGGED WORKOUT SESSIONS",
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          color = NothingTextSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Reset Sample Data Button
        OutlinedButton(
          onClick = { showResetConfirmation = true },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reset_sample_data_btn"),
          shape = RoundedCornerShape(999.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = NothingTextDisplay),
          border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "LOAD SAMPLE DATA (PULL, PUSH, LEGS)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Clear all button
        Button(
          onClick = { showClearConfirmation = true },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("clear_all_data_btn"),
          shape = RoundedCornerShape(999.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = NothingRed.copy(alpha = 0.15f),
            contentColor = NothingRed
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, NothingRed.copy(alpha = 0.4f))
        ) {
          Text(
            text = "CLEAR ALL WORKOUT HISTORY",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }

  if (showClearConfirmation) {
    AlertDialog(
      onDismissRequest = { showClearConfirmation = false },
      containerColor = NothingSurface,
      shape = RoundedCornerShape(16.dp),
      title = {
        Text(
          text = "CLEAR ALL HISTORY?",
          color = NothingTextDisplay,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(
          text = "This will permanently delete all logged workouts and reset all muscles to untrained.",
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          color = NothingTextSecondary
        )
      },
      confirmButton = {
        NothingPillButton(
          text = "CONFIRM CLEAR",
          onClick = {
            viewModel.clearAllData()
            showClearConfirmation = false
          },
          isPrimary = true
        )
      },
      dismissButton = {
        NothingPillButton(
          text = "CANCEL",
          onClick = { showClearConfirmation = false },
          isPrimary = false
        )
      }
    )
  }

  if (showResetConfirmation) {
    AlertDialog(
      onDismissRequest = { showResetConfirmation = false },
      containerColor = NothingSurface,
      shape = RoundedCornerShape(16.dp),
      title = {
        Text(
          text = "RELOAD SAMPLE DATA?",
          color = NothingTextDisplay,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(
          text = "This will replace your current logs with standard sample workouts (Pull, Push, Legs, Upper).",
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          color = NothingTextSecondary
        )
      },
      confirmButton = {
        NothingPillButton(
          text = "CONFIRM LOAD",
          onClick = {
            viewModel.resetToSampleData()
            showResetConfirmation = false
          },
          isPrimary = true
        )
      },
      dismissButton = {
        NothingPillButton(
          text = "CANCEL",
          onClick = { showResetConfirmation = false },
          isPrimary = false
        )
      }
    )
  }
}

@Composable
private fun ThresholdRow(
  dotColor: Color,
  title: String,
  subtitle: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .background(dotColor, CircleShape)
        .border(1.dp, NothingBorder, CircleShape)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(
        text = title,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = NothingTextDisplay
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = NothingTextSecondary
      )
    }
  }
}
