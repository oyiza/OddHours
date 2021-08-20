package com.example.oddhours.ui.shifts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsListModel
import com.example.oddhours.data.repository.JobRepository
import kotlinx.android.synthetic.main.fragment_shifts.*


class ShiftsFragment : Fragment() {

    private var jobRepository = JobRepository()
    private lateinit var shiftsForAdapter: List<ShiftsListModel>
    private var hasShifts = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try{
            shiftsForAdapter = jobRepository.getShiftsForUIList()
            hasShifts = shiftsForAdapter.isNotEmpty()
        }catch (e: Exception){
            Log.e(TAG, e.printStackTrace().toString())
        }
        return if (hasShifts) {
            inflater.inflate(R.layout.fragment_shifts, container, false)
        } else {
            inflater.inflate(R.layout.item_no_shift, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasShifts) {
            parentRv.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = shiftsForAdapter.let { ParentAdapter(it, requireActivity(), view.findNavController()) }
            }
        }
    }

    companion object {
        private const val TAG = "ShiftsFragment"
    }
}