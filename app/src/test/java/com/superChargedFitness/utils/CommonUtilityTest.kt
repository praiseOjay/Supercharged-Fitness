package com.superChargedFitness.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CommonUtilityTest {

    @Test
    fun testSecToTime_positiveValue() {
        // Arrange
        val timeInSeconds = 125 // 2 minutes and 5 seconds

        // Act
        val formattedTime = CommonUtility.secToTime(timeInSeconds)

        // Assert
        assertEquals("02:05", formattedTime)
    }

    @Test
    fun testSecToTime_zeroValue() {
        val formattedTime = CommonUtility.secToTime(0)
        assertEquals("00:00", formattedTime)
    }

    @Test
    fun testSecToTime_negativeValue() {
        val formattedTime = CommonUtility.secToTime(-10)
        assertEquals("00:00", formattedTime)
    }

    @Test
    fun testUnitFormat_singleDigit() {
        assertEquals("05", CommonUtility.unitFormat(5))
        assertEquals("00", CommonUtility.unitFormat(0))
    }

    @Test
    fun testUnitFormat_doubleDigit() {
        assertEquals("15", CommonUtility.unitFormat(15))
        assertEquals("99", CommonUtility.unitFormat(99))
    }

    @Test
    fun testUnitFormat_negativeDigit() {
        assertEquals("-5", CommonUtility.unitFormat(-5))
    }
}
