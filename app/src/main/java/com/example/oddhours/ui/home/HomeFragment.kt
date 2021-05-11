package com.example.oddhours.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.repository.JobRepository
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var layoutmanager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<HomeAdapter.JobViewHolder>? = null
    private var jobRepository: JobRepository = JobRepository()
    private var testList = generateDummyList(0)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return if (testList.isNotEmpty()) {
            inflater.inflate(R.layout.fragment_home, container, false)
        } else {
            inflater.inflate(R.layout.no_jobs_view, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (testList.isEmpty()) {
            // do nothing. layout is already inflated
        } else {
            recyclerViewHome.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = HomeAdapter(testList);
            }
        }
    }

    /**
     * using a helper method to generate a dummy list for testing purposes.
     * when we eventually have the DB setup, we should have a helper function that grabs the data from the DB
     * and returns it in a List<JobModel>
     */
    private fun generateDummyList(size: Int): List<JobModel> {
        Log.i(TAG, "generating dummy list...")
        val list = ArrayList<JobModel>()

        for (i in 1 until size+1) {
            val item = jobRepository.buildJobList("Job number $i", "Location, LO")
            list += item
        }

        return list
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}