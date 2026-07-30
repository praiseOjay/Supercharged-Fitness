
package com.superChargedFitness.data.local

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * DAO for querying workout details across the app's ~17 workout tables.
 * Uses @RawQuery because the table name is dynamic (determined at runtime
 * based on which workout category the user selects).
 */
@Dao
interface WorkoutDetailsDao {

    /**
     * Executes a raw query to fetch workout details from a specific table.
     * The caller constructs the query with the appropriate table name.
     *
     * Example usage via WorkoutRepository:
     *   SimpleSQLiteQuery("SELECT * FROM tbl_chest_beginner")
     */
    @RawQuery
    suspend fun getWorkoutsByTable(query: SupportSQLiteQuery): List<WorkoutDetails>
}
