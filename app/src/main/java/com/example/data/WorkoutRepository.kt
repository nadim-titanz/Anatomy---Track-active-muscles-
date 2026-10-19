package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutRepository(private val workoutDao: WorkoutDao) {

  val allSessions: Flow<List<WorkoutSession>> = workoutDao.getAllSessions()
  val completedSessions: Flow<List<WorkoutSession>> = workoutDao.getCompletedSessions()
  val allBodyMetrics: Flow<List<BodyMetric>> = workoutDao.getAllBodyMetrics()
  val latestBodyMetric: Flow<BodyMetric?> = workoutDao.getLatestBodyMetric()

  fun getSessionsForDay(epochDay: Long): Flow<List<WorkoutSession>> {
    return workoutDao.getSessionsForDay(epochDay)
  }

  fun getExercisesForSession(sessionId: Long): Flow<List<ExerciseEntry>> {
    return workoutDao.getExercisesForSession(sessionId)
  }

  suspend fun getExercisesForSessionSync(sessionId: Long): List<ExerciseEntry> {
    return workoutDao.getExercisesForSessionSync(sessionId)
  }

  fun getExercisesByName(name: String): Flow<List<ExerciseEntry>> {
    return workoutDao.getExercisesByName(name)
  }

  suspend fun insertSession(session: WorkoutSession): Long {
    return workoutDao.insertSession(session)
  }

  suspend fun insertSessionWithExercises(
    session: WorkoutSession,
    exercises: List<ExerciseEntry>
  ): Long {
    val sessionId = workoutDao.insertSession(session)
    if (exercises.isNotEmpty()) {
      val entriesWithId = exercises.map { it.copy(sessionId = sessionId) }
      workoutDao.insertExercises(entriesWithId)
    }
    return sessionId
  }

  suspend fun updateSession(session: WorkoutSession) {
    workoutDao.updateSession(session)
  }

  suspend fun deleteSession(sessionId: Long) {
    workoutDao.deleteExercisesForSession(sessionId)
    workoutDao.deleteSession(sessionId)
  }

  suspend fun insertBodyMetric(metric: BodyMetric): Long {
    return workoutDao.insertBodyMetric(metric)
  }

  suspend fun deleteBodyMetric(id: Long) {
    workoutDao.deleteBodyMetric(id)
  }

  /**
   * Evaluates recovery state and full training history for all 16 muscle groups
   * based on completed workouts.
   */
  fun getMuscleRecoveryMap(): Flow<Map<MuscleGroup, MuscleRecoveryInfo>> {
    return workoutDao.getCompletedSessions().map { sessions ->
      val now = System.currentTimeMillis()
      val oneDayMillis = 24L * 60 * 60 * 1000

      val result = mutableMapOf<MuscleGroup, MuscleRecoveryInfo>()

      for (muscle in MuscleGroup.values()) {
        // Find all sessions targeting this muscle, sorted newest first
        val matchingSessions = sessions.filter { session ->
          session.getMuscleList().contains(muscle)
        }.sortedByDescending { it.timestamp }

        val lastSession = matchingSessions.firstOrNull()

        if (lastSession == null) {
          result[muscle] = MuscleRecoveryInfo(
            muscleGroup = muscle,
            state = RecencyState.NEEDS_ATTENTION,
            daysAgo = null,
            lastWorkoutName = null,
            lastTrainedTimestamp = null,
            workoutHistory = emptyList()
          )
        } else {
          val diffDays = (now - lastSession.timestamp).coerceAtLeast(0) / oneDayMillis
          val state = when {
            diffDays <= 3 -> RecencyState.RECENT             // 1-3 days ago (or today) = Red
            diffDays == 4L -> RecencyState.MORE_THAN_3_DAYS   // More than 3 days = Yellow
            diffDays in 5..7 -> RecencyState.GREY_RECOVERED  // 5-7 days ago = Grey
            else -> RecencyState.NEEDS_ATTENTION             // 8+ days = Black
          }

          result[muscle] = MuscleRecoveryInfo(
            muscleGroup = muscle,
            state = state,
            daysAgo = diffDays,
            lastWorkoutName = lastSession.name,
            lastTrainedTimestamp = lastSession.timestamp,
            workoutHistory = matchingSessions
          )
        }
      }
      result
    }
  }
}
