package com.example.oddhours.data.repository

import com.example.oddhours.data.model.JobInformationModel
import com.example.oddhours.data.model.JobModel

class JobRepository() {

    /**
     * planning to use this to store the list of jobs when we can access it from the DB
     */
    private var jobModelList: List<JobModel>? = null

    private var jobInformation: JobInformationModel? = null

    /**
     * TODO: add a new job to the DB. might need to double check the return type
     */
    fun addNewJob(jobName: String, jobLocation: String, jobInfo: String): JobModel {
        return JobModel(jobName, jobLocation, jobInfo)
    }

    /**
     * TODO: this method should make use of the return object / values from DB (instead of taking in jobName and jobLocation)
     * and create all the jobs then probably store it in jobModelList for the current app session. this should be called from
     * MainActivity after we connect to the DB and query it to get all current jobs the user has stored. the return list is
     * what we feed into the recyclerview adapter!
     */
    fun createJob(jobName: String, jobLocation: String): JobModel {
        return JobModel(jobName, jobLocation, getTotalHoursForWeek(jobName))
    }

    /**
     * TODO: this method will be responsible for calculating total hours in the current week cycle
     * might be smart to pass in something else as parameter instead of jobName (to make lookup faster)
     */
    private fun getTotalHoursForWeek(jobName: String): String {
        return "This week: X hours"
    }
}