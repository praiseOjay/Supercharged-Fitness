
package com.superChargedFitness.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database backed by the pre-populated HomeWorkout.db asset.
 *
 * The asset database contains ~17 workout tables (e.g. tbl_chest_beginner)
 * with identical schemas, plus tbl_youtube_link. Room's createFromAsset()
 * handles the initial copy automatically.
 *
 * WorkoutDetails entity is only used for @RawQuery mapping — it does NOT
 * create a new table since we query existing tables dynamically.
 */
@Database(
    entities = [WorkoutDetails::class, YouTubeLink::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDetailsDao(): WorkoutDetailsDao
    abstract fun youtubeLinkDao(): YouTubeLinkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "HomeWorkout.db"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .createFromAsset(DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
