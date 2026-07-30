package com.superChargedFitness.data.repository

import com.superChargedFitness.data.local.AppDatabase
import com.superChargedFitness.data.local.WorkoutDetails
import com.superChargedFitness.pojo.PWorkOutDetails
import androidx.sqlite.db.SimpleSQLiteQuery

/**
 * Repository that provides workout data from the Room database.
 * Centralises all data-access logic previously scattered across Activities.
 */
class WorkoutRepository(private val database: AppDatabase) {

    /** Whitelist of allowed table names to prevent SQL injection. */
    private val allowedTables = setOf(
        "tbl_full_body_workouts_list", "tbl_lower_body_list",
        "tbl_chest_beginner", "tbl_chest_intermediate", "tbl_chest_advanced",
        "tbl_abs_beginner", "tbl_abs_intermediate", "tbl_abs_advanced",
        "tbl_arm_beginner", "tbl_arm_intermediate", "tbl_arm_advanced",
        "tbl_shoulder_back_beginner", "tbl_shoulder_back_intermediate", "tbl_shoulder_back_advanced",
        "tbl_leg_beginner", "tbl_leg_intermediate", "tbl_leg_advanced"
    )

    /**
     * Fetches workout details from the given table, mapped to the legacy
     * [PWorkOutDetails] POJO for backward compatibility with adapters/intents.
     *
     * @throws IllegalArgumentException if [tableName] is not in the whitelist
     */
    suspend fun getWorkoutDetails(tableName: String): ArrayList<PWorkOutDetails> {
        require(allowedTables.contains(tableName)) { "Invalid table name: $tableName" }

        val query = SimpleSQLiteQuery("SELECT * FROM $tableName")
        val roomResults = database.workoutDetailsDao().getWorkoutsByTable(query)

        return ArrayList(roomResults.map { it.toPWorkOutDetails() })
    }

    /**
     * Looks up the YouTube video link for a workout by its title.
     *
     * @return The YouTube video ID/link, or empty string if not found.
     */
    suspend fun getVideoLink(workoutTitle: String): String {
        return database.youtubeLinkDao().getYouTubeLinkByTitle(workoutTitle) ?: ""
    }

    /** Maps Room entity → legacy POJO to avoid changing all adapters at once. */
    private fun WorkoutDetails.toPWorkOutDetails(): PWorkOutDetails {
        val p = PWorkOutDetails()
        p.workout_id = this.workoutId
        p.title = this.title
        p.videoLink = this.videoLink
        p.descriptions = this.descriptions
        p.time = this.time
        p.time_type = this.timeType
        p.image = this.image
        return p
    }
}
