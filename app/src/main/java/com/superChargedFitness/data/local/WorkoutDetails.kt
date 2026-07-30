
package com.superChargedFitness.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Room entity that maps to the columns in each workout table.
 * Used with @RawQuery since workouts are spread across ~17 tables
 * with identical schemas (e.g. tbl_chest_beginner, tbl_abs_advanced).
 */
@Entity
data class WorkoutDetails(
    @PrimaryKey
    @ColumnInfo(name = "Workout_id")
    val workoutId: Int,

    @ColumnInfo(name = "Title")
    val title: String,

    @ColumnInfo(name = "videoLink")
    val videoLink: String,

    @ColumnInfo(name = "Description")
    val descriptions: String,

    @ColumnInfo(name = "Time")
    val time: String,

    @ColumnInfo(name = "time_type")
    val timeType: String,

    @ColumnInfo(name = "Image")
    val image: String
) : Serializable
