package com.superChargedFitness.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.superChargedFitness.utils.ConstantString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `initialization builds category list`() {
        // Arrange & Act
        val viewModel = HomeViewModel()

        // Assert
        val categories = viewModel.workoutCategories.value
        assertNotNull(categories)
        
        // We expect a specific number of categories based on the buildCategoryList implementation
        // 1 (main) + 2 (full/lower) + 4 (chest) + 4 (abs) + 4 (arm) + 4 (shoulder) + 4 (leg) = 23
        assertEquals(23, categories?.size)

        // Verify the first item is the 7x4 challenge header
        val firstItem = categories?.get(0)
        assertEquals(ConstantString.main, firstItem?.catDefficultyLevel)
        assertEquals("7 X 4 Challenge", firstItem?.catName)

        // Verify a specific workout category
        val chestBeginner = categories?.find { 
            it.catDefficultyLevel == ConstantString.biginner && it.catName == "Chest" 
        }
        assertNotNull(chestBeginner)
        assertEquals(ConstantString.tbl_chest_beginner, chestBeginner?.catTableName)
    }
}
