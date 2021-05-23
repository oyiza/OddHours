package com.example.oddhours.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.database.TableJobs
import com.example.oddhours.database.TableShifts
import kotlinx.android.synthetic.main.addshift.view.*
import kotlinx.android.synthetic.main.job_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * HomeAdapter now also takes in the context as a parameter in the constructor
 * context is required for the Alert Dialog
 */

class HomeAdapter(private val jobList: List<JobModel>, val context: Context) : RecyclerView.Adapter<HomeAdapter.JobViewHolder>() {

    val MyFormat = "MM/dd/yyyy"
    val sdf = SimpleDateFormat(MyFormat, Locale.CANADA)

    /**
     *  The 3 variables below are used for insert these values in SQLite
     */
    var dateForDb = ""
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
            // TODO: button click should lead to new add hours fragment or activity
            Log.i(TAG, "clicked button of : ${holder.jobName.text}")
            Toast.makeText(
                holder.itemView.context,
                "You clicked button of : ${holder.jobName.text}",
                Toast.LENGTH_SHORT
            ).show()

            val clickedJobID = TableJobs().getJobID(holder.jobName.text.toString(), holder.jobLocation.text.toString())

            /**
             * below code is for popup dialog and the respective on button click listeners
             */
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.addshift, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("Add a Shift")
                val mAlertDialog = mBuilder.show()
                mDialogView.shiftDateTV.text = "${month+1}/${day}/${year}"

            /**
             * Shift Date Button onclicklistener
             *  - sets the maxDate to today's date, so that the user cannot pick a future date
             */

            mDialogView.shiftDateBTN.setOnClickListener{
                val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    dateForDb = sdf.format(c.time).toString()
                    mDialogView.shiftDateTV.setText(sdf.format(c.time))
                }, year, month, day)
                dpd.datePicker.maxDate = c.timeInMillis
                dpd.show()
            }

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

            mDialogView.saveBTN.setOnClickListener {
                if(endTimeHour > startTimeHour){
                    val totalTimeWorked = calculateTotalHours(startTimeHour, startTimeMin, endTimeHour, endTimeMin)
                    // TODO: shiftRepository class for creating new shifts?
                    val shiftsModel = ShiftsModel(1,dateForDb,clickedJobID,startTimeForDb,endTimeForDb,totalTimeWorked )
                    var insertShift = TableShifts().insertShift(shiftsModel)
                    println("PRINTING ALL SHIFTS")
                    println("---------------------")
                    println(TableShifts().getShifts())
                    mAlertDialog.dismiss()
                }
                else{
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
        }

        // long click listener for the card
        holder.itemView.setOnLongClickListener{
            Toast.makeText(holder.itemView.context, "Long click detected on ${holder.jobName.text}", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = jobList.size

    // TODO: another chunk to be moved into shiftRepository class perhaps?
    fun calculateTotalHours(stHour: Int, stMin: Int, etHour: Int, etMin: Int): String{
        val hoursWorked = etHour - stHour
        val minutesWorked: Int
        val totalTimeWorked: String

        if(etMin > stMin) {
            minutesWorked = etMin - stMin
            totalTimeWorked = hoursWorked.toString()+"h "+minutesWorked.toString()+"m"
        }
        else{
            minutesWorked = stMin - etMin
            totalTimeWorked = hoursWorked.toString()+"h "+minutesWorked.toString()+"m"
        }
        return totalTimeWorked
    }

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