package com.example.oddhours.data.repository

import android.util.Log
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.database.TableJobs

/**
 * JobRepository class could take context as a parameter
 * When we pass a parameter with val or var keyword, it actually becomes a property of the class
 * Its not just a parameter anymore, if you pass a parameter without val or var keyword, then you have to
 * explicitly define a property
 *  See example below
 *  init block is a feature in Kotlin, where init block gets executed right after primary constructor
 *
 *  class JobRepository(context: Context){
 *      var ctx: Context? = null
 *      init{
 *          ctx = context
 *      }
 *  }
 */

class JobRepository() {

    var jobModelList: List<JobModel>? = null

    /**
     * store the list of shifts
     */
    var shiftsList: List<ShiftsModel>? = null

//    var context: Context? = null

    /**
     * @return list of jobs from the DB
     */
    fun buildJobList(): List<JobModel> {
        Log.i(TAG, "buildJobList() started")
        val db = TableJobs()
        jobModelList = db.getJobs()
        return jobModelList as List<JobModel>
    }

    /**
     * TODO: this method will be responsible for calculating total hours in the current week cycle for a specific job
     * might be smart to pass in something else as parameter instead of jobName (to make lookup faster)
     */
    private fun getTotalHoursForWeek(jobName: String): String {
        return "This week: X hours"
    }

    /**
     * TODO: add a new job to the DB (and possibly to jobModelList). might need to double check the return type
     */
    fun addNewJob(jobName: String, jobLocation: String, jobInfo: String): JobModel {
        return JobModel(1, jobName, jobLocation, jobInfo)
    }

    fun getJobID(jobName: String, jobLocation: String): Int {
        val db = TableJobs()
        return db.getJobID(jobName, jobLocation)
    }

    fun editJob(jobName: String, jobLocation: String, jobIdToEdit: Int): Boolean {
        val db = TableJobs()
        return db.editJob(jobName, jobLocation, jobIdToEdit)
    }

    fun deleteJob(jobName: String, jobLocation: String): Boolean {
        val db = TableJobs()
        // TODO: any other validation we want to do here?
        return db.deleteJob(jobName, jobLocation)
    }

    companion object {
        private const val TAG = "JobRepository"
    }
}