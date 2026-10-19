package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import com.example.data.MuscleGroup
import com.example.data.WorkoutSession
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Full Manual Workout Logger and Editor: Nothing OS Aesthetic
 * - Dot-matrix / Monospace technical typography
 * - High-contrast selection chips
 * - Minimalist Nothing Pill buttons
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutEditorSheet(
  session: WorkoutSession?,
  onDismiss: () -> Unit,
  onSave: (
    id: Long,
    name: String,
    timestamp: Long,
    muscles: List<MuscleGroup>,
    notes: String
  ) -> Unit,
  onDelete: ((Long) -> Unit)? = null
) {
  val isEditing = session != null && session.id != 0L

  var workoutName by remember { mutableStateOf(session?.name ?: "") }
  var notesText by remember { mutableStateOf(session?.notes ?: "") }
  var currentTimestamp by remember { mutableStateOf(session?.timestamp ?: System.currentTimeMillis()) }

  val selectedMuscles = remember {
    mutableStateListOf<MuscleGroup>().apply {
      if (session != null) {
        addAll(session.getMuscleList())
      }
    }
  }

  val quickTemplates = listOf(
    "Pull Day" to listOf(MuscleGroup.LATS, MuscleGroup.UPPER_BACK, MuscleGroup.BICEPS, MuscleGroup.FOREARMS, MuscleGroup.REAR_DELTS),
    "Push Day" to listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_DELTS, MuscleGroup.SIDE_DELTS, MuscleGroup.TRICEPS),
    "Leg Day" to listOf(MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.CALVES),
    "Upper Body" to listOf(MuscleGroup.CHEST, MuscleGroup.UPPER_BACK, MuscleGroup.LATS, MuscleGroup.FRONT_DELTS, MuscleGroup.BICEPS, MuscleGroup.TRICEPS),
    "Arms + Abs" to listOf(MuscleGroup.BICEPS, MuscleGroup.TRICEPS, MuscleGroup.FOREARMS, MuscleGroup.ABS, MuscleGroup.OBLIQUES)
  )

  var showDatePicker by remember { mutableStateOf(false) }
  val datePickerState = rememberDatePickerState(
    initialSelectedDateMillis = currentTimestamp
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = NothingSurface,
    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 10.dp)
          .width(36.dp)
          .height(3.dp)
          .background(NothingBorder, RoundedCornerShape(999.dp))
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Header in Nothing OS style
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(NothingRed, CircleShape)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isEditing) "// EDIT WORKOUT" else "// LOG WORKOUT",
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = NothingTextDisplay
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (isEditing && onDelete != null) {
            IconButton(
              onClick = { onDelete(session!!.id) },
              modifier = Modifier.testTag("delete_workout_btn")
            ) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Workout", tint = NothingRed, modifier = Modifier.size(18.dp))
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NothingTextSecondary, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Date Selector
      val sdfDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = NothingSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showDatePicker = true }
          .testTag("workout_date_selector")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "DATE",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = NothingTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = sdfDate.format(Date(currentTimestamp)).uppercase(),
              fontSize = 14.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = NothingTextDisplay
            )
          }

          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Change Date",
            tint = NothingTextDisplay,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Workout Name Field
      OutlinedTextField(
        value = workoutName,
        onValueChange = { workoutName = it },
        label = {
          Text(
            "WORKOUT NAME",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = NothingTextSecondary
          )
        },
        placeholder = {
          Text(
            "e.g. Pull Day, Legs, Upper Body",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = NothingTextDisabled
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("workout_name_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = NothingTextDisplay,
          unfocusedTextColor = NothingTextDisplay,
          focusedBorderColor = NothingTextDisplay,
          unfocusedBorderColor = NothingBorder,
          focusedContainerColor = NothingSurfaceElevated,
          unfocusedContainerColor = NothingSurfaceElevated
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Templates row
      Text(
        text = "PRESETS",
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = NothingTextSecondary
      )
      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(quickTemplates) { (presetName, presetMuscles) ->
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = NothingSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
            modifier = Modifier.clickable {
              workoutName = presetName
              selectedMuscles.clear()
              selectedMuscles.addAll(presetMuscles)
            }
          ) {
            Text(
              text = presetName.uppercase(),
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Medium,
              letterSpacing = 0.5.sp,
              color = NothingTextDisplay,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Muscle Selection Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "TARGET MUSCLES [${selectedMuscles.size}]",
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = NothingTextDisplay
        )

        if (selectedMuscles.isNotEmpty()) {
          Text(
            text = "CLEAR ALL",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = NothingRed,
            modifier = Modifier.clickable { selectedMuscles.clear() }
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      val allMuscles = MuscleGroup.values().toList()
      val frontMuscles = allMuscles.filter { it.isFront }
      val backMuscles = allMuscles.filter { !it.isFront }

      Text(
        text = "// FRONT BODY",
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        color = NothingTextDisabled
      )
      Spacer(modifier = Modifier.height(6.dp))
      MuscleChipsFlow(
        muscles = frontMuscles,
        selectedMuscles = selectedMuscles,
        onToggle = { m ->
          if (selectedMuscles.contains(m)) selectedMuscles.remove(m)
          else selectedMuscles.add(m)
        }
      )

      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "// BACK BODY",
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        color = NothingTextDisabled
      )
      Spacer(modifier = Modifier.height(6.dp))
      MuscleChipsFlow(
        muscles = backMuscles,
        selectedMuscles = selectedMuscles,
        onToggle = { m ->
          if (selectedMuscles.contains(m)) selectedMuscles.remove(m)
          else selectedMuscles.add(m)
        }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Notes
      OutlinedTextField(
        value = notesText,
        onValueChange = { notesText = it },
        label = {
          Text(
            "NOTES (OPTIONAL)",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = NothingTextSecondary
          )
        },
        placeholder = {
          Text(
            "Felt strong, good pump, 4 sets...",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = NothingTextDisabled
          )
        },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = NothingTextDisplay,
          unfocusedTextColor = NothingTextDisplay,
          focusedBorderColor = NothingTextDisplay,
          unfocusedBorderColor = NothingBorder,
          focusedContainerColor = NothingSurfaceElevated,
          unfocusedContainerColor = NothingSurfaceElevated
        )
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Save Button: Nothing Pill Button
      NothingPillButton(
        text = if (isEditing) "SAVE CHANGES" else "SAVE WORKOUT",
        onClick = {
          val id = session?.id ?: 0L
          onSave(id, workoutName, currentTimestamp, selectedMuscles.toList(), notesText)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_workout_confirm_btn")
      )
    }
  }

  // DatePickerDialog with Nothing OS style
  if (showDatePicker) {
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        NothingPillButton(
          text = "SELECT",
          onClick = {
            datePickerState.selectedDateMillis?.let {
              currentTimestamp = it
            }
            showDatePicker = false
          },
          isPrimary = true
        )
      },
      dismissButton = {
        NothingPillButton(
          text = "CANCEL",
          onClick = { showDatePicker = false },
          isPrimary = false
        )
      }
    ) {
      DatePicker(
        state = datePickerState,
        colors = DatePickerDefaults.colors(
          containerColor = NothingSurface,
          titleContentColor = NothingTextDisplay,
          headlineContentColor = NothingTextDisplay,
          selectedDayContainerColor = NothingTextDisplay,
          selectedDayContentColor = NothingOledBlack
        )
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MuscleChipsFlow(
  muscles: List<MuscleGroup>,
  selectedMuscles: List<MuscleGroup>,
  onToggle: (MuscleGroup) -> Unit
) {
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    muscles.forEach { muscle ->
      val isSelected = selectedMuscles.contains(muscle)
      Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (isSelected) NothingTextDisplay else NothingSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isSelected) NothingTextDisplay else NothingBorder
        ),
        modifier = Modifier
          .testTag("muscle_chip_${muscle.name}")
          .clickable { onToggle(muscle) }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = NothingOledBlack,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Text(
            text = muscle.displayName.uppercase(),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = 0.5.sp,
            color = if (isSelected) NothingOledBlack else NothingTextPrimary
          )
        }
      }
    }
  }
}
