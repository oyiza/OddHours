package com.example.oddhours.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import kotlinx.android.synthetic.main.job_row.view.*

class HomeAdapter(private val jobList: List<JobModel>) : RecyclerView.Adapter<HomeAdapter.JobViewHolder>() {

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
            // Todo: button click should lead to new add hours fragment or activity
            Toast.makeText(
                holder.itemView.context,
                "You clicked button of : ${holder.jobName.text}",
                Toast.LENGTH_LONG
            ).show()
        }

        // onClick listener for the card - in case we want to click the card and open the job details
        holder.itemView.setOnClickListener{
            Toast.makeText(
                holder.itemView.context,
                "You clicked card number ${holder.jobName.text[holder.jobName.text.lastIndex]}",
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
}