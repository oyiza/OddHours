package com.example.oddhours.ui.shifts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsModel
import kotlinx.android.synthetic.main.shifts_detail.view.*

class ChildAdapter(private var shiftsList: List<ShiftsModel>, val context: Context): RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {
        fun bindShifts(items: ShiftsModel){
            itemView.shiftID.text = items.shiftStartDate
            itemView.shiftEndDate.text = items.shiftEndDate
            itemView.shiftStart.text = items.startTime
            itemView.shiftEnd.text = items.endTime
            itemView.hoursWorked.text = items.hoursWorked
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.shifts_detail, parent,false)
        return ViewHolder(v, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shifts = shiftsList[position]
        holder.bindShifts(shifts)
    }

    override fun getItemCount() = shiftsList.size
}