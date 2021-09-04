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
import com.example.oddhours.utils.Helper
import kotlinx.android.synthetic.main.dialog_add_shift.view.*
import kotlinx.android.synthetic.main.dialog_edit_delete_job.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_job.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * HomeAdapter now also takes in the context as a parameter in the constructor
 * context is required for the Alert Dialog
 */
class HomeAdapter(private var jobList: List<JobModel>, val context: Context, private val navController: NavController) : RecyclerView.Adapter<HomeAdapter.JobViewHolder>() {

    private var jobRepository = JobRepository()

    private val myFormat = "MM/dd/yyyy"
    private val sdf = SimpleDateFormat(myFormat, Locale.CANADA)

    /**
     *  The 4 variables below are used to insert these values in SQLite
     */
    var startDateForDb = ""
    var endDateForDb = ""
    var startTimeForDb = ""
    var endTimeForDb = ""

    /**
     *  The 4 variables below are used for calculating the hours and min worked (together with the dates above - if they're different)
     */
    var startTimeHour = 0
    var startTimeMin = 0
    var endTimeHour = 0
    var endTimeMin = 0

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    val daysInMonth = intArrayOf(31,28,31,30,31,30,31,31,30,31,30,31)
    var dayOfYear = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)

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

            // NOTE: at the beginning, startDate and endDate are always the same because of similar initialization
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            val today = Calendar.getInstance()

            /**
             * below code is for popup dialog and the respective on button click listeners
             */
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_shift, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle("Add a Shift")
            val mAlertDialog = mBuilder.show()
            // not showing date text so we force the user to click on the date buttons - this sets the startDate and endDate correctly for us
//            mDialogView.shiftStartDateTV.text = sdf.format(c.time)
//            mDialogView.shiftEndDateTV.text = sdf.format(c.time)

            /**
             * Shift Start Date Button onclicklistener
             *  - sets the maxDate to today's date, so that the user cannot pick a future date
             */

            mDialogView.shiftStartDateBtn.setOnClickListener{
                val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    startDateForDb = sdf.format(c.time).toString() // "06/14/2021"
                    mDialogView.shiftStartDateTv.text = sdf.format(c.time)
                    startDate.set(year, monthOfYear, dayOfMonth)
                    // DEBUG TODO: remove this eventually
                    // Log.d(TAG, "startDate: year: $year, month: ${monthOfYear}, day: $dayOfMonth")
                    // Log.d(TAG, "$startDate")
                    dayOfYear = 0
                    var helper = Helper()
                    dayOfYear = helper.calculateDayOfTheYear(monthOfYear, dayOfMonth, year)

                }, year, month, day)
                // TODO: there should be some logic here to move around the maxDate and minDate for the datepicker
                dpd.datePicker.maxDate = today.timeInMillis
                dpd.show()
            }

            /**
             *  Start Time Button onclicklistener
             *   - sets the 24 hour view to false so the user can use AM/PM options
             *   - however, the time is displayed in 24-hour format
             */

            mDialogView.startTimeBtn.setOnClickListener{
                val timeSetListener = TimePickerDialog.OnTimeSetListener{
                        timePicker, hour, minute ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)
                    startTimeHour = hour
                    startTimeMin = minute
                    mDialogView.startTimeTv.text = SimpleDateFormat("HH:mm").format(c.time)
                    startTimeForDb = SimpleDateFormat("HH:mm").format(c.time)
                }

                TimePickerDialog(context, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(
                    Calendar.MINUTE), false).show()
            }

            /**
             * Shift End Date Button onclicklistener
             *  - sets the maxDate to today's date, so that the user cannot pick a future date
             */

            mDialogView.shiftEndDateBtn.setOnClickListener{
                val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    endDateForDb = sdf.format(c.time).toString() // "06/14/2021"
                    mDialogView.shiftEndDateTv.text = sdf.format(c.time)
                    endDate.set(year, monthOfYear, dayOfMonth)
                    // DEBUG TODO: remove this eventually
                    // Log.d(TAG, "endDate: year: $year, month: ${monthOfYear}, day: $dayOfMonth")
                    // Log.d(TAG, "$endDate")
                }, year, month, day)
                dpd.datePicker.maxDate = today.timeInMillis
                dpd.show()
            }

            /**
             * End Time Button on click listener
             *  - sets the 24 hour view to false so the user can use AM/PM options
             *  - however, the time is displayed in 24-hour format
             */

            mDialogView.endTimeBtn.setOnClickListener{
                val timeSetListener = TimePickerDialog.OnTimeSetListener{
                        timePicker, hour, minute ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)
                    endTimeHour = hour
                    endTimeMin = minute
                    mDialogView.endTimeTv.text = SimpleDateFormat("HH:mm").format(c.time)
                    endTimeForDb = SimpleDateFormat("HH:mm").format(c.time)
                }
                TimePickerDialog(context, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(
                    Calendar.MINUTE), false).show()
            }

            /**
             *  Save Button onclick listener
             *   - decides what type of shift we're calculating (same day / overnight)
             *   - verifies that user selected an End Time that is ahead of Start Time if same day shift
             *   - then calculateTotalHours() function calculates the overall hours+min worked in the shift
             *      - calculateHours() function returns a string
             */

            mDialogView.saveBtn.setOnClickListener {
                if (allButtonsClicked(mDialogView)) {
                    when {
                        getShiftType(endDate, startDate) == Constants.OVERNIGHT_SHIFT -> {
                            Log.d(TAG, "overnight shift")
                            val totalTimeWorked = jobRepository.calculateTotalHours(startTimeHour, startTimeMin, endTimeHour + 24, endTimeMin)
                            val shiftsModel = ShiftsModel(1, startDateForDb, dayOfYear, endDateForDb, clickedJobID, startTimeForDb, endTimeForDb, totalTimeWorked )
                            if (jobRepository.shiftExists(shiftsModel)) {
                                Toast.makeText(context, "Shift already exists. Modify current entry or go to shifts tab to view all shifts", Toast.LENGTH_LONG).show()
                            } else {
                                jobRepository.insertShift(shiftsModel)
                                Toast.makeText(context, "⏲ Successfully added overnight shift. Go to shifts tab to view all shifts", Toast.LENGTH_LONG).show()
                                mAlertDialog.dismiss()
                            }
                        }
                        getShiftType(endDate, startDate) == Constants.DAY_SHIFT -> {
                            Log.d(TAG, "day shift")
                            if (checkShiftDuration(endTimeHour, startTimeHour, endTimeMin, startTimeMin)) {
                                val totalTimeWorked = jobRepository.calculateTotalHours(startTimeHour, startTimeMin, endTimeHour, endTimeMin)
                                val shiftsModel = ShiftsModel(1, startDateForDb, dayOfYear, endDateForDb, clickedJobID, startTimeForDb, endTimeForDb, totalTimeWorked )
                                if (jobRepository.shiftExists(shiftsModel)) {
                                    Toast.makeText(context, "Shift already exists. Modify current entry or go to shifts tab to view all shifts", Toast.LENGTH_LONG).show()
                                } else {
                                    jobRepository.insertShift(shiftsModel)
                                    Toast.makeText(context, "⏲ Successfully added day shift. Go to shifts tab to view all shifts", Toast.LENGTH_LONG).show()
                                    mAlertDialog.dismiss()
                                }
                            }
                            else {
                                Toast.makeText(
                                        holder.itemView.context,
                                        "End Time is earlier than Start Time.. Readjust times and try again",
                                        Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        getShiftType(endDate, startDate) == Constants.INVALID_SHIFT_RANGE -> {
                            Toast.makeText(context, "Please choose dates one day apart max!", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill in all missing information", Toast.LENGTH_SHORT).show()
                }
            }

            mDialogView.cancelBtn.setOnClickListener{
                mAlertDialog.cancel()
                // mAlertDialog.dismiss()
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

            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_delete_job, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle(holder.jobName.text)
            val mAlertDialog = mBuilder.show()

            // onClick listener for edit button
            mDialogView.editJobBtn.setOnClickListener{
                mAlertDialog.dismiss()
                openAddJobFragment(holder.jobName, holder.jobLocation)
            }

            // onClick listener for delete button
            mDialogView.deleteJobBtn.setOnClickListener{
                // TODO: maybe add some confirmation before deleting the job, say like 'are you sure?' before deleting it or have the button red so user is warned
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

    private fun checkShiftDuration(endTimeHour: Int, startTimeHour: Int, endTimeMin: Int, startTimeMin: Int): Boolean {
        val validHours = endTimeHour > startTimeHour
        val equalHours = endTimeHour == startTimeHour
        val validMinutes = equalHours && (endTimeMin > startTimeMin)

        return validHours || validMinutes
    }

    private fun allButtonsClicked(mDialogView: View): Boolean {
        val startDateText = mDialogView.shiftStartDateTv.text
        val startTimeText = mDialogView.startTimeTv.text
        val endDateText = mDialogView.shiftEndDateTv.text
        val endTimeText = mDialogView.endTimeTv.text

        // TODO: there should be a better way to compare with initial_date_value variable in strings.xml
        // e.g: val string: String = getString(R.string.hello)
        val initialDateValue = "--/--/----"
        val initialTimeValue = "--:--"

        if (startDateText.equals(initialDateValue) || startTimeText.equals(initialTimeValue) ||
                endDateText.equals(initialDateValue) || endTimeText.equals(initialTimeValue)) {
            return false
        }
        return true
    }

    // TODO: could we move this to jobRepository?
    private fun getShiftType(endDate: Calendar, startDate: Calendar): Int {
        val endDateDay = endDate.get(Calendar.DAY_OF_MONTH)
        val endDateMonth = endDate.get(Calendar.MONTH)
//        val endDateYear = endDate.get(Calendar.YEAR)
        val endDateDayOfWeek = endDate.get(Calendar.DAY_OF_WEEK)

        val startDateDay = startDate.get(Calendar.DAY_OF_MONTH)
        val startDateMonth = startDate.get(Calendar.MONTH)
//        val startDateYear = startDate.get(Calendar.YEAR)
        val startDateDayOfWeek = startDate.get(Calendar.DAY_OF_WEEK)

        // below variables help fix issue when startDate and endDate are at the end of the 7 day cycle and it's either
        // the same month or a different month. we want to ensure the overlap is still exactly one day
        val oneDayOverlapSameMonth = endDateDay - startDateDay == 1
        val oneDayOverlapDiffMonth = (startDateDayOfWeek == 7 && endDateDayOfWeek == 1) && (endDateMonth - startDateMonth == 1)
        val oneDayOverlap = oneDayOverlapSameMonth || oneDayOverlapDiffMonth

        // DEBUG LOGS (uncomment following log lines for more information in logcat
        // Log.d(TAG, "startDateDay: $startDateDay")
        // Log.d(TAG, "startDateMonth: $startDateMonth")
        // Log.d(TAG, "endDateDay: $endDateDay")
        // Log.d(TAG, "endDateMonth: $endDateMonth")
        // Log.d(TAG, "endDateDayOfWeek: $endDateDayOfWeek, startDateDayOfWeek: $startDateDayOfWeek")
        // Log.d(TAG, "----------------------------------------------------------------------------")

        if (((startDateDayOfWeek == 7 && endDateDayOfWeek == 1) && oneDayOverlap)
                || ((endDateDayOfWeek == startDateDayOfWeek + 1) && oneDayOverlap)) {
            // overnight shift
            return Constants.OVERNIGHT_SHIFT
        } else if (endDateDayOfWeek == startDateDayOfWeek) {
            // day shift
            return Constants.DAY_SHIFT
        }

        return Constants.INVALID_SHIFT_RANGE
    }

    // TODO: this might have to go to jobRepository too only if it's needed in another fragment / activity
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
            navController.navigate(R.id.navigationAddJobFragment, args)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, e.printStackTrace().toString())
        } catch (e: Exception) {
            Log.e(TAG, e.printStackTrace().toString())
        }
    }

    override fun getItemCount() = jobList.size

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jobName: TextView = itemView.jobNameTv
        val jobLocation: TextView = itemView.jobLocationTv
        val jobInfo: TextView = itemView.jobInfoTv
        val addHoursButton: Button = itemView.addHoursBtn
    }

    companion object {
        private const val TAG = "HomeAdapter"
    }
}