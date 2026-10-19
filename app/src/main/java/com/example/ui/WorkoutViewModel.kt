package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application, viewModelScope)
  private val repository = WorkoutRepository(database.workoutDao())

  val allSessions: StateFlow<List<WorkoutSession>> = repository.allSessions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val completedSessions: StateFlow<List<WorkoutSession>> = repository.completedSessions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val latestBodyMetric: StateFlow<BodyMetric?> = repository.latestBodyMetric
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val allBodyMetrics: StateFlow<List<BodyMetric>> = repository.allBodyMetrics
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recoveryMap: StateFlow<Map<MuscleGroup, MuscleRecoveryInfo>> = repository.getMuscleRecoveryMap()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

  // Recently trained muscles (1-3 days ago = Red)
  val recentlyTrainedMuscles: StateFlow<List<MuscleRecoveryInfo>> = recoveryMap.map { map ->
    map.values.filter { it.state == RecencyState.RECENT }
      .sortedBy { it.daysAgo ?: 999 }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Neglected muscles ("Needs Attention" - 8+ days or never)
  val neglectedMuscles: StateFlow<List<MuscleRecoveryInfo>> = recoveryMap.map { map ->
    map.values.filter { it.state == RecencyState.NEEDS_ATTENTION }
      .sortedByDescending { it.daysAgo ?: 999 }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Trained > 3 days ago (Yellow)
  val fewDaysAgoMuscles: StateFlow<List<MuscleRecoveryInfo>> = recoveryMap.map { map ->
    map.values.filter { it.state == RecencyState.MORE_THAN_3_DAYS }
      .sortedBy { it.daysAgo ?: 999 }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Complete muscle coverage sorted by days since trained
  val allMusclesCoverage: StateFlow<List<MuscleRecoveryInfo>> = recoveryMap.map { map ->
    map.values.sortedWith(
      compareBy<MuscleRecoveryInfo> {
        when (it.state) {
          RecencyState.NEEDS_ATTENTION -> 3
          RecencyState.GREY_RECOVERED -> 2
          RecencyState.MORE_THAN_3_DAYS -> 1
          RecencyState.RECENT -> 0
        }
      }.thenByDescending { it.daysAgo ?: 999 }
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allExercises: StateFlow<List<ExerciseEntry>> = database.workoutDao().getAllExercises()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected date for calendar view (default to today)
  val todayEpochDay = System.currentTimeMillis() / (24L * 60 * 60 * 1000)
  private val _selectedDateEpochDay = MutableStateFlow(todayEpochDay)
  val selectedDateEpochDay: StateFlow<Long> = _selectedDateEpochDay.asStateFlow()

  fun selectDate(epochDay: Long) {
    _selectedDateEpochDay.value = epochDay
  }

  // Selected muscle for detailed history inspection
  private val _selectedMuscle = MutableStateFlow<MuscleGroup?>(null)
  val selectedMuscle: StateFlow<MuscleGroup?> = _selectedMuscle.asStateFlow()

  fun selectMuscle(muscle: MuscleGroup?) {
    _selectedMuscle.value = muscle
  }

  // Quick Workout Add / Edit Sheet state
  private val _editingSession = MutableStateFlow<WorkoutSession?>(null)
  val editingSession: StateFlow<WorkoutSession?> = _editingSession.asStateFlow()

  private val _showWorkoutEditor = MutableStateFlow(false)
  val showWorkoutEditor: StateFlow<Boolean> = _showWorkoutEditor.asStateFlow()

  fun openAddWorkout(initialEpochDay: Long = todayEpochDay) {
    val initialTime = initialEpochDay * 24L * 60 * 60 * 1000 + (12 * 60 * 60 * 1000)
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(initialTime))
    _editingSession.value = WorkoutSession(
      id = 0,
      name = "",
      dayOfWeek = dayName,
      timestamp = initialTime,
      dateEpochDay = initialEpochDay,
      durationMinutes = 45,
      totalVolumeLbs = 0.0,
      targetedMuscles = "",
      isCompleted = true,
      notes = ""
    )
    _showWorkoutEditor.value = true
  }

  fun openEditWorkout(session: WorkoutSession) {
    _editingSession.value = session
    _showWorkoutEditor.value = true
  }

  fun closeWorkoutEditor() {
    _showWorkoutEditor.value = false
    _editingSession.value = null
  }

  fun saveOrUpdateWorkout(
    id: Long,
    name: String,
    timestamp: Long,
    targetedMuscles: List<MuscleGroup>,
    notes: String,
    durationMinutes: Int = 45
  ) {
    viewModelScope.launch {
      val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))
      val epochDay = timestamp / (24L * 60 * 60 * 1000)
      val muscleString = targetedMuscles.joinToString(",") { it.name }
      val cleanName = name.trim().ifBlank {
        if (targetedMuscles.isNotEmpty()) {
          targetedMuscles.take(2).joinToString(" + ") { it.displayName }
        } else {
          "Workout"
        }
      }

      val session = WorkoutSession(
        id = id,
        name = cleanName,
        dayOfWeek = dayName,
        timestamp = timestamp,
        dateEpochDay = epochDay,
        durationMinutes = durationMinutes,
        totalVolumeLbs = 0.0,
        targetedMuscles = muscleString,
        isCompleted = true,
        notes = notes
      )

      if (id == 0L) {
        repository.insertSession(session)
      } else {
        repository.updateSession(session)
      }
      closeWorkoutEditor()
    }
  }

  fun deleteWorkout(sessionId: Long) {
    viewModelScope.launch {
      repository.deleteSession(sessionId)
      if (_editingSession.value?.id == sessionId) {
        closeWorkoutEditor()
      }
    }
  }

  fun logBodyMetric(weightLbs: Double, bodyFat: Double? = null, notes: String = "") {
    viewModelScope.launch {
      val now = System.currentTimeMillis()
      repository.insertBodyMetric(
        BodyMetric(
          timestamp = now,
          dateEpochDay = now / (24L * 60 * 60 * 1000),
          weightLbs = weightLbs,
          bodyFatPercent = bodyFat,
          notes = notes
        )
      )
    }
  }

  fun getExercisesForSession(sessionId: Long): Flow<List<ExerciseEntry>> {
    return repository.getExercisesForSession(sessionId)
  }

  fun clearAllData() {
    viewModelScope.launch {
      database.workoutDao().deleteAllSessions()
      database.workoutDao().deleteAllExercises()
      database.workoutDao().deleteAllBodyMetrics()
      _selectedMuscle.value = null
    }
  }

  fun resetToSampleData() {
    viewModelScope.launch {
      val now = System.currentTimeMillis()
      val oneDayMillis = 24L * 60 * 60 * 1000
      val today = now / oneDayMillis

      // Clear existing
      val existing = database.workoutDao().getAllSessions().first()
      for (s in existing) {
        database.workoutDao().deleteSession(s.id)
      }

      // Add sample workouts matching prompt:
      // 1. Pull Day (Yesterday / 1 day ago) - Back, Lats, Biceps, Forearms, Rear Delts
      database.workoutDao().insertSession(
        WorkoutSession(
          name = "Pull Day",
          dayOfWeek = "Yesterday",
          timestamp = now - (1L * oneDayMillis),
          dateEpochDay = today - 1,
          durationMinutes = 55,
          targetedMuscles = "LATS,UPPER_BACK,BICEPS,FOREARMS,REAR_DELTS",
          isCompleted = true,
          notes = "Felt great on pullups and dumbbell rows."
        )
      )

      // 2. Push Day (2 days ago) - Chest, Front Delts, Side Delts, Triceps
      database.workoutDao().insertSession(
        WorkoutSession(
          name = "Push Day",
          dayOfWeek = "2 days ago",
          timestamp = now - (2L * oneDayMillis),
          dateEpochDay = today - 2,
          durationMinutes = 50,
          targetedMuscles = "CHEST,FRONT_DELTS,SIDE_DELTS,TRICEPS",
          isCompleted = true,
          notes = "Solid chest and shoulder session."
        )
      )

      // 3. Leg Day (4 days ago) - Quads, Abs
      database.workoutDao().insertSession(
        WorkoutSession(
          name = "Leg Day",
          dayOfWeek = "4 days ago",
          timestamp = now - (4L * oneDayMillis),
          dateEpochDay = today - 4,
          durationMinutes = 45,
          targetedMuscles = "QUADS,ABS",
          isCompleted = true,
          notes = "Squats and core training."
        )
      )

      // 4. Arms (7 days ago) - Biceps, Triceps
      database.workoutDao().insertSession(
        WorkoutSession(
          name = "Arms",
          dayOfWeek = "7 days ago",
          timestamp = now - (7L * oneDayMillis),
          dateEpochDay = today - 7,
          durationMinutes = 40,
          targetedMuscles = "BICEPS,TRICEPS",
          isCompleted = true,
          notes = "Arm hypertrophy focus."
        )
      )
    }
  }

  fun shareWorkoutSummary(context: Context, session: WorkoutSession) {
    val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(session.timestamp))
    val muscles = session.getMuscleList().joinToString(", ") { it.displayName }

    val shareText = """
🏋️ Anatome Workout Log
━━━━━━━━━━━━━━━━━━━━━━
Workout: ${session.name}
Date: $dateStr
Targeted Muscles: $muscles

${if (session.notes.isNotBlank()) "Notes: " + session.notes else ""}
Logged with Anatome — Visual Workout & Muscle Tracker.
""".trimIndent()

    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, shareText)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Workout Summary")
    context.startActivity(shareIntent)
  }
}
