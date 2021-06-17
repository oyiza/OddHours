package com.example.oddhours.database

import android.content.ContentValues
import com.example.oddhours.data.model.ShiftsModel

class TableShifts {
    private val db = DatabaseHelper.database
    private val getShifts = "SELECT * FROM ${DatabaseHelper.shiftsTable}"

    fun insertShift(newShift: ShiftsModel): Long {
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.shift_Start_Date_COL_2, newShift.shiftStartDate)
        contentValues.put(DatabaseHelper.shift_End_Date_COL_3, newShift.shiftEndDate)
        contentValues.put(DatabaseHelper.job_ID_COL_4, newShift.jobID)
        contentValues.put(DatabaseHelper.start_Time_COL_5, newShift.startTime)
        contentValues.put(DatabaseHelper.end_Time_COL_6, newShift.endTime)
        contentValues.put(DatabaseHelper.hours_Worked_COL_7, newShift.hoursWorked)

        return db!!.insert(DatabaseHelper.shiftsTable, null, contentValues)
    }

    fun getShifts(): List<ShiftsModel> {
        val res = db!!.rawQuery(getShifts, null)
        val listOfShifts: MutableList<ShiftsModel> = mutableListOf()
        if (res.count != 0) {
            while (res.moveToNext()) {
                val shiftsModel = ShiftsModel(res.getInt(0), res.getString(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5), res.getString(6))
                listOfShifts.add(shiftsModel)
            }
        }
        return listOfShifts
    }

    fun getShiftsForJobID(jobId: Int): MutableList<ShiftsModel> {
        val res = db!!.rawQuery(
                "SELECT * FROM ${DatabaseHelper.shiftsTable} WHERE ${DatabaseHelper.job_ID_COL_4} = $jobId", null)
        val listOfShifts: MutableList<ShiftsModel> = mutableListOf<ShiftsModel>()
        if (res.count != 0) {
            while (res.moveToNext()) {
                val shiftsModel = ShiftsModel(res.getInt(0), res.getString(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5), res.getString(6))
                listOfShifts.add(shiftsModel)
            }
            return listOfShifts
        }
        return listOfShifts
    }

    fun deleteShiftsForJob(jobId: Int): Boolean {
        val whereClause = "${DatabaseHelper.job_ID_COL_4} = $jobId"
        val res = db!!.delete(DatabaseHelper.shiftsTable, whereClause, null)
        return res > 0
    }
}