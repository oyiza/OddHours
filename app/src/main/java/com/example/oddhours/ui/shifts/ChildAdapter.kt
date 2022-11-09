package com.example.oddhours.ui.shifts

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.data.repository.JobRepository
import com.example.oddhours.utils.Constants
import com.example.oddhours.utils.Helper
import kotlinx.android.synthetic.main.dialog_add_shift.view.*
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
    private var startDateForDb = ""
    private var endDateForDb = ""
    private var startTimeForDb = ""
    private var endTimeForDb = ""

    /**
     *  The 4 variables below are used for calculating the hours and min worked (together with the dates above - if they're different)
     */
    var startTimeHour = 0
    var startTimeMin = 0
    var endTimeHour = 0
    var endTimeMin = 0

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var dayOfYear = 0

    private val helper = Helper()

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

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
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

                // get values for displaying in the dialog
                startDateForDb = holder.itemView.shiftStartTv.text.toString()
                endDateForDb = holder.itemView.shiftEndTv.text.toString()
                startTimeForDb = holder.itemView.shiftStartHourTv.text.toString()
                endTimeForDb = holder.itemView.shiftEndHourTv.text.toString()

                // TODO: should be able to call the jobrepository from here to get the shiftID
                editShiftID = jobRepository.getShiftID(holder.itemView.shiftStartTv.text as String, holder.itemView.shiftEndTv.text as String, holder.itemView.shiftStartHourTv.text as String, holder.itemView.shiftEndHourTv.text as String)

                val startDate = Calendar.getInstance()
                val endDate = Calendar.getInstance()
                val today = Calendar.getInstance()

                /**
                 * edit shift popup dialog and the respective on button click listeners
                 */
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_shift, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)

                val mAlertDialog = mBuilder.show()

                // display values in the dialog
                mDialogView.shiftStartDateTv.text = startDateForDb
                mDialogView.startTimeTv.text = startTimeForDb
                mDialogView.shiftEndDateTv.text = endDateForDb
                mDialogView.endTimeTv.text = endTimeForDb

                // since we're showing values, the user could potentially not click all the buttons and would hit save.
                // we need to ensure the current values are still stored in the database
                startTimeHour = startTimeForDb.substringBefore(":").toInt()
                startTimeMin = startTimeForDb.substringAfter(":").toInt()
                endTimeHour = endTimeForDb.substringBefore(":").toInt()
                endTimeMin = endTimeForDb.substringAfter(":").toInt()

                startDate.set(startDateForDb.substringAfterLast("/").toInt(), startDateForDb.substringBefore("/").toInt() -1, startDateForDb.substring(3, 5).toInt())
                endDate.set(endDateForDb.substringAfterLast("/").toInt(), endDateForDb.substringBefore("/").toInt() -1, endDateForDb.substring(3, 5).toInt())

                mDialogView.shiftStartDateBtn.setOnClickListener{
                    val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in TextView
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, monthOfYear)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        startDateForDb = sdf.format(calendar.time).toString() // "06/14/2021"
                        mDialogView.shiftStartDateTv.text = sdf.format(calendar.time)
                        startDate.set(year, monthOfYear, dayOfMonth)
                        dayOfYear = 0
                        val helper = Helper()
                        dayOfYear = helper.calculateDayOfTheYear(monthOfYear, dayOfMonth, year)
                        // DEBUG
                        // Log.d(TAG, "startDate: year: $year, month: ${monthOfYear}, day: $dayOfMonth")
                        // Log.d(TAG, "startDate after setting via the button: ${startDate.time}")
                    }, year, month, day)
                    dpd.datePicker.maxDate = today.timeInMillis
                    dpd.show()
                }

                mDialogView.startTimeBtn.setOnClickListener{
                    val timeSetListener = TimePickerDialog.OnTimeSetListener{
                            timePicker, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        startTimeHour = hour
                        startTimeMin = minute
                        mDialogView.startTimeTv.text = SimpleDateFormat("HH:mm").format(calendar.time)
                        startTimeForDb = SimpleDateFormat("HH:mm").format(calendar.time)
                    }

                    TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                        Calendar.MINUTE), false).show()
                    Log.e(TAG, "start hour: $startTimeHour, start minute: $startTimeMin")
                }


                mDialogView.shiftEndDateBtn.setOnClickListener{
                    val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in TextView
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, monthOfYear)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        endDateForDb = sdf.format(calendar.time).toString() // "06/14/2021"
                        mDialogView.shiftEndDateTv.text = sdf.format(calendar.time)
                        endDate.set(year, monthOfYear, dayOfMonth)
                        // DEBUG
                        // Log.d(TAG, "endDate: year: $year, month: ${monthOfYear}, day: $dayOfMonth")
                        // Log.d(TAG, "${endDate.time}")
                    }, year, month, day)
                    dpd.datePicker.maxDate = today.timeInMillis
                    dpd.datePicker.minDate = startDate.timeInMillis
                    dpd.show()
                }


                mDialogView.endTimeBtn.setOnClickListener{
                    val timeSetListener = TimePickerDialog.OnTimeSetListener{
                            timePicker, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        endTimeHour = hour
                        endTimeMin = minute
                        mDialogView.endTimeTv.text = SimpleDateFormat("HH:mm").format(calendar.time)
                        endTimeForDb = SimpleDateFormat("HH:mm").format(calendar.time)
                    }

                    TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                        Calendar.MINUTE), false).show()
                }


                mDialogView.saveBtn.setOnClickListener {
                    if (allButtonsClicked(mDialogView)) {
                        when {
                            jobRepository.getShiftType(endDate, startDate) == Constants.OVERNIGHT_SHIFT -> {
                                Log.d(TAG, "overnight shift")
                                val totalTimeWorked = jobRepository.calculateTotalHours(startTimeHour, startTimeMin, endTimeHour + 24, endTimeMin)
                                val shiftsModel = ShiftsModel(editShiftID, startDateForDb, dayOfYear, endDateForDb, 0, startTimeForDb, endTimeForDb, totalTimeWorked )
                                jobRepository.editShift(shiftsModel, editShiftID)
                                Toast.makeText(context, "⏲ Successfully edited overnight shift", Toast.LENGTH_LONG).show()
                                mAlertDialog.dismiss()
                                notifyDataSetChanged()
                                navController.navigate(R.id.navigationShiftsFragment) // temporary workaround: reload the shifts fragment
                            }
                            jobRepository.getShiftType(endDate, startDate) == Constants.DAY_SHIFT -> {
                                Log.d(TAG, "day shift")
                                if (checkShiftDuration(endTimeHour, startTimeHour, endTimeMin, startTimeMin)) {
                                    val totalTimeWorked = jobRepository.calculateTotalHours(startTimeHour, startTimeMin, endTimeHour, endTimeMin)
                                    val shiftsModel = ShiftsModel(editShiftID, startDateForDb, dayOfYear, endDateForDb, 0, startTimeForDb, endTimeForDb, totalTimeWorked )
                                    jobRepository.editShift(shiftsModel, editShiftID)
                                    Toast.makeText(context, "⏲ Successfully edited day shift.", Toast.LENGTH_LONG).show()
                                    mAlertDialog.dismiss()
                                }
                                else {
                                    Toast.makeText(
                                        holder.itemView.context,
                                        "End Time is earlier than Start Time.. Please try again",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                notifyDataSetChanged()
                                navController.navigate(R.id.navigationShiftsFragment) // temporary workaround: reload the shifts fragment
                            }
                            jobRepository.getShiftType(endDate, startDate) == Constants.INVALID_SHIFT_RANGE -> {
                                Toast.makeText(context, "Please choose dates 1 day apart max!", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all missing information", Toast.LENGTH_SHORT).show()
                    }
                }

                mDialogView.cancelBtn.setOnClickListener{
                    mAlertDialog.cancel()
                }

            }

            mDialogView.deleteShiftBtn.setOnClickListener {
                val shiftModel = ShiftsModel(1, holder.itemView.shiftStartTv.text as String, dayOfYear, holder.itemView.shiftEndTv.text as String, 1, holder.itemView.shiftStartHourTv.text as String, holder.itemView.shiftEndHourTv.text as String, "")
                val isDeleted = jobRepository.deleteIndividualShift(shiftModel)
                if (isDeleted) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Successfully removed shift",
                        Toast.LENGTH_SHORT
                    ).show()
                    mDialog.dismiss()
                    shiftsList = helper.removeShiftItemFromUI(shiftsList, position)
                    notifyDataSetChanged()
                    if (shiftsList.isEmpty()) { // we've removed the last item
                        // ideally, we want to notify the parent adapter that we're deleting the last shift for current job and tell it to reload data
                        Log.d(TAG, "deleted the last item")
                        navController.navigate(R.id.navigationShiftsFragment) // temporary workaround: reload the shifts fragment
                    }
                } else {
                    Toast.makeText(context, "Unable to delete selected shift, something went wrong", Toast.LENGTH_SHORT).show()
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

        // e.g: val string: String = getString(R.string.hello)
        val initialDateValue = "--/--/----"
        val initialTimeValue = "--:--"

        if (startDateText.equals(initialDateValue) || startTimeText.equals(initialTimeValue) ||
            endDateText.equals(initialDateValue) || endTimeText.equals(initialTimeValue)) {
            return false
        }
        return true
    }

    companion object {
        private const val TAG = "ChildAdapter"
    }
}