package com.example.oddhours.ui.shifts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsListModel
import kotlinx.android.synthetic.main.shifts_row.view.*

class ParentAdapter(private val allShifts: List<ShiftsListModel>, val context: Context, val navController: NavController): RecyclerView.Adapter<ParentAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView){
        fun bindShifts(item: ShiftsListModel){
            itemView.jobTV.text = item.jobInfo.jobTitle + ", "+item.jobInfo.jobLocation
            itemView.shiftsRV.apply{
                layoutManager = LinearLayoutManager(shiftsRV.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = ChildAdapter(item.shifts,context)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.shifts_row, parent, false)
        return ViewHolder(v, context)
    }

    override fun onBindViewHolder(holder: ParentAdapter.ViewHolder, position: Int) {
        val ShiftsItem = allShifts[position]
        holder.bindShifts(ShiftsItem)
    }
    override fun getItemCount() = allShifts.size
}