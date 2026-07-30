package com.superChargedFitness.worker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.superChargedFitness.utils.ConstantString
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tracks the user's workout streak by checking SharedPreferences for the
 * last completed workout date. Runs daily alongside the reminder worker.
 *
 * Streak logic:
 * - If the last workout was yesterday → increment streak
 * - If the last workout was today → no change (already counted)
 * - If the last workout was >1 day ago → reset streak to 0
 */
class StreakTrackingWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_streak_tracking"
        const val PREF_STREAK_COUNT = "pref_streak_count"
        const val PREF_LAST_WORKOUT_DATE = "pref_last_workout_date"
        const val PREF_STREAK_UPDATED_DATE = "pref_streak_updated_date"
        private const val TAG = "StreakTrackingWorker"
    }

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val lastWorkoutDate = prefs.getString(PREF_LAST_WORKOUT_DATE, null)
        val lastStreakUpdate = prefs.getString(PREF_STREAK_UPDATED_DATE, null)

        // Don't double-count if we already ran today
        if (lastStreakUpdate == today) {
            Log.d(TAG, "Streak already updated today")
            return Result.success()
        }

        val currentStreak = prefs.getInt(PREF_STREAK_COUNT, 0)

        if (lastWorkoutDate != null) {
            val lastDate = dateFormat.parse(lastWorkoutDate)
            val todayDate = dateFormat.parse(today)

            if (lastDate != null && todayDate != null) {
                val diffMs = todayDate.time - lastDate.time
                val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()

                when {
                    diffDays == 0 -> {
                        // Workout was today — keep streak as is
                        Log.d(TAG, "Workout today, streak maintained: $currentStreak")
                    }
                    diffDays == 1 -> {
                        // Workout was yesterday — streak continues
                        val newStreak = currentStreak + 1
                        prefs.edit()
                            .putInt(PREF_STREAK_COUNT, newStreak)
                            .putString(PREF_STREAK_UPDATED_DATE, today)
                            .apply()
                        Log.d(TAG, "Streak incremented: $newStreak")
                    }
                    else -> {
                        // More than 1 day gap — reset streak
                        prefs.edit()
                            .putInt(PREF_STREAK_COUNT, 0)
                            .putString(PREF_STREAK_UPDATED_DATE, today)
                            .apply()
                        Log.d(TAG, "Streak reset (gap: $diffDays days)")
                    }
                }
            }
        } else {
            // No workout recorded yet — streak is 0
            prefs.edit()
                .putInt(PREF_STREAK_COUNT, 0)
                .putString(PREF_STREAK_UPDATED_DATE, today)
                .apply()
            Log.d(TAG, "No workout history, streak: 0")
        }

        return Result.success()
    }
}
