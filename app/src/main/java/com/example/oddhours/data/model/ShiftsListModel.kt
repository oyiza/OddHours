package com.example.oddhours.data.model

/**
 * This data class will be passed to the ChildAdapter class
 * With this data class, we have access to pretty much all the info about a job and it's shifts
 */
data class ShiftsListModel(var jobModel: JobModel, var shifts: List<ShiftsModel>)
