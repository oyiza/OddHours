package com.example.oddhours.ui.shifts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oddhours.R
import com.example.oddhours.data.model.ShiftsListModel
import com.example.oddhours.data.repository.JobRepository
import kotlinx.android.synthetic.main.fragment_shifts.*
import java.lang.Exception


class ShiftsFragment : Fragment() {

    private var jobRepository = JobRepository()
    private lateinit var shiftsForAdapter: List<ShiftsListModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try{
            shiftsForAdapter = jobRepository.getShiftsForUIList()
        }catch (e: Exception){
            println(e)
        }
        return inflater.inflate(R.layout.fragment_shifts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewShifts.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = shiftsForAdapter.let { ParentAdapter(it, requireActivity(), view.findNavController()) }
        }
    }
}