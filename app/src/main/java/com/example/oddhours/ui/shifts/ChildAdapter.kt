package com.example.oddhours.ui.shifts

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.model.ShiftsListModel
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.data.repository.JobRepository
import com.example.oddhours.ui.home.HomeAdapter
import kotlinx.android.synthetic.main.dialog_edit_delete_job.view.*
import kotlinx.android.synthetic.main.dialog_edit_delete_shift.view.*
import kotlinx.android.synthetic.main.item_shift_detail.view.*

class ChildAdapter(private var shiftsList: List<ShiftsModel>, val context: Context): RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
    private var jobRepository = JobRepository()

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
                openShiftsFragment(holder.itemView.shiftStartTv,holder.itemView.shiftEndTv, holder.itemView.shiftStartHourTv, holder.itemView.shiftEndHourTv)
            }

            mDialogView.deleteShiftBtn.setOnClickListener {
                val shiftModel = ShiftsModel(1, holder.itemView.shiftStartTv.text as String, holder.itemView.shiftEndTv.text as String, 1, holder.itemView.shiftStartHourTv.text as String, holder.itemView.shiftEndHourTv.text as String, "")
                // TODO: wrap delete method in try catch? regular and custom exception needed (JobNotFoundException)
                val isDeleted = jobRepository.deleteIndividualShift(shiftModel)
                if (isDeleted) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Successfully deleted shift",
                        Toast.LENGTH_SHORT
                    ).show()
                    mDialog.dismiss()
//                    shiftsList.removeAt(position)
                    shiftsList = removeItemFromUI(shiftsList, position)
                    notifyDataSetChanged()
                } else {
                    Log.i(TAG, "Error, not able to delete job")
                    mDialog.dismiss()
                }
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = shiftsList.size

    fun openShiftsFragment(shiftStartDate: TextView, shiftEndDate: TextView, shiftStartHour: TextView, shiftEndHour: TextView){

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