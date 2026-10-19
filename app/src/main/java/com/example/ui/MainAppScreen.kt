package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.components.WorkoutEditorSheet
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.MusclesScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.*

enum class NavTab(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  OVERVIEW("OVERVIEW", Icons.Filled.Home, Icons.Outlined.Home),
  CALENDAR("CALENDAR", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
  MUSCLES("MUSCLES", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
  SETTINGS("SETTINGS", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MainAppScreen(
  viewModel: WorkoutViewModel = viewModel()
) {
  var currentTab by remember { mutableStateOf(NavTab.OVERVIEW) }
  val showEditor by viewModel.showWorkoutEditor.collectAsState()
  val editingSession by viewModel.editingSession.collectAsState()

  Scaffold(
    containerColor = NothingOledBlack,
    floatingActionButton = {
      if (currentTab == NavTab.OVERVIEW) {
        Surface(
          onClick = { viewModel.openAddWorkout() },
          shape = RoundedCornerShape(999.dp),
          color = NothingTextDisplay,
          border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
          modifier = Modifier
            .padding(bottom = 4.dp)
            .testTag("main_add_workout_fab")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Workout",
              tint = NothingOledBlack,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "LOG WORKOUT",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = NothingOledBlack
            )
          }
        }
      }
    },
    bottomBar = {
      NothingBottomBar(
        currentTab = currentTab,
        onTabSelected = { currentTab = it }
      )
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(NothingOledBlack)
        .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
      when (currentTab) {
        NavTab.OVERVIEW -> OverviewScreen(
          viewModel = viewModel,
          onOpenAddWorkout = { viewModel.openAddWorkout() }
        )
        NavTab.CALENDAR -> CalendarScreen(
          viewModel = viewModel
        )
        NavTab.MUSCLES -> MusclesScreen(
          viewModel = viewModel
        )
        NavTab.SETTINGS -> SettingsScreen(
          viewModel = viewModel
        )
      }
    }
  }

  // Active Workout Add/Edit Bottom Sheet
  if (showEditor) {
    WorkoutEditorSheet(
      session = editingSession,
      onDismiss = { viewModel.closeWorkoutEditor() },
      onSave = { id, name, timestamp, muscles, notes ->
        viewModel.saveOrUpdateWorkout(
          id = id,
          name = name,
          timestamp = timestamp,
          targetedMuscles = muscles,
          notes = notes
        )
      },
      onDelete = { id ->
        viewModel.deleteWorkout(id)
      }
    )
  }
}

/**
 * 📱 Nothing OS Floating Navigation Bar:
 * - Solves radius issue with sleek RoundedCornerShape(24.dp)
 * - Solves padding issue with edge clearance, navigationBarsPadding, and generous touch targets
 * - Signature Nothing Red LED active indicator dot
 */
@Composable
fun NothingBottomBar(
  currentTab: NavTab,
  onTabSelected: (NavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = NothingSurface,
      border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("main_navigation_bar")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        NavTab.values().forEach { tab ->
          val isSelected = currentTab == tab
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .clickable { onTabSelected(tab) }
              .padding(vertical = 6.dp)
              .testTag("nav_tab_${tab.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = tab.title,
                tint = if (isSelected) NothingTextDisplay else NothingTextDisabled,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = tab.title,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.8.sp,
                color = if (isSelected) NothingTextDisplay else NothingTextDisabled
              )
              Spacer(modifier = Modifier.height(3.dp))
              // Nothing Red dot indicator (constant height so no jumping)
              Box(
                modifier = Modifier
                  .size(3.5.dp)
                  .background(
                    if (isSelected) NothingRed else Color.Transparent,
                    CircleShape
                  )
              )
            }
          }
        }
      }
    }
  }
}
