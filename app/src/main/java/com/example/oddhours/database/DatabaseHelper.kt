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
            "$shift_Date_COL_2 TEXT, " +
            "$job_ID_COL_3 INTEGER, " +
            "$start_Time_COL_4 TEXT, " +
            "$end_Time_COL_5 TEXT, " +
            "$hours_Worked_COL_6 TEXT, " +
            "FOREIGN KEY($job_ID_COL_3) REFERENCES $jobTable($job_ID_COL_1));"

//    private val getJobs = "SELECT * FROM $jobTable"

    companion object {
        const val dbName = "oddHours"
        const val jobTable = "Jobs"
        const val job_ID_COL_1 = "JobID"
        const val job_Name_COL_2 = "JobName"
        const val job_Location_COL_3 = "JobLocation"
        const val shiftsTable = "Shifts"
        const val shift_ID_COL_1 = "ShiftID"
        const val shift_Date_COL_2 = "ShiftDate"
        const val job_ID_COL_3 = "JobID"
        const val start_Time_COL_4 = "StartTime"
        const val end_Time_COL_5 = "EndTime"
        const val hours_Worked_COL_6 = "HoursWorked"

        var database: SQLiteDatabase? = null
        fun initDatabase(context: Context){
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

    /**
     *  insertJob() - adds a new job to the database, jobID is assigned by default by SQLite
     */
//    fun insertJob(newJob: JobModel): Long {
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(job_Name_COL_2, newJob.jobName)
//        contentValues.put(job_Location_COL_3, newJob.jobLocation)
//
//        return db.insert(jobTable, null, contentValues)
//    }
//
//    /**
//     *  getJobs() - retrieves all the jobs in the Database and returns it as a JobModel List
//     */
//    fun getJobs(): List<JobModel> {
//        val db = this.writableDatabase
//        val res = db.rawQuery(getJobs, null)
//        val listOfJobs: MutableList<JobModel> = mutableListOf()
//        if (res.count != 0) {
//            while (res.moveToNext()) {
//                val jobModel =
//                    JobModel(res.getInt(0), res.getString(1), res.getString(2))
//                listOfJobs.add(jobModel)
//            }
//        }
//        return listOfJobs
//    }
//
//    /**
//     *  checkJobNameAndJobLocationExists - validates whether the same name and location already exists or not
//     */
//    fun checkJobNameAndJobLocationExists(jobName: String, jobLocation: String): Boolean {
//        val db = this.writableDatabase
//        val res = db.rawQuery(
//            "SELECT $job_ID_COL_1 FROM $jobTable WHERE $job_Name_COL_2 = \"$jobName\" AND $job_Location_COL_3 = \"$jobLocation\"",
//            null
//        )
//        return res.count != 0
//    }
//
//    fun dropTable() {
//        val db = this.writableDatabase
//        db.execSQL("DROP TABLE IF EXISTS $jobTable")
//    }
//
//    /**
//     *  getJobID() - takes jobName & jobLocation as parameters to get a JobID
//     */
//    fun getJobID(jobName: String, jobLocation: String): Number {
//        val db = this.writableDatabase
//        val res = db.rawQuery(
//            "SELECT $job_ID_COL_1 FROM $jobTable WHERE $job_Name_COL_2 = \"$jobName\" AND $job_Location_COL_3 = \"$jobLocation\"",
//            null
//        )
//        var response = -1
//        while (res.moveToNext()) {
//            response = res.getInt(0)
//        }
//        return response
//    }
//
//    fun insertShift(shiftID: Int, shiftDate: String, jobID: Int, start: String, end: String): Long {
//        //First calculate the hours worked, from the entered Start and End time
//        // Then push the data to SQLite
//        var hoursWorked = ""
//
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(shift_ID_COL_1, shiftID)
//        contentValues.put(shift_Date_COL_2, shiftDate)
//        contentValues.put(job_ID_COL_3, jobID)
//        contentValues.put(start_Time_COL_4, start)
//        contentValues.put(end_Time_COL_5, end)
//        contentValues.put(hours_Worked_COL_6, hoursWorked)
//
//        return db.insert(shiftsTable, null, contentValues)
//    }
//
//    fun getShiftsForjob(shiftID: Int): Cursor {
//        val db = this.writableDatabase
//        val res = db.rawQuery("SELECT * FROM $shiftsTable WHERE $job_ID_COL_3 = '$shiftID'", null)
//        return res
//    }
}