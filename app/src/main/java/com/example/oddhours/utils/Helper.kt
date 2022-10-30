package com.example.oddhours.utils

import android.util.Log
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsModel

class Helper {

    fun calculateDayOfTheYear(monthOfYear: Int, dayOfMonth: Int, year: Int): Int {

        val daysInMonth: IntArray = if (ifLeapYear(year)){
            intArrayOf(31,29,31,30,31,30,31,31,30,31,30,31)
        } else {
            intArrayOf(31,28,31,30,31,30,31,31,30,31,30,31)
        }

        var dayOfYear = 0
        for ((counter, item) in daysInMonth.withIndex()) {
            if(counter <= (monthOfYear-1)) {
                dayOfYear += item
            }
            else if (counter == monthOfYear) {
                dayOfYear += dayOfMonth
            }
        }
        return dayOfYear
    }

    private fun ifLeapYear(year: Int): Boolean {
        return ((year % 400) == 0) || (((year % 4) == 0) && ((year % 100) != 0))
    }

    fun removeJobItemFromUI(list: List<JobModel>, position: Int): List<JobModel> {
        Log.i(homeTag, "removeItem() called: position is $position")
        val result = list.toMutableList()
        result.removeAt(position)
        return result.toList()
    }

    fun removeShiftItemFromUI(list: List<ShiftsModel>, position: Int): List<ShiftsModel> {
        Log.i(childTag, "removeItem() called: position is $position")
        val result = list.toMutableList()
        result.removeAt(position)
        return result.toList()
    }

    companion object {
        private const val homeTag = "HomeAdapter"
        private const val childTag = "ChildAdapter"

    }
}
