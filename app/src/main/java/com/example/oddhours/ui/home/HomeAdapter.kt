package com.example.oddhours.ui.home

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
import kotlinx.android.synthetic.main.addshift.view.*
import kotlinx.android.synthetic.main.job_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * HomeAdapter now also takes in the context as a parameter in the constructor
 * context is required for the Alert Dialog
 */

class HomeAdapter(private val jobList: List<JobModel>, val context: Context) : RecyclerView.Adapter<HomeAdapter.JobViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_row, parent, false)

        return JobViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val currentItem = jobList[position]

        holder.jobName.text = currentItem.jobName
        holder.jobLocation.text = currentItem.jobLocation
        holder.jobInfo.text = currentItem.jobInfo

        // this also works, but don't use this because it calls findViewById() under the hood over and over every time we need
        // to bind viewHolder
        // holder.itemView.txtJobName.text = currentItem.jobName

        // onClick listener for the add hours button
        holder.addHoursButton.setOnClickListener {
            // TODO: button click should lead to new add hours fragment or activity
            Log.i(TAG, "clicked button of : ${holder.jobName.text}")
            Toast.makeText(
                holder.itemView.context,
                "You clicked button of : ${holder.jobName.text}",
                Toast.LENGTH_LONG
            ).show()

            /**
             * below code is for popup dialog and the respective on button click listeners
             */
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.addshift, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("Add a Shift")
                val mAlertDialog = mBuilder.show()

                mDialogView.startTimeBTN.setOnClickListener{
                    val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener{
                            timePicker, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        mDialogView.startTimeTV.text = SimpleDateFormat("HH:mm").format(cal.time)
                    }

                    TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                        Calendar.MINUTE), false).show()
                }

                mDialogView.endTimeBTN.setOnClickListener{
                    val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener{
                            timePicker, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        mDialogView.endTimeTV.text = SimpleDateFormat("HH:mm").format(cal.time)
                    }

                    TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                        Calendar.MINUTE), false).show()
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
                Toast.LENGTH_LONG
            ).show()
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