package com.example.oddhours.data.repository

import android.content.Context
import android.util.Log
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.database.DatabaseHelper

/**
 * Now, JobRepository class will take context as a parameter
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

class JobRepository(var context: Context) {
    /**
     * store the list of jobs
     */
    var jobModelList: List<JobModel>? = null

    /**
     * store the list of shifts
     */
    var shiftsList: List<ShiftsModel>? = null

//    var context: Context? = null

    /**
     * TODO: this method should make use of the return object / values from DB (instead of taking in jobName and jobLocation)
     * and create all the jobs then probably store it in jobModelList for the current app session. this should be called from
     * MainActivity after we connect to the DB and query it to get all current jobs the user has stored. the return list is
     * what we feed into the recyclerview adapter!
     */
    fun buildJobList(): List<JobModel> {
        Log.i(TAG, "buildJobList() started")
        var db = DatabaseHelper(context!!)
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

    companion object {
        private const val TAG = "JobRepository"
    }
}