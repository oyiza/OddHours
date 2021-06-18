package com.example.oddhours.ui.shifts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsModel
import kotlinx.android.synthetic.main.dialog_add_shift.view.*
import kotlinx.android.synthetic.main.item_shift_detail.view.*

class ChildAdapter(private var shiftsList: List<ShiftsModel>, val context: Context): RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {
        fun bindShifts(items: ShiftsModel){
            itemView.shiftStartTV.text = items.shiftStartDate
            itemView.shiftEndTV.text = items.shiftEndDate
            itemView.shiftStartHourTV.text = items.startTime
            itemView.shiftEndHourTV.text = items.endTime
            itemView.hoursWorkedTV.text = items.hoursWorked
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_shift_detail, parent,false)
        return ViewHolder(v, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shifts = shiftsList[position]
        holder.bindShifts(shifts)
    }

    override fun getItemCount() = shiftsList.size
}