package com.example.oddhours.database

import android.content.ContentValues
import com.example.oddhours.data.model.ShiftsModel

class TableShifts {
    private val db = DatabaseHelper.database
    private val getShifts = "SELECT * FROM ${DatabaseHelper.shiftsTable}"

    fun insertShift(newShift: ShiftsModel): Long {
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.shift_Start_Date_COL_2, newShift.shiftStartDate)
        contentValues.put(DatabaseHelper.shift_Day_Of_Year_COL_3, newShift.shiftDayOfYear)
        contentValues.put(DatabaseHelper.shift_End_Date_COL_4, newShift.shiftEndDate)
        contentValues.put(DatabaseHelper.job_ID_COL_5, newShift.jobID)
        contentValues.put(DatabaseHelper.start_Time_COL_6, newShift.startTime)
        contentValues.put(DatabaseHelper.end_Time_COL_7, newShift.endTime)
        contentValues.put(DatabaseHelper.hours_Worked_COL_8, newShift.hoursWorked)

        return db!!.insert(DatabaseHelper.shiftsTable, null, contentValues)
    }

    fun getShifts(): List<ShiftsModel> {
        val res = db!!.rawQuery(getShifts, null)
        val listOfShifts: MutableList<ShiftsModel> = mutableListOf()
        if (res.count != 0) {
            while (res.moveToNext()) {
                val shiftsModel = ShiftsModel(res.getInt(0), res.getString(1), res.getInt(2), res.getString(3), res.getInt(4), res.getString(5), res.getString(6), res.getString(7))
                listOfShifts.add(shiftsModel)
            }
        }
        return listOfShifts
    }

    fun getShiftsForJobID(jobId: Int): MutableList<ShiftsModel> {
        val res = db!!.rawQuery(
                "SELECT * FROM ${DatabaseHelper.shiftsTable} WHERE ${DatabaseHelper.job_ID_COL_5} = $jobId", null)
        val listOfShifts: MutableList<ShiftsModel> = mutableListOf<ShiftsModel>()
        if (res.count != 0) {
            while (res.moveToNext()) {
                val shiftsModel = ShiftsModel(res.getInt(0), res.getString(1), res.getInt(2), res.getString(3), res.getInt(4), res.getString(5), res.getString(6), res.getString(7))
                listOfShifts.add(shiftsModel)
            }
            return listOfShifts
        }
        return listOfShifts
    }

    fun deleteShiftsForJob(jobId: Int): Boolean {
        val whereClause = "${DatabaseHelper.job_ID_COL_5} = $jobId"
        val res = db!!.delete(DatabaseHelper.shiftsTable, whereClause, null)
        return res > 0
    }

    fun editShift(shiftModel: ShiftsModel, shiftIdtoEdit: Int): Boolean{
        val res = db!!.rawQuery(
            "UPDATE ${DatabaseHelper.shiftsTable} SET " +
                    "${DatabaseHelper.shift_Start_Date_COL_2} = \"${shiftModel.shiftStartDate}\"," +
                    "${DatabaseHelper.shift_Day_Of_Year_COL_3} = \"${shiftModel.shiftDayOfYear}\"," +
                    "${DatabaseHelper.shift_End_Date_COL_4} = \"${shiftModel.shiftEndDate}\", " +
                    "${DatabaseHelper.start_Time_COL_6} = \"${shiftModel.startTime}\"," +
                    "${DatabaseHelper.end_Time_COL_7} = \"${shiftModel.endTime}\", " +
                    "${DatabaseHelper.hours_Worked_COL_8} = \"${shiftModel.hoursWorked}\"" +
                    "WHERE ${DatabaseHelper.shift_ID_COL_1} = $shiftIdtoEdit", null)
        return res.count > -1
    }

    fun getShiftID(startDate: String, endDate: String, startHour: String, endHour: String): Int{
        val res = db!!.rawQuery (
            "SELECT ${DatabaseHelper.shift_ID_COL_1} FROM " +
                    "${DatabaseHelper.shiftsTable} " +
                    "WHERE ${DatabaseHelper.shift_Start_Date_COL_2} = \"$startDate\" " +
                    "AND ${DatabaseHelper.shift_End_Date_COL_4} = \"$endDate\" " +
                    "AND ${DatabaseHelper.start_Time_COL_6} = \"$startHour\" " +
                    "AND ${DatabaseHelper.end_Time_COL_7} = \"$endHour\"",
            null
        )
        if (res.count != 0) {
            while (res.moveToNext()) {
                println("CLICKED ON A SHIFT, SHIFT ID is ${res.getInt(0)}")
                return res.getInt(0)
            }
        }
        return 0
    }

    fun deleteIndividualShift(shiftId: Int): Boolean{
        val whereClause = "${DatabaseHelper.shift_ID_COL_1} = $shiftId"
        val res = db!!.delete(DatabaseHelper.shiftsTable, whereClause, null)
        return res > 0
    }
}