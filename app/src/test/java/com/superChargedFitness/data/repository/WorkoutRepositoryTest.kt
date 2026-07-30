package com.superChargedFitness.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.superChargedFitness.data.local.AppDatabase
import com.superChargedFitness.data.local.WorkoutDetails
import com.superChargedFitness.data.local.WorkoutDetailsDao
import com.superChargedFitness.data.local.YouTubeLinkDao
import com.superChargedFitness.utils.ConstantString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class WorkoutRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockWorkoutDao: WorkoutDetailsDao
    private lateinit var mockYouTubeDao: YouTubeLinkDao
    private lateinit var repository: WorkoutRepository

    @Before
    fun setup() {
        mockDatabase = mock()
        mockWorkoutDao = mock()
        mockYouTubeDao = mock()
        
        whenever(mockDatabase.workoutDetailsDao()).thenReturn(mockWorkoutDao)
        whenever(mockDatabase.youtubeLinkDao()).thenReturn(mockYouTubeDao)
        
        repository = WorkoutRepository(mockDatabase)
    }

    @Test
    fun `getWorkoutDetails returns mapped list for valid table name`() = runTest {
        // Arrange
        val tableName = ConstantString.tbl_chest_beginner
        val mockEntity = WorkoutDetails(
            workoutId = 1,
            title = "Pushups",
            videoLink = "abcd",
            descriptions = "Do pushups",
            time = "30",
            timeType = "time",
            image = "pushup_img"
        )
        whenever(mockWorkoutDao.getWorkoutsByTable(any())).thenReturn(listOf(mockEntity))

        // Act
        val result = repository.getWorkoutDetails(tableName)

        // Assert
        assertEquals(1, result.size)
        assertEquals(1, result[0].workout_id)
        assertEquals("Pushups", result[0].title)
        
        // Verify query was constructed correctly
        verify(mockWorkoutDao).getWorkoutsByTable(argThat {
            (this as SimpleSQLiteQuery).sql == "SELECT * FROM $tableName"
        })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getWorkoutDetails throws exception for invalid table name`() = runTest {
        // Act
        repository.getWorkoutDetails("invalid_table_name")
        // Exception should be thrown
    }

    @Test
    fun `getVideoLink returns link when found`() = runTest {
        // Arrange
        val title = "Jumping Jacks"
        val expectedLink = "xyz123"
        whenever(mockYouTubeDao.getYouTubeLinkByTitle(title)).thenReturn(expectedLink)

        // Act
        val result = repository.getVideoLink(title)

        // Assert
        assertEquals(expectedLink, result)
        verify(mockYouTubeDao).getYouTubeLinkByTitle(title)
    }

    @Test
    fun `getVideoLink returns empty string when not found`() = runTest {
        // Arrange
        val title = "Unknown Workout"
        whenever(mockYouTubeDao.getYouTubeLinkByTitle(title)).thenReturn(null)

        // Act
        val result = repository.getVideoLink(title)

        // Assert
        assertEquals("", result)
    }
}
