package com.example.oddhours.ui.shifts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.data.repository.JobRepository
import com.example.oddhours.ui.home.HomeAdapter
import com.example.oddhours.utils.Constants
import com.example.oddhours.utils.Helper
import kotlinx.android.synthetic.main.dialog_add_shift.view.*
import kotlinx.android.synthetic.main.dialog_edit_delete_job.view.*
import kotlinx.android.synthetic.main.dialog_edit_delete_shift.view.*
import kotlinx.android.synthetic.main.item_shift_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChildAdapter(private var shiftsList: List<ShiftsModel>, val context: Context, private val navController: NavController): RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
    private var jobRepository = JobRepository()
    private val shiftsFragment = ShiftsFragment()

    private val myFormat = "MM/dd/yyyy"
    private val sdf = SimpleDateFormat(myFormat, Locale.CANADA)
    private var editShiftID = -1

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

    var dayOfYear = 0

    class ViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {
        fun bindShifts(items: ShiftsModel){
            itemView.shiftStartTv.text = items.shiftStartDate
            itemView.shiftEndTv.text = items.shiftEndDate
            itemView.shiftStartHourTv.text = items.startTime
            itemView.shiftEndHourTv.text = items.endTime
            itemView.hoursWorkedTv.text = items.hoursWorked
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_shift_detail, parent,false)
        return ViewHolder(v, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shifts = shiftsList[position]
        holder.bindShifts(shifts)
        holder.itemView.setOnLongClickListener{
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_delete_shift, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
            val mDialog = mBuilder.show()

            mDialogView.editShiftBtn.setOnClickListener{
                mDialog.dismiss()
                editShiftID = getShiftID(holder.itemView.shiftStartTv.text as String, holder.itemView.shiftEndTv.text as String, holder.itemView.shiftStartHourTv.text as String, holder.itemView.shiftEndHourTv.text as String)

                // TODO: at the beginning, startDate and endDate are always the same because of similar initialization
                val startDate = Calendar.getInstance()
                val endDate = Calendar.getInstance()
                val today = Calendar.getInstance()

                /**
                 * below code is for popup dialog and the respective on button click listeners
                 */

                val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_shift, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("Edit Shift")
                val mAlertDialog = mBuilder.show()

                mDialogView.shiftStartDateBtn.setOnClickListener{
                    val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in TextView
                        c.set(Calendar.YEAR, year)
                        c.set(Calendar.MONTH, monthOfYear)
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        startDateForDb = sdf.format(c.time).toString() // "06/14/2021"
                        mDialogView.shiftStartDateTv.text = sdf.format(c.time)
                        startDate.set(year, monthOfYear, dayOfMonth)
                        dayOfYear = 0
                        var helper = Helper()
                        dayOfYear = helper.calculateDayOfTheYear(monthOfYear, dayOfMonth, year)
                        // DEBUG TODO: remove this eventually
                        // Log.d(TAG, "startDate: year: $year, month: ${monthOfYear}, day: $dayOfMonth")
                        // Log.d(TAG, "$startDate")
                    }, year, month, day)
                    // TODO: there should be some logic here to move around the maxDate and minDate for the datepicker
                    dpd.datePicker.maxDate = today.timeInMillis
                    dpd.show()
                }

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


                mDialogView.saveBtn.setOnClickListener {
                    if (allButtonsClicked(mDialogView)) {
                        when {
                            getShiftType(endDate, startDate) == Constants.OVERNIGHT_SHIFT -> {
                                Log.d(TAG, "overnight shift")
                                val totalTimeWorked = jobRepository.calculateTotalHours(startTimeHour, startTimeMin, endTimeHour + 24, endTimeMin)
                                val shiftsModel = ShiftsModel(editShiftID, startDateForDb, dayOfYear, endDateForDb, 0, startTimeForDb, endTimeForDb, totalTimeWorked )
                                jobRepository.editShift(shiftsModel, editShiftID)
                                Toast.makeText(context, "⏲ Successfully edited shift", Toast.LENGTH_LONG).show()
                                mAlertDialog.dismiss()
//                                shiftsList = removeItemFromUI(shiftsList, position)
                                notifyDataSetChanged()
                                navController.navigate(R.id.navigationShiftsFragment) // temporary workaround: reload the shifts fragment
                            }
                            getShiftType(endDate, startDate) == Constants.DAY_SHIFT -> {
                                Log.d(TAG, "day shift")
                                if (checkShiftDuration(endTimeHour, startTimeHour, endTimeMin, startTimeMin)) {
                                    val totalTimeWorked = jobRepository.calculateTotalHours(startTimeHour, startTimeMin, endTimeHour, endTimeMin)
                                    val shiftsModel = ShiftsModel(editShiftID, startDateForDb, dayOfYear, endDateForDb, 0, startTimeForDb, endTimeForDb, totalTimeWorked )
                                    jobRepository.editShift(shiftsModel, editShiftID)
                                    Toast.makeText(context, "⏲ Successfully edited shift.", Toast.LENGTH_LONG).show()
                                    mAlertDialog.dismiss()
                                }
                                else {
                                    Toast.makeText(
                                        holder.itemView.context,
                                        "End Time is earlier than Start Time.. Readjust times and try again",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                shiftsList = removeItemFromUI(shiftsList, position)
                                notifyDataSetChanged()
                                navController.navigate(R.id.navigationShiftsFragment) // temporary workaround: reload the shifts fragment
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

            mDialogView.deleteShiftBtn.setOnClickListener {
                val shiftModel = ShiftsModel(1, holder.itemView.shiftStartTv.text as String, dayOfYear, holder.itemView.shiftEndTv.text as String, 1, holder.itemView.shiftStartHourTv.text as String, holder.itemView.shiftEndHourTv.text as String, "")
                val isDeleted = jobRepository.deleteIndividualShift(shiftModel)
                if (isDeleted) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Successfully deleted shift",
                        Toast.LENGTH_SHORT
                    ).show()
                    mDialog.dismiss()
                    shiftsList = removeItemFromUI(shiftsList, position)
                    notifyDataSetChanged()
                    if (shiftsList.isEmpty()) { // we've removed the last item
                        // ideally, we want to notify the parent adapter that we're deleting the last shift for current job and tell it to reload data
                        Log.d(TAG, "deleted the last item")
                        navController.navigate(R.id.navigationShiftsFragment) // temporary workaround: reload the shifts fragment
                    }
                } else {
                    Toast.makeText(context, "Unable to delete selected shift, something went wrong.", Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "Error, not able to delete shift")
                    mDialog.dismiss()
                }
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = shiftsList.size

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

    private fun getShiftID(shiftStartDate: String, shiftEndDate: String, shiftStartTime: String, shiftEndTime: String): Int{
        return jobRepository.getShiftID(shiftStartDate, shiftEndDate, shiftStartTime, shiftEndTime)
    }

    private fun removeItemFromUI(list: List<ShiftsModel>, position: Int): List<ShiftsModel> {
        Log.i(TAG, "removeItem() called: position is $position")
        val result = list.toMutableList()
        result.removeAt(position)
        return result.toList()
    }

    companion object {
        private const val TAG = "ChildAdapter"
    }
}