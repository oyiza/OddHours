package com.example.oddhours.utils

class Helper {

    fun calculateDayOfTheYear(monthOfYear: Int, dayOfMonth: Int): Int{
        val daysInMonth = intArrayOf(31,28,31,30,31,30,31,31,30,31,30,31)
        var dayOfYear = 0
        var counter = 0
        for(item in daysInMonth){
            if(counter <= (monthOfYear-1)){
                dayOfYear += item
            }
            else if (counter == monthOfYear){
                dayOfYear += dayOfMonth
            }
            counter++
        }
        return dayOfYear
    }
}