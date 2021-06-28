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
import kotlinx.android.synthetic.main.item_shifts.view.*

class ParentAdapter(private val allShifts: List<ShiftsListModel>, val context: Context, private val navController: NavController): RecyclerView.Adapter<ParentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, val context: Context, private val navController: NavController): RecyclerView.ViewHolder(itemView) {
        fun bindShifts(item: ShiftsListModel){
            itemView.jobTitleTv.text = item.jobInfo.jobName + ", "+item.jobInfo.jobLocation
            itemView.childRv.apply{
                layoutManager = LinearLayoutManager(childRv.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = ChildAdapter(item.shifts.asReversed(), context, navController)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_shifts, parent, false)
        return ViewHolder(v, context, navController)
    }

    override fun onBindViewHolder(holder: ParentAdapter.ViewHolder, position: Int) {
        val shiftsList = allShifts[position]
        holder.bindShifts(shiftsList)
    }

    override fun getItemCount() = allShifts.size
}