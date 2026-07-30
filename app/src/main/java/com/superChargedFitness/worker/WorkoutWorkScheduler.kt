package com.superChargedFitness.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Helper object to schedule all periodic WorkManager tasks.
 */
object WorkoutWorkScheduler {

    fun scheduleAllWork(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // 1. Daily Workout Reminder Notification
        val reminderRequest = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateDelayUntilNextMorning(), TimeUnit.MILLISECONDS)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            WorkoutReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )

        // 2. Daily Streak Tracking
        val streakRequest = PeriodicWorkRequestBuilder<StreakTrackingWorker>(1, TimeUnit.DAYS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            StreakTrackingWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            streakRequest
        )

        // 3. Daily Workout Data Backup
        val backupRequest = PeriodicWorkRequestBuilder<WorkoutDataBackupWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WorkoutDataBackupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )
    }

    private fun calculateDelayUntilNextMorning(): Long {
        val javaCalendar = java.util.Calendar.getInstance()
        val currentMillis = javaCalendar.timeInMillis
        
        javaCalendar.set(java.util.Calendar.HOUR_OF_DAY, 8)
        javaCalendar.set(java.util.Calendar.MINUTE, 0)
        javaCalendar.set(java.util.Calendar.SECOND, 0)

        var targetMillis = javaCalendar.timeInMillis
        if (targetMillis <= currentMillis) {
            // Target is in the past (e.g. it's already 10 AM), schedule for tomorrow 8 AM
            targetMillis += TimeUnit.DAYS.toMillis(1)
        }
        
        return targetMillis - currentMillis
    }
}
