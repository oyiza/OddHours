package com.example.oddhours.ui.home

import android.os.Bundle
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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testList = generateDummyListUsingModels(10)

        recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = HomeAdapter(testList);
        }
    }

    /**
     * using a helper method to generate a dummy list for testing purposes.
     * when we eventually have the DB setup, we should have a helper function that grabs the data from the DB
     * and returns it in a List<JobModel>
     */
//    private fun generateDummyList(size: Int): List<JobModel> {
//        val list = ArrayList<JobModel>()
//
//        for (i in 1 until size+1) {
//            val item = JobModel("Job number $i", "Location, LO", "This week: X hours")
//            list += item
//        }
//
//        return list
//    }

    private fun generateDummyListUsingModels(size: Int): List<JobModel> {
        val list = ArrayList<JobModel>()

        for (i in 1 until size+1) {
            val item = jobRepository.createJob("Job number $i", "Location, LO")
            list += item
        }

        return list
    }
}