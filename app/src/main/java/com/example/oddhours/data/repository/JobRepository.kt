package com.example.oddhours.data.repository

import com.example.oddhours.data.model.JobModel

class JobRepository() {

    /**
     * planning to use this to store the list of jobs when we can access it from the DB
     */
    private var jobModelList: List<JobModel>? = null

    /**
     * this will be the method to get the jobs and store in jobModelList
     */
//    fun getJobs(): List<JobModel> {
//
//    }

    fun createJob(jobName: String, jobLocation: String, jobInfo: String): JobModel {
        return JobModel(jobName, jobLocation, jobInfo)
    }

    fun getTotalHoursForWeek(): String {
        return "This week: X hours"
    }
}