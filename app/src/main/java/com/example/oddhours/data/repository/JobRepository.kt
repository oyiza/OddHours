package com.example.oddhours.data.repository

import android.util.Log
import com.example.oddhours.data.model.JobInfoModel
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsListModel
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.database.TableJobs
import com.example.oddhours.database.TableShifts

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
    val dbJobs = TableJobs()
    val dbShifts = TableShifts()
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
    fun addNewJob(newJob: JobModel): Long {
        return dbJobs.insertJob(newJob)
    }

    fun getJobID(jobName: String, jobLocation: String): Int {
        return dbJobs.getJobID(jobName, jobLocation)
    }

    fun getAllJobIDs(): List<Int>{
        return dbJobs.getAllJobID()
    }

    fun editJob(jobName: String, jobLocation: String, jobIdToEdit: Int): Boolean {
        return dbJobs.editJob(jobName, jobLocation, jobIdToEdit)
    }

    fun deleteJob(jobName: String, jobLocation: String): Boolean {
        // TODO: any other validation we want to do here?
        return dbJobs.deleteJob(jobName, jobLocation)
    }

    fun checkJobNameAndJobLocationExists(jobName: String, jobLocation: String): Boolean{
        return dbJobs.checkJobNameAndJobLocationExists(jobName,jobLocation)
    }

    fun insertShift(newShift: ShiftsModel): Long{
        return dbShifts.insertShift(newShift)
    }

    fun getShiftsForUIList(): MutableList<ShiftsListModel> {
        val jobIDList = getAllJobIDs()

        var jobInfoModel: JobInfoModel
        var shiftsListForAdapter = mutableListOf<ShiftsListModel>()
        var shiftsFromJobId: MutableList<ShiftsModel>
        var shiftsListModel: ShiftsListModel

        /**
         * Looping through each jobId and getting shifts and adding to the List of ShiftsListModel
         */
        for(i in jobIDList){
            shiftsFromJobId = dbShifts.getShiftsForJobID(i)
            if(shiftsFromJobId.size != 0){
                jobInfoModel = dbJobs.getJobNameAndLocation(i)
                shiftsListModel = ShiftsListModel(jobInfoModel,shiftsFromJobId)
                shiftsListForAdapter.add(shiftsListModel)
            }
        }

        return shiftsListForAdapter
    }

    companion object {
        private const val TAG = "JobRepository"
    }
}