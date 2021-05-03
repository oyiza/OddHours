package com.example.oddhours.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, dbName, null, 1){

    val sqlCreateTable_job = "CREATE TABLE IF NOT EXISTS $jobTable (" +
            "$job_COL_0 INTEGER PRIMARY KEY, " +
            "$job_COL_1 TEXT, " +
            "$job_COL_2 TEXT);"
    val sqlCreateTable_hours = "CREATE TABLE IF NOT EXISTS $hoursTable (" +
            "$hours_COL_0 INTEGER PRIMARY KEY, " +
            "$hours_COL_1 TEXT, " +
            "$hours_COL_2 INTEGER, " +
            "$hours_COL_3 TEXT, " +
            "$hours_COL_4 TEXT, " +
            "$hours_COL_5 INTEGER, " +
            "FOREIGN KEY($hours_COL_2) REFERENCES $jobTable($job_COL_0));"

    companion object {
        val dbName = "oddHours"
        val jobTable = "Job"
        val job_COL_0 = "JobID"
        val job_COL_1 = "JobName"
        val job_COL_2 = "JobLocation"
        val hoursTable = "Hours"
        val hours_COL_0 = "ShiftID"
        val hours_COL_1 = "ShiftDate"
        val hours_COL_2 = "JobID"
        val hours_COL_3 = "StartTime"
        val hours_COL_4 = "EndTime"
        val hours_COL_5 = "HoursWorked"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(sqlCreateTable_job)
        db!!.execSQL(sqlCreateTable_hours)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $dbName")
        onCreate(db)
    }

}