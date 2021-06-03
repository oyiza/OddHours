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
     * TODO: this method will be responsible for calculating total hours in the current week cycle for a specific job
     * might be smart to pass in something else as parameter instead of jobName (to make lookup faster)
     */
    private fun getTotalHoursForWeek(jobName: String): String {
        return "This week: X hours"
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

    // TODO: could rename this to a simple check that job doesn't exist in db
    fun checkJobNameAndJobLocationExists(jobName: String, jobLocation: String): Boolean{
        return dbJobs.checkJobNameAndJobLocationExists(jobName,jobLocation)
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

    fun buildJobModel(jobName: String, jobLocation: String): JobModel {
        return JobModel(1, jobName, jobLocation)
    }

    private fun getJobFromDb(jobName: String, jobLocation: String): JobModel {
        val jobId = getJobID(jobName, jobLocation)
        return JobModel(jobId, jobName, jobLocation)
    }

    companion object {
        private const val TAG = "JobRepository"
    }
}