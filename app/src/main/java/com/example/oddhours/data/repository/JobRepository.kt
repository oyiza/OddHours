package com.example.oddhours.data.repository

import com.example.oddhours.data.model.JobModel

class JobRepository {

    private var JobModel: JobModel? = null

    fun getTotalHoursForWeek(): String {
        return this.JobModel.toString()
    }
}