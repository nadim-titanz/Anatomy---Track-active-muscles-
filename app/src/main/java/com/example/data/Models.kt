package com.example.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MuscleGroup(val displayName: String, val isFront: Boolean) {
  NECK("Neck", true),
  CHEST("Chest", true),
  FRONT_DELTS("Front Shoulders", true),
  SIDE_DELTS("Side Shoulders", true),
  REAR_DELTS("Rear Shoulders", false),
  BICEPS("Biceps", true),
  TRICEPS("Triceps", false),
  FOREARMS("Forearms", true),
  LATS("Lats", false),
  UPPER_BACK("Upper Back", false),
  LOWER_BACK("Lower Back", false),
  ABS("Abs", true),
  OBLIQUES("Obliques", true),
  GLUTES("Glutes", false),
  QUADS("Quadriceps", true),
  HAMSTRINGS("Hamstrings", false),
  CALVES("Calves", true);

  companion object {
    fun fromString(name: String): MuscleGroup? {
      val trimmed = name.trim().uppercase()
      return when (trimmed) {
        "NECK" -> NECK
        "CHEST" -> CHEST
        "FRONT_DELTS", "FRONT DELTS", "FRONT SHOULDERS" -> FRONT_DELTS
        "SIDE_DELTS", "SIDE DELTS", "SIDE SHOULDERS", "DELTS" -> SIDE_DELTS
        "REAR_DELTS", "REAR DELTS", "REAR SHOULDERS" -> REAR_DELTS
        "BICEPS" -> BICEPS
        "TRICEPS" -> TRICEPS
        "FOREARMS" -> FOREARMS
        "LATS" -> LATS
        "UPPER_BACK", "UPPER BACK", "TRAPS", "BACK" -> UPPER_BACK
        "LOWER_BACK", "LOWER BACK" -> LOWER_BACK
        "ABS", "ABDOMINALS" -> ABS
        "OBLIQUES" -> OBLIQUES
        "GLUTES" -> GLUTES
        "QUADS", "QUADRICEPS" -> QUADS
        "HAMSTRINGS" -> HAMSTRINGS
        "CALVES", "CALVES_FRONT", "CALVES_BACK" -> CALVES
        else -> runCatching { valueOf(trimmed) }.getOrNull()
      }
    }
  }
}

enum class RecencyState(val label: String, val colorHex: Long, val emoji: String) {
  RECENT("Recent (1–3d)", 0xFFEF4444, "🔴"),               // 1-3 days ago = Red
  MORE_THAN_3_DAYS("> 3d ago", 0xFFEAB308, "🟡"),           // More than 3 days = Yellow
  GREY_RECOVERED("5–7d ago", 0xFF71717A, "⚪"),             // 5-7 days ago = Grey
  NEEDS_ATTENTION("8+ days / Untrained", 0xFF14171F, "⚫"); // 8+ days or untrained = Black

  val color: Color
    get() = Color(colorHex)
}

data class MuscleRecoveryInfo(
  val muscleGroup: MuscleGroup,
  val state: RecencyState,
  val daysAgo: Long?, // null if never trained
  val lastWorkoutName: String?,
  val lastTrainedTimestamp: Long?,
  val workoutHistory: List<WorkoutSession> = emptyList()
)

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val dayOfWeek: String,
  val timestamp: Long = System.currentTimeMillis(),
  val dateEpochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24),
  val durationMinutes: Int = 45,
  val totalVolumeLbs: Double = 0.0,
  val targetedMuscles: String = "", // Comma-separated MuscleGroup names
  val isCompleted: Boolean = true,
  val notes: String = ""
) {
  fun getMuscleList(): List<MuscleGroup> {
    if (targetedMuscles.isBlank()) return emptyList()
    return targetedMuscles.split(",")
      .mapNotNull { MuscleGroup.fromString(it.trim()) }
  }
}

@Entity(tableName = "exercise_entries")
data class ExerciseEntry(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: Long,
  val exerciseName: String,
  val muscleGroup: String, // MuscleGroup name
  val setNumber: Int,
  val reps: Int,
  val weightLbs: Double,
  val isCompleted: Boolean = true
)

@Entity(tableName = "body_metrics")
data class BodyMetric(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val dateEpochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24),
  val weightLbs: Double,
  val bodyFatPercent: Double? = null,
  val notes: String = ""
)
