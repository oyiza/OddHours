package com.example.oddhours.ui.shifts

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.ui.home.HomeAdapter
import kotlinx.android.synthetic.main.dialog_edit_delete_job.view.*
import kotlinx.android.synthetic.main.item_shift_detail.view.*

class ChildAdapter(private var shiftsList: List<ShiftsModel>, val context: Context): RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
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
            mBuilder.show()

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = shiftsList.size
}