package com.example.oddhours.data.model

import java.util.*

data class ShiftsModel(val shiftID: Int, val shiftDate: Date, val jobID: Int, var startTime: String, var endTime: String, var hoursWorked: String)