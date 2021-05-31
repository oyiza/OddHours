package com.example.oddhours.database

import android.content.ContentValues
import com.example.oddhours.data.model.JobModel

class TableJobs {

    private val getJobs = "SELECT * FROM ${DatabaseHelper.jobTable}"

    // TODO: close cursors - https://youtu.be/CzGNaiSoh7E?t=1910
    fun insertJob(newJob: JobModel): Long {
        val db = DatabaseHelper.database
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.job_Name_COL_2, newJob.jobName)
        contentValues.put(DatabaseHelper.job_Location_COL_3, newJob.jobLocation)

        return db!!.insert(DatabaseHelper.jobTable, null, contentValues)
    }

    /**
     *  getJobs() - retrieves all the jobs in the Database and returns it as a JobModel List
     */
    fun getJobs(): List<JobModel> {
        val db = DatabaseHelper.database
        val res = db!!.rawQuery(getJobs, null)
        val listOfJobs: MutableList<JobModel> = mutableListOf()
        if (res.count != 0) {
            while (res.moveToNext()) {
                // TODO: maybe use jobRepo::addNewJob() here instead of actual jobModel? also this is the paradox method for me
                val jobModel =
                    JobModel(res.getInt(0), res.getString(1), res.getString(2))
                listOfJobs.add(jobModel)
            }
        }
        return listOfJobs
    }

    /**
     *  checkJobNameAndJobLocationExists - validates whether the same name and location already exists or not
     */
    fun checkJobNameAndJobLocationExists(jobName: String, jobLocation: String): Boolean {
        val db = DatabaseHelper.database
        val res = db!!.rawQuery (
            "SELECT ${DatabaseHelper.job_ID_COL_1} FROM ${DatabaseHelper.jobTable} WHERE ${DatabaseHelper.job_Name_COL_2} = \"$jobName\" AND ${DatabaseHelper.job_Location_COL_3} = \"$jobLocation\"",
            null
        )
        return res.count != 0
    }

    /**
     *  getJobID - returns the job id when user clicks on Add Hours to add a new shift
     *
     */
    fun getJobID(jobName: String, jobLocation: String): Int {
        val db = DatabaseHelper.database
        val res = db!!.rawQuery (
            "SELECT ${DatabaseHelper.job_ID_COL_1} FROM ${DatabaseHelper.jobTable} WHERE ${DatabaseHelper.job_Name_COL_2} = \"$jobName\" AND ${DatabaseHelper.job_Location_COL_3} = \"$jobLocation\"",
            null
        )
        if (res.count != 0) {
            while (res.moveToNext()) {
                println("CLICKED ON A JOB, JOB ID is ${res.getInt(0)}")
                return res.getInt(0)
            }
        }
        return 0
    }

    fun editJob(jobName: String, jobLocation: String, jobIdToEdit: Int): Boolean {
        val db = DatabaseHelper.database
        val res = db!!.rawQuery(
            "UPDATE ${DatabaseHelper.jobTable} SET ${DatabaseHelper.job_Name_COL_2} = \"$jobName\", ${DatabaseHelper.job_Location_COL_3} = \"$jobLocation\" WHERE ${DatabaseHelper.job_ID_COL_1} = $jobIdToEdit",
        null
        )
        return res.count > -1
    }

    fun deleteJob(jobName: String, jobLocation: String): Boolean {
        val db = DatabaseHelper.database
        val whereClause = "${DatabaseHelper.job_Name_COL_2} = \"$jobName\" AND ${DatabaseHelper.job_Location_COL_3} = \"$jobLocation\""
        val res = db!!.delete(DatabaseHelper.jobTable, whereClause, null)
        return res > 0
    }
}