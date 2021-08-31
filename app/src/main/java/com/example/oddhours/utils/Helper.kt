package com.example.oddhours.utils

class Helper {

    fun calculateDayOfTheYear(monthOfYear: Int, dayOfMonth: Int, year: Int): Int {

        var daysInMonth: IntArray = if (ifLeapYear(year)){
            intArrayOf(31,29,31,30,31,30,31,31,30,31,30,31)
        } else {
            intArrayOf(31,28,31,30,31,30,31,31,30,31,30,31)
        }

        var dayOfYear = 0
        var counter = 0
        for (item in daysInMonth) {
            if(counter <= (monthOfYear-1)) {
                dayOfYear += item
            }
            else if (counter == monthOfYear) {
                dayOfYear += dayOfMonth
            }
            counter++
        }
        return dayOfYear
    }

    private fun ifLeapYear(year: Int): Boolean {
        return ((year % 400) == 0) || (((year % 4) == 0) && ((year % 100) != 0))
    }
}