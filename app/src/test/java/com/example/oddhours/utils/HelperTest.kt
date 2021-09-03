package com.example.oddhours.utils

import org.junit.Assert
import org.junit.Test

class HelperTest {

    private val testHelper: Helper = Helper()

    @Test
    fun `calculate day of the year returns correct day for regular year`() {
        val actualDayOfYearFor08182018 = 230
        val monthOfYear = 7
        val dayOfMonth = 18
        val year = 2018

        val expectedDayOfYearFor08182018 = testHelper.calculateDayOfTheYear(monthOfYear, dayOfMonth, year)

        Assert.assertEquals(expectedDayOfYearFor08182018, actualDayOfYearFor08182018)
    }

    @Test
    fun `calculate day of the year returns correct day for leap year`() {
        val actualDayOfYearFor08182016 = 231
        val monthOfYear = 7
        val dayOfMonth = 18
        val year = 2016

        val expectedDayOfYearFor08182016 = testHelper.calculateDayOfTheYear(monthOfYear, dayOfMonth, year)

        Assert.assertEquals(expectedDayOfYearFor08182016, actualDayOfYearFor08182016)
    }
}