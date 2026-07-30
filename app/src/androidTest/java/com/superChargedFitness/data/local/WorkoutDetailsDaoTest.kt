package com.superChargedFitness.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.superChargedFitness.utils.ConstantString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WorkoutDetailsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var workoutDao: WorkoutDetailsDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // We use the actual pre-populated database for this test since
        // the app relies entirely on it.
        database = AppDatabase.getDatabase(context)
        workoutDao = database.workoutDetailsDao()
    }

    @After
    fun teardown() {
        // AppDatabase is a singleton used by the app, we shouldn't close it 
        // aggressively in tests that share context, but if we made a new instance 
        // we would close it. Since we called getDatabase(), it's the singleton.
    }

    @Test
    fun getWorkoutsByTable_returnsDataFromExistingTable() = runTest {
        // Arrange: Query the beginner chest table which should exist in the pre-populated DB
        val tableName = ConstantString.tbl_chest_beginner
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName")

        // Act
        val results = workoutDao.getWorkoutsByTable(query)

        // Assert
        assertTrue("Results should not be empty for an existing table", results.isNotEmpty())
        
        // Check mapping of a known entity
        val firstWorkout = results[0]
        assertTrue("Workout should have an ID", firstWorkout.workoutId > 0)
        assertTrue("Workout should have a title", firstWorkout.title.isNotEmpty())
    }
}
