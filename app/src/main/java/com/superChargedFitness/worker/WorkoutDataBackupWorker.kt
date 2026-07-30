package com.superChargedFitness.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Backs up workout preferences (progress, streaks, sound settings, purchase status)
 * to a local JSON file in internal storage. Runs daily via PeriodicWorkRequest.
 *
 * The backup includes all SharedPreferences data so the user's workout progress
 * (last completed position, streak count, etc.) can survive a reinstall if the
 * backup file is preserved or synced.
 */
class WorkoutDataBackupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_workout_backup"
        private const val TAG = "WorkoutDataBackupWorker"
        private const val BACKUP_DIR = "workout_backup"
        private const val BACKUP_FILE = "workout_data_backup.json"
    }

    override suspend fun doWork(): Result {
        return try {
            val prefs = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val allEntries = prefs.all

            // Build a simple JSON representation of all preferences
            val jsonBuilder = StringBuilder()
            jsonBuilder.append("{\n")
            jsonBuilder.append("  \"backup_timestamp\": \"${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\",\n")
            jsonBuilder.append("  \"data\": {\n")

            val entries = allEntries.entries.toList()
            entries.forEachIndexed { index, entry ->
                val value = when (val v = entry.value) {
                    is String -> "\"${v.replace("\"", "\\\"")}\""
                    is Boolean -> v.toString()
                    is Int -> v.toString()
                    is Long -> v.toString()
                    is Float -> v.toString()
                    else -> "\"${v.toString()}\""
                }
                val comma = if (index < entries.size - 1) "," else ""
                jsonBuilder.append("    \"${entry.key}\": $value$comma\n")
            }

            jsonBuilder.append("  }\n")
            jsonBuilder.append("}")

            // Write to internal storage
            val backupDir = File(applicationContext.filesDir, BACKUP_DIR)
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val backupFile = File(backupDir, BACKUP_FILE)
            FileOutputStream(backupFile).use { fos ->
                fos.write(jsonBuilder.toString().toByteArray())
                fos.flush()
            }

            Log.d(TAG, "Workout data backed up to: ${backupFile.absolutePath} (${allEntries.size} entries)")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Backup failed", e)
            Result.retry()
        }
    }
}
