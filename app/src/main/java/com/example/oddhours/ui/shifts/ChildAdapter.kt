package com.example.oddhours.ui.shifts

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsModel
import com.example.oddhours.data.repository.JobRepository
import kotlinx.android.synthetic.main.dialog_edit_delete_job.view.*
import kotlinx.android.synthetic.main.dialog_edit_delete_shift.view.*
import kotlinx.android.synthetic.main.item_shift_detail.view.*

class ChildAdapter(private var shiftsList: List<ShiftsModel>, val context: Context, private val navController: NavController): RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
    private var jobRepository = JobRepository()
    private val shiftsFragment = ShiftsFragment()

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
                    shiftsList = removeItemFromUI(shiftsList, position)
                    notifyDataSetChanged()
                    if (shiftsList.isEmpty()) { // we've removed the last item
                        // ideally, we want to notify the parent adapter that we're deleting the last shift for current job and tell it to reload data
                        Log.d(TAG, "deleted the last item")
                        navController.navigate(R.id.navigationShiftsFragment) // temporary workaround: reload the shifts fragment
                    }
                } else {
                    Log.i(TAG, "Error, not able to delete shift")
                    mDialog.dismiss()
                }
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = shiftsList.size

    private fun openShiftsFragment(shiftStartDate: TextView, shiftEndDate: TextView, shiftStartHour: TextView, shiftEndHour: TextView){
        Toast.makeText(context, "Oops!. Edit functionality is not implemented. Please delete the shift and re-enter it again.", Toast.LENGTH_LONG).show()
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