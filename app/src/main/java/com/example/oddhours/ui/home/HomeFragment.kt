package com.example.oddhours.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.repository.JobRepository
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var layoutmanager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<HomeAdapter.JobViewHolder>? = null
    private var jobRepository = JobRepository()
    private var hasJobs = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // retrieve jobs
        try {
            jobRepository.buildJobList()
            hasJobs = jobRepository.jobModelList!!.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, e.printStackTrace().toString())
        }

        return if (hasJobs) {
            inflater.inflate(R.layout.fragment_home, container, false)
        } else {
            inflater.inflate(R.layout.item_no_job, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasJobs) {
            homeRv.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = jobRepository.jobModelList?.let { HomeAdapter(it, requireActivity(), view.findNavController()) }
                // let's keep an eye on the above adapter call, if we experience any weird issues, we can revert to the below one
                // adapter = HomeAdapter(getAllJobs())
            }
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}