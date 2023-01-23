package com.example.oddhours.data.model

data class ShiftsModel(val shiftID: Int, val shiftStartDate: String, val shiftDayOfYear: Int, val shiftEndDate: String, val jobID: Int, var startTime: String, var endTime: String, var hoursWorked: String)