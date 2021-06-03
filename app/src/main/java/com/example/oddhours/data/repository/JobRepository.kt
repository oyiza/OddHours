package com.example.oddhours.data.repository

import android.util.Log
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
    private val dbJobs = TableJobs()
    private val dbShifts = TableShifts()
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
     * after adding the job to DB, we relocate to homeFragment where the recyclerView is built again from the DB, updating the jobRepository list
     */
    fun addNewJob(newJob: JobModel): Long {
        return dbJobs.insertJob(newJob)
    }

    fun getJobID(jobName: String, jobLocation: String): Int {
        return dbJobs.getJobID(jobName, jobLocation)
    }

    // TODO: this method does exactly what buildJobList() does.. if no need for this we can remove it then
//    fun getAllJobs(): List<JobModel>{
//        return dbJobs.getAllJobs()
//    }

    fun editJob(jobModel: JobModel, jobIdToEdit: Int): Boolean {
        return dbJobs.editJob(jobModel, jobIdToEdit)
    }

    fun deleteJob(jobModel: JobModel): Boolean {
        return dbJobs.deleteJob(jobModel)
    }

    fun checkJobExists(jobName: String, jobLocation: String): Boolean{
        return dbJobs.checkJobExists(jobName,jobLocation)
    }

    fun insertShift(newShift: ShiftsModel): Long{
        return dbShifts.insertShift(newShift)
    }

    fun getShiftsForUIList(): MutableList<ShiftsListModel> {
        val jobModelList = buildJobList()

        val shiftsListForAdapter = mutableListOf<ShiftsListModel>()
        var shiftsFromJobId: MutableList<ShiftsModel>
        var shiftsListModel: ShiftsListModel

        /**
         * Looping through each jobId and getting shifts and adding to the List of ShiftsListModel
         */
        for(job in jobModelList){
            shiftsFromJobId = dbShifts.getShiftsForJobID(job.jobID)
            if(shiftsFromJobId.size != 0){
                shiftsListModel = ShiftsListModel(job,shiftsFromJobId)
                shiftsListForAdapter.add(shiftsListModel)
            }
        }
        return shiftsListForAdapter
    }

    fun buildShiftsModel(
        shiftID: Int,
        dateForDb: String,
        clickedJobID: Int,
        startTimeForDb: String,
        endTimeForDb: String,
        totalTimeWorked: String
    ): ShiftsModel {
        return ShiftsModel(shiftID, dateForDb, clickedJobID, startTimeForDb, endTimeForDb, totalTimeWorked)
    }

    fun calculateTotalHours(startTimeHour: Int, startTimeMin: Int, endTimeHour: Int, endTimeMin: Int): String{
        val hoursWorked = endTimeHour - startTimeHour
        val minutesWorked: Int
        val totalTimeWorked: String

        if(endTimeMin > startTimeMin) {
            minutesWorked = endTimeMin - startTimeMin
            totalTimeWorked = hoursWorked.toString()+"h "+minutesWorked.toString()+"m"
        }
        else{
            minutesWorked = startTimeMin - endTimeMin
            totalTimeWorked = hoursWorked.toString()+"h "+minutesWorked.toString()+"m"
        }
        return totalTimeWorked
    }

    fun buildJobModel(jobName: String, jobLocation: String): JobModel {
        return JobModel(1, jobName, jobLocation)
    }

    // potentially to retrieve jobs from db if we need that functionality
    private fun getJobFromDb(jobName: String, jobLocation: String): JobModel {
        val jobId = getJobID(jobName, jobLocation)
        return JobModel(jobId, jobName, jobLocation)
    }

    companion object {
        private const val TAG = "JobRepository"
    }
}