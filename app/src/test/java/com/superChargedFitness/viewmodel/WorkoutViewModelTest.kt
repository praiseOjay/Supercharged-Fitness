package com.superChargedFitness.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class WorkoutViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WorkoutViewModel
    private val mockApp: Application = mock(Application::class.java)

    @Before
    fun setup() {
        viewModel = WorkoutViewModel(mockApp)
    }

    @Test
    fun `timer increments and formats correctly when not paused`() {
        assertEquals("00:00", viewModel.getTimerText())
        
        viewModel.incrementTimer()
        assertEquals("00:01", viewModel.getTimerText())
        assertEquals("00:01", viewModel.timerText.value)
        
        viewModel.incrementTimer()
        assertEquals("00:02", viewModel.getTimerText())
    }

    @Test
    fun `timer does not increment when paused`() {
        viewModel.pauseTimer()
        assertTrue(viewModel.isTimerPaused.value == true)
        
        viewModel.incrementTimer()
        assertEquals("00:00", viewModel.getTimerText())
    }

    @Test
    fun `timer resumes correctly`() {
        viewModel.pauseTimer()
        viewModel.resumeTimer()
        
        assertFalse(viewModel.isTimerPaused.value == true)
        
        viewModel.incrementTimer()
        assertEquals("00:01", viewModel.getTimerText())
    }

    @Test
    fun `sound toggles correctly`() {
        // Initial state is true
        assertTrue(viewModel.isSoundOn.value == true)
        
        // Toggle to false
        val result1 = viewModel.toggleSound()
        assertFalse(result1)
        assertFalse(viewModel.isSoundOn.value == true)
        
        // Toggle back to true
        val result2 = viewModel.toggleSound()
        assertTrue(result2)
        assertTrue(viewModel.isSoundOn.value == true)
    }
}
