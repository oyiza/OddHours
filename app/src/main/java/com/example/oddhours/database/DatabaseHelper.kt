package com.example.oddhours.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, dbName, null, 1){

    val sqlCreateTable_job = "CREATE TABLE IF NOT EXISTS $jobTable (" +
            "$job_ID_COL_1 INTEGER PRIMARY KEY, " +
            "$job_Name_COL_2 TEXT, " +
            "$job_Location_COL_3 TEXT);"
    val sqlCreateTable_shifts = "CREATE TABLE IF NOT EXISTS $shiftsTable (" +
            "$shift_ID_COL_1 INTEGER PRIMARY KEY, " +
            "$shift_Date_COL_2 TEXT, " +
            "$job_ID_COL_3 INTEGER, " +
            "$start_Time_COL_4 TEXT, " +
            "$end_Time_COL_5 TEXT, " +
            "$hours_Worked_COL_6 TEXT, " +
            "FOREIGN KEY($job_ID_COL_3) REFERENCES $jobTable($job_ID_COL_1));"

    val getJobs = "SELECT * FROM $jobTable"

    //JobModel (JobId, JobName, JobLocation)
    //ShiftsModel (ShiftID, ShiftDate, JobID, StartTime, EndTime, HoursWorked)

    companion object {
        val dbName = "oddHours"
        val jobTable = "Jobs"
        val job_ID_COL_1 = "JobID"
        val job_Name_COL_2 = "JobName"
        val job_Location_COL_3 = "JobLocation"
        val shiftsTable = "Shifts"
        val shift_ID_COL_1 = "ShiftID"
        val shift_Date_COL_2 = "ShiftDate"
        val job_ID_COL_3 = "JobID"
        val start_Time_COL_4 = "StartTime"
        val end_Time_COL_5 = "EndTime"
        val hours_Worked_COL_6 = "HoursWorked"
    }

    // Supported Data Types by SQLite
    //  NULL, INTEGER, REAL, TEXT, BLOB
    // so, we will have to store, dates and times as strings
    // "YYYY-MM-DD" and "HH:MM:SS.SSS"

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(sqlCreateTable_job)
        db!!.execSQL(sqlCreateTable_shifts)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $dbName")
        onCreate(db)
    }

    fun insertJob(jobID: Int, jobName: String, jobLocation: String): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(job_ID_COL_1, jobID)
        contentValues.put(job_Name_COL_2, jobName)
        contentValues.put(job_Location_COL_3, jobLocation)

        return db.insert(jobTable, null, contentValues)
    }

    fun insertShift(shiftID: Int, shiftDate: String, jobID: Int, start: String, end: String): Long{
        //First calculate the hours worked, from the entered Start and End time
        // Then push the data to SQLite
        var hoursWorked = ""

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(shift_ID_COL_1, shiftID)
        contentValues.put(shift_Date_COL_2, shiftDate)
        contentValues.put(job_ID_COL_3, jobID)
        contentValues.put(start_Time_COL_4, start)
        contentValues.put(end_Time_COL_5, end)
        contentValues.put(hours_Worked_COL_6, hoursWorked)

        return db.insert(shiftsTable, null, contentValues)
    }

    fun getJobs(): Cursor {
        val db = this.writableDatabase
        val res = db.rawQuery(getJobs, null)
        return res
    }

    fun getShiftsForjob(shiftID: Int): Cursor{
        val db = this.writableDatabase
        val res = db.rawQuery("SELECT * FROM $shiftsTable WHERE $job_ID_COL_3 = '$shiftID'", null)
        return res
    }

}
