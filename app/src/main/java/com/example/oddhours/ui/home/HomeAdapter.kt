package com.example.oddhours.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.data.repository.JobRepository
import com.example.oddhours.utils.Constants
import kotlinx.android.synthetic.main.addshift.view.*
import kotlinx.android.synthetic.main.edit_delete_job.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.job_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * HomeAdapter now also takes in the context as a parameter in the constructor
 * context is required for the Alert Dialog
 */
class HomeAdapter(private var jobList: List<JobModel>, val context: Context, val navController: NavController) : RecyclerView.Adapter<HomeAdapter.JobViewHolder>() {

    private var jobRepository = JobRepository()

    private val myFormat = "MM/dd/yyyy"
    private val sdf = SimpleDateFormat(myFormat, Locale.CANADA)

    /**
     *  The 3 variables below are used for insert these values in SQLite
     */
    var startDateForDb = ""
    var endDateForDb = ""
    var startTimeForDb = ""
    var endTimeForDb = ""

    /**
     *  The 4 variables below are used for calculating the hours and min worked
     */
    var startTimeHour = 0
    var startTimeMin = 0
    var endTimeHour = 0
    var endTimeMin = 0

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_row, parent, false)

        return JobViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val currentItem = jobList[position]

        holder.jobName.text = currentItem.jobName
        holder.jobLocation.text = currentItem.jobLocation
        holder.jobInfo.text = currentItem.jobInfo

        // onClick listener for the add hours button
        holder.addHoursButton.setOnClickListener {
            Log.i(TAG, "clicked button of : ${holder.jobName.text}")

            val clickedJobID = jobRepository.getJobID(holder.jobName.text.toString(), holder.jobLocation.text.toString())

            /**
             * below code is for popup dialog and the respective on button click listeners
             */
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.addshift, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle("Add a Shift")
            val mAlertDialog = mBuilder.show()
            mDialogView.shiftStartDateTV.text = "${month+1}/${day}/${year}"
            mDialogView.shiftEndDateTV.text = "${month+1}/${day}/${year}"

            /**
             * Shift Start Date Button onclicklistener
             *  - sets the maxDate to today's date, so that the user cannot pick a future date
             */

            mDialogView.shiftStartDateBTN.setOnClickListener{
                val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    startDateForDb = sdf.format(c.time).toString()
                    mDialogView.shiftStartDateTV.text = sdf.format(c.time)
                }, year, month, day)
                dpd.datePicker.maxDate = c.timeInMillis
                dpd.show()
            }

            startDateForDb = sdf.format(c.time).toString()

            /**
             *  Start Time Button onclicklistener
             *   - sets the 24 hour view to false so the user can use AM/PM options
             *   - however, the time is displayed in 24-hour format
             */

            mDialogView.startTimeBTN.setOnClickListener{
                val timeSetListener = TimePickerDialog.OnTimeSetListener{
                        timePicker, hour, minute ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)
                    startTimeHour = hour
                    startTimeMin = minute
                    mDialogView.startTimeTV.text = SimpleDateFormat("HH:mm").format(c.time)
                    startTimeForDb = SimpleDateFormat("HH:mm").format(c.time)
                }

                TimePickerDialog(context, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(
                    Calendar.MINUTE), false).show()
            }

            /**
             * Shift End Date Button onclicklistener
             *  - sets the maxDate to today's date, so that the user cannot pick a future date
             */

            mDialogView.shiftEndDateBTN.setOnClickListener{
                val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    endDateForDb = sdf.format(c.time).toString()
                    mDialogView.shiftEndDateTV.text = sdf.format(c.time)
                }, year, month, day)
                dpd.datePicker.maxDate = c.timeInMillis
                dpd.show()
            }

            /**
             * End Time Button on click listener
             *  - sets the 24 hour view to false so the user can use AM/PM options
             *  - however, the time is displayed in 24-hour format
             */

            mDialogView.endTimeBTN.setOnClickListener{
                val timeSetListener = TimePickerDialog.OnTimeSetListener{
                        timePicker, hour, minute ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)
                    endTimeHour = hour
                    endTimeMin = minute
                    mDialogView.endTimeTV.text = SimpleDateFormat("HH:mm").format(c.time)
                    endTimeForDb = SimpleDateFormat("HH:mm").format(c.time)
                }
                TimePickerDialog(context, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(
                    Calendar.MINUTE), false).show()
            }

            /**
             *  Save Button onclick listener
             *   - First verifies that user selected the End Time that is ahead of Start Time
             *   - Next, calculateTotalHours() function calculates the overall hours+min worked in the shift
             *      - calculateHours() function returns a string
             */

            // TODO: praise
            mDialogView.saveBTN.setOnClickListener {
                if (endTimeHour > startTimeHour) {
                    val totalTimeWorked = jobRepository.calculateTotalHours(startTimeHour, startTimeMin, endTimeHour, endTimeMin)
                    val shiftsModel = ShiftsModel(1, startDateForDb, clickedJobID, startTimeForDb, endTimeForDb, totalTimeWorked )
                    jobRepository.insertShift(shiftsModel)
                    mAlertDialog.dismiss()
                }
                else {
                    Toast.makeText(
                        holder.itemView.context,
                        "End Time is earlier then Start Time",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            mDialogView.cancelBTN.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }

        // onClick listener for the card - in case we want to click the card and open the job details
        holder.itemView.setOnClickListener{
            Log.i(TAG, "clicked card: ${holder.jobName.text}")
            Toast.makeText(
                holder.itemView.context,
                "You clicked ${holder.jobName.text}'s card",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "clicked position is $position")
        }

        // long click listener for the card
        holder.itemView.setOnLongClickListener{
            Toast.makeText(holder.itemView.context, "Long click detected on ${holder.jobName.text}", Toast.LENGTH_SHORT).show()

            val mDialogView = LayoutInflater.from(context).inflate(R.layout.edit_delete_job, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle(holder.jobName.text)
            val mAlertDialog = mBuilder.show()

            // onClick listener for edit button
            mDialogView.btnEditJob.setOnClickListener{
                mAlertDialog.dismiss()
                openAddJobFragment(holder.jobName, holder.jobLocation)
            }

            // onClick listener for delete button
            mDialogView.btnDeleteJob.setOnClickListener{
                // maybe say like 'are you sure?' before deleting it lol
                // TODO: wrap in try catch? custom exception needed (JobNotFoundException)
                val jobModel = JobModel(1, holder.jobName.text as String, holder.jobLocation.text as String)
                val isDeleted = jobRepository.deleteJob(jobModel)
                if (isDeleted) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Successfully deleted ${holder.jobName.text}",
                        Toast.LENGTH_SHORT
                    ).show()
                    mAlertDialog.dismiss()
                    jobList = removeItemFromUI(jobList, position)
                    notifyDataSetChanged()
                } else {
                    Log.i(TAG, "Error, not able to delete job")
                    mAlertDialog.dismiss()
                }
            }
            return@setOnLongClickListener true
        }
    }

    // TODO: this might have to go to jobRepository too
    private fun removeItemFromUI(list: List<JobModel>, position: Int): List<JobModel> {
        Log.i(TAG, "removeItem() called: position is $position")
        val result = list.toMutableList()
        result.removeAt(position)
        return result.toList()
    }

    private fun openAddJobFragment(jobName: TextView, jobLocation: TextView) {
        try {
            val jobNameToEdit = jobName.text.toString()
            val jobLocationToEdit = jobLocation.text.toString()
            val args: Bundle = Bundle()
            args.putBoolean(Constants.CURRENTLY_EDITING_JOB, true)
            args.putString(Constants.JOB_NAME, jobNameToEdit)
            args.putString(Constants.JOB_LOCATION, jobLocationToEdit)
            navController.navigate(R.id.navigation_addjob, args)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, e.printStackTrace().toString())
        } catch (e: Exception) {
            Log.e(TAG, e.printStackTrace().toString())
        }
    }

    override fun getItemCount() = jobList.size

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jobName: TextView = itemView.txtJobName
        val jobLocation: TextView = itemView.txtJobLocation
        val jobInfo: TextView = itemView.txtJobInfo
        val addHoursButton: Button = itemView.btnAddHours
    }

    companion object {
        private const val TAG = "HomeAdapter"
    }
}