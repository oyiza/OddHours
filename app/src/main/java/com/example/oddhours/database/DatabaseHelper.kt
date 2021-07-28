package com.example.oddhours.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, dbName, null, 1) {

    private val sqlCreateTableJob = "CREATE TABLE IF NOT EXISTS $jobTable (" +
            "$job_ID_COL_1 INTEGER PRIMARY KEY, " +
            "$job_Name_COL_2 TEXT, " +
            "$job_Location_COL_3 TEXT);"
    private val sqlCreateTableShifts = "CREATE TABLE IF NOT EXISTS $shiftsTable (" +
            "$shift_ID_COL_1 INTEGER PRIMARY KEY, " +
            "$shift_Start_Date_COL_2 TEXT, " +
            "$shift_Day_Of_Year_COL_3 TEXT," +
            "$shift_End_Date_COL_4 TEXT, " +
            "$job_ID_COL_5 INTEGER, " +
            "$start_Time_COL_6 TEXT, " +
            "$end_Time_COL_7 TEXT, " +
            "$hours_Worked_COL_8 TEXT, " +
            "FOREIGN KEY($job_ID_COL_5) REFERENCES $jobTable($job_ID_COL_1));"

//    private val getJobs = "SELECT * FROM $jobTable"

    companion object {
        const val dbName = "oddHours"
        const val jobTable = "Jobs"
        const val job_ID_COL_1 = "JobID"
        const val job_Name_COL_2 = "JobName"
        const val job_Location_COL_3 = "JobLocation"
        const val shiftsTable = "Shifts"
        const val shift_ID_COL_1 = "ShiftID"
        const val shift_Start_Date_COL_2 = "ShiftStartDate"
        const val shift_Day_Of_Year_COL_3 = "ShiftDayOfYear"
        const val shift_End_Date_COL_4 = "ShiftEndDate"
        const val job_ID_COL_5 = "JobID"
        const val start_Time_COL_6 = "StartTime"
        const val end_Time_COL_7 = "EndTime"
        const val hours_Worked_COL_8 = "HoursWorked"

        var database: SQLiteDatabase? = null

        fun initDatabase(context: Context) {
            database = DatabaseHelper(context).writableDatabase
        }
    }

    // Supported Data Types by SQLite
    //  NULL, INTEGER, REAL, TEXT, BLOB
    // so, we will have to store, dates and times as strings
    // "YYYY-MM-DD" and "HH:MM:SS.SSS"


    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(sqlCreateTableJob)
        db.execSQL(sqlCreateTableShifts)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $dbName")
        onCreate(db)
    }
}