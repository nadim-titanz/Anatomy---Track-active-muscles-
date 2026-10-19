package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

  @Query("SELECT * FROM workout_sessions ORDER BY timestamp DESC")
  fun getAllSessions(): Flow<List<WorkoutSession>>

  @Query("SELECT * FROM workout_sessions WHERE isCompleted = 1 ORDER BY timestamp DESC")
  fun getCompletedSessions(): Flow<List<WorkoutSession>>

  @Query("SELECT * FROM workout_sessions WHERE dateEpochDay = :epochDay ORDER BY timestamp DESC")
  fun getSessionsForDay(epochDay: Long): Flow<List<WorkoutSession>>

  @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
  suspend fun getSessionById(sessionId: Long): WorkoutSession?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: WorkoutSession): Long

  @Update
  suspend fun updateSession(session: WorkoutSession)

  @Query("DELETE FROM workout_sessions WHERE id = :id")
  suspend fun deleteSession(id: Long)

  @Query("DELETE FROM workout_sessions")
  suspend fun deleteAllSessions()

  // Exercise Entries
  @Query("SELECT * FROM exercise_entries WHERE sessionId = :sessionId ORDER BY setNumber ASC")
  fun getExercisesForSession(sessionId: Long): Flow<List<ExerciseEntry>>

  @Query("SELECT * FROM exercise_entries WHERE sessionId = :sessionId ORDER BY setNumber ASC")
  suspend fun getExercisesForSessionSync(sessionId: Long): List<ExerciseEntry>

  @Query("SELECT * FROM exercise_entries ORDER BY id DESC")
  fun getAllExercises(): Flow<List<ExerciseEntry>>

  @Query("SELECT * FROM exercise_entries WHERE exerciseName = :name ORDER BY id ASC")
  fun getExercisesByName(name: String): Flow<List<ExerciseEntry>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExercise(entry: ExerciseEntry): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExercises(entries: List<ExerciseEntry>)

  @Query("DELETE FROM exercise_entries WHERE id = :id")
  suspend fun deleteExercise(id: Long)

  @Query("DELETE FROM exercise_entries WHERE sessionId = :sessionId")
  suspend fun deleteExercisesForSession(sessionId: Long)

  @Query("DELETE FROM exercise_entries")
  suspend fun deleteAllExercises()

  // Body Metrics
  @Query("SELECT * FROM body_metrics ORDER BY timestamp DESC")
  fun getAllBodyMetrics(): Flow<List<BodyMetric>>

  @Query("SELECT * FROM body_metrics ORDER BY timestamp DESC LIMIT 1")
  fun getLatestBodyMetric(): Flow<BodyMetric?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBodyMetric(metric: BodyMetric): Long

  @Query("DELETE FROM body_metrics WHERE id = :id")
  suspend fun deleteBodyMetric(id: Long)

  @Query("DELETE FROM body_metrics")
  suspend fun deleteAllBodyMetrics()
}
