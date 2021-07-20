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

    fun editJob(jobModel: JobModel, jobIdToEdit: Int): Boolean {
        return dbJobs.editJob(jobModel, jobIdToEdit)
    }

    fun deleteJob(jobModel: JobModel): Boolean {
        // delete associated shifts
        val jobID = dbJobs.getJobID(jobModel.jobName, jobModel.jobLocation)
        deleteShiftsForJob(jobID)

        return dbJobs.deleteJob(jobModel)
    }

    fun getShiftID(shiftStartDate: String, shiftEndDate: String, shiftStartTime: String, shiftEndTime: String): Int {
        return dbShifts.getShiftID(shiftStartDate, shiftEndDate, shiftStartTime, shiftEndTime)
    }

    fun editShift(shiftsModel: ShiftsModel, shiftIdtoEdit: Int): Boolean{
        return dbShifts.editShift(shiftsModel, shiftIdtoEdit)
    }

    private fun deleteShiftsForJob(jobID: Int) {
        if (dbShifts.deleteShiftsForJob(jobID)) {
            Log.d(TAG, "shifts deleted for job with ID: $jobID")
        } else {
            Log.d(TAG, "something went wrong and shifts were not deleted for job with ID: $jobID")
        }
    }

    fun deleteIndividualShift(shiftsModel: ShiftsModel): Boolean{
        val shiftID = dbShifts.getShiftID(shiftsModel.shiftStartDate, shiftsModel.shiftEndDate, shiftsModel.startTime, shiftsModel.endTime)
        return dbShifts.deleteIndividualShift(shiftID)
    }

    fun checkJobExists(jobName: String, jobLocation: String): Boolean {
        return dbJobs.checkJobExists(jobName,jobLocation)
    }

    fun insertShift(newShift: ShiftsModel): Long {
        // TODO: we need validation here to ensure duplicate shifts aren't entered into DB - user can't have the same shift twice for same job
        return dbShifts.insertShift(newShift)
    }

    fun getShiftsForUIList(): MutableList<ShiftsListModel> {
        val jobModelList = buildJobList() // TODO: maybe at this point we don't need to call buildJobList()? just use the jobModelList?

        val shiftsListForAdapter = mutableListOf<ShiftsListModel>()
        var shiftsFromJobId: MutableList<ShiftsModel>
        var shiftsListModel: ShiftsListModel

        /**
         * Looping through each jobId and getting shifts and adding to the List of ShiftsListModel
         */
        for (job in jobModelList) {
            shiftsFromJobId = dbShifts.getShiftsForJobID(job.jobID)
            if (shiftsFromJobId.size > 0) {
                shiftsListModel = ShiftsListModel(job,shiftsFromJobId)
                shiftsListForAdapter.add(shiftsListModel)
            }
        }
        return shiftsListForAdapter
    }

    fun calculateTotalHours(startTimeHour: Int, startTimeMin: Int, endTimeHour: Int, endTimeMin: Int): String {
        val hoursWorked: Int
        val minutesWorked: Int
        val totalTimeWorked: String

        when {
            endTimeMin > startTimeMin -> {
                hoursWorked = endTimeHour - startTimeHour
                minutesWorked = endTimeMin - startTimeMin
                totalTimeWorked = hoursWorked.toString()+"h "+minutesWorked.toString()+"m"
            }
            endTimeMin < startTimeMin -> {
                hoursWorked = (endTimeHour - startTimeHour) - 1
                minutesWorked = (endTimeMin - startTimeMin) + 60
                totalTimeWorked = hoursWorked.toString()+"h "+minutesWorked.toString()+"m"
            }
            else -> { // minutes are equal = 0
                hoursWorked = endTimeHour - startTimeHour
                minutesWorked = startTimeMin - endTimeMin
                totalTimeWorked = hoursWorked.toString()+"h "+minutesWorked.toString()+"m"
            }
        }
        return totalTimeWorked
    }

    companion object {
        private const val TAG = "JobRepository"
    }
}