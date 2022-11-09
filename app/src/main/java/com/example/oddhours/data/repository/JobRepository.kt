package com.example.oddhours.data.repository

import android.util.Log
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsListModel
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.database.TableJobs
import com.example.oddhours.database.TableShifts
import com.example.oddhours.utils.Constants
import java.util.*

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

    init {
        if (jobModelList == null) {
            jobModelList = buildJobList()
        }
    }

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

    fun getShiftID(
        shiftStartDate: String,
        shiftEndDate: String,
        shiftStartTime: String,
        shiftEndTime: String
    ): Int {
        return dbShifts.getShiftID(shiftStartDate, shiftEndDate, shiftStartTime, shiftEndTime)
    }

    fun editShift(shiftsModel: ShiftsModel, shiftIdToEdit: Int): Boolean {
        return dbShifts.editShift(shiftsModel, shiftIdToEdit)
    }

    private fun deleteShiftsForJob(jobID: Int) {
        if (dbShifts.deleteShiftsForJob(jobID)) {
            Log.d(TAG, "shifts deleted for job with ID: $jobID")
        } else {
            Log.d(TAG, "something probably went wrong and shifts were not deleted for job with ID: $jobID")
        }
    }

    fun deleteIndividualShift(shiftsModel: ShiftsModel): Boolean {
        val shiftID = dbShifts.getShiftID(
            shiftsModel.shiftStartDate,
            shiftsModel.shiftEndDate,
            shiftsModel.startTime,
            shiftsModel.endTime
        )
        return dbShifts.deleteIndividualShift(shiftID)
    }

    fun jobExists(jobName: String, jobLocation: String): Boolean {
        return dbJobs.jobExists(jobName, jobLocation)
    }

    fun shiftExists(newShift: ShiftsModel): Boolean { // takes a shiftsModel object
        return dbShifts.shiftExists(newShift)
    }

    fun insertShift(newShift: ShiftsModel): Long {
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

            val sortedShifts = shiftsFromJobId.sortedBy { shift -> shift.shiftDayOfYear}

            if (sortedShifts.isNotEmpty()) {
                shiftsListModel = ShiftsListModel(job, sortedShifts)
                shiftsListForAdapter.add(shiftsListModel)
            }
        }
        return shiftsListForAdapter
    }

    fun calculateTotalHours(
        startTimeHour: Int,
        startTimeMin: Int,
        endTimeHour: Int,
        endTimeMin: Int
    ): String {
        val hoursWorked: Int
        val minutesWorked: Int
        val totalTimeWorked: String

        when {
            endTimeMin > startTimeMin -> {
                hoursWorked = endTimeHour - startTimeHour
                minutesWorked = endTimeMin - startTimeMin
                totalTimeWorked = hoursWorked.toString() + "h " + minutesWorked.toString() + "m"
            }
            endTimeMin < startTimeMin -> {
                hoursWorked = (endTimeHour - startTimeHour) - 1
                minutesWorked = (endTimeMin - startTimeMin) + 60
                totalTimeWorked = hoursWorked.toString() + "h " + minutesWorked.toString() + "m"
            }
            else -> { // minutes are equal = 0
                hoursWorked = endTimeHour - startTimeHour
                minutesWorked = startTimeMin - endTimeMin
                totalTimeWorked = hoursWorked.toString() + "h " + minutesWorked.toString() + "m"
            }
        }
        return totalTimeWorked
    }

    fun getTotalHoursForJobAsInt(job: JobModel): Int {
        var shiftsList = mutableListOf<ShiftsModel>()
        shiftsList = dbShifts.getShiftsForJobID(job.jobID)
        var totalHours = 0

        for (shift in shiftsList) {
            // Log.d("ChartsFragment", job.jobName + " " + shift.hoursWorked) // DEBUG
            totalHours += convertShiftTimeToInt(shift.hoursWorked)
        }

        return totalHours
    }

    fun getShiftType(endDate: Calendar, startDate: Calendar): Int {
        val endDateDayOfMonth = endDate.get(Calendar.DAY_OF_MONTH)
        val endDateMonth = endDate.get(Calendar.MONTH)
        val endDateDayOfWeek = endDate.get(Calendar.DAY_OF_WEEK)

        val startDateDayOfMonth = startDate.get(Calendar.DAY_OF_MONTH)
        val startDateMonth = startDate.get(Calendar.MONTH)
        val startDateDayOfWeek = startDate.get(Calendar.DAY_OF_WEEK)

        val oneDayOverlapSameMonth = endDateDayOfMonth - startDateDayOfMonth == 1
        val oneDayOverlapDiffMonth = ((startDateDayOfWeek == Constants.SATURDAY && endDateDayOfWeek == Constants.SUNDAY) || (startDateDayOfWeek == endDateDayOfWeek - 1))
                && (endDateMonth - startDateMonth == 1)
        val oneDayOverlap = oneDayOverlapSameMonth || oneDayOverlapDiffMonth

        // DEBUG LOGS (uncomment following log lines for more information in logcat
        // Log.d(TAG, "startDate: ${startDate.time}, endDate: ${endDate.time}")
        // Log.d(TAG, "startDateDay: $startDateDay")
        // Log.d(TAG, "startDateMonth: $startDateMonth")
        // Log.d(TAG, "endDateDay: $endDateDay")
        // Log.d(TAG, "endDateMonth: $endDateMonth")
        // Log.d(TAG, "endDateDayOfWeek: $endDateDayOfWeek, startDateDayOfWeek: $startDateDayOfWeek")
        // Log.d(TAG, "----------------------------------------------------------------------------")

        if (((startDateDayOfWeek == Constants.SATURDAY && endDateDayOfWeek == Constants.SUNDAY) && oneDayOverlap)
            || ((endDateDayOfWeek == startDateDayOfWeek + 1) && oneDayOverlap)) {
            // overnight shift
            return Constants.OVERNIGHT_SHIFT
        } else if (endDateDayOfWeek == startDateDayOfWeek) {
            // day shift
            return Constants.DAY_SHIFT
        }

        return Constants.INVALID_SHIFT_RANGE
    }

    private fun convertShiftTimeToInt(hoursWorked: String): Int {
        var shiftTime = 0
        if (hoursWorked.isNotBlank()) {
            val hours = hoursWorked.substringBefore("h").toInt()
            val minutes = hoursWorked.substringAfter(" ").substringBefore("m").toInt()

            shiftTime = hours + (minutes / 60)
            // Log.d("ChartsFragment", "shiftTime: $shiftTime") // DEBUG
        }

        return shiftTime
    }

    companion object {
        private const val TAG = "JobRepository"
    }
}