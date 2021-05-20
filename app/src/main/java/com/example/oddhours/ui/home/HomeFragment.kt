package com.example.oddhours.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oddhours.R
import com.example.oddhours.data.repository.JobRepository
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var layoutmanager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<HomeAdapter.JobViewHolder>? = null
    private var jobRepository: JobRepository? = null
    private var hasJobs = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // retrieve jobs
        jobRepository = JobRepository(requireActivity())
        jobRepository!!.buildJobList()

        hasJobs = jobRepository!!.jobModelList!!.isNotEmpty()

        return if (hasJobs) {
            inflater.inflate(R.layout.fragment_home, container, false)
        } else {
            inflater.inflate(R.layout.no_jobs_home, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         *  Call getAllJobs() function in this class to get the JobModelList and pass it to adapter
         *  getAllJobs() is directly passed into HomeAdapter below
         */
        if (hasJobs) {
            recyclerViewHome.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = jobRepository!!.jobModelList?.let { HomeAdapter(it,requireActivity()) }
                // let's keep an eye on the above adapter call, if we experience any weird issues, we can revert to the below one
//                adapter = HomeAdapter(getAllJobs())
            }
        }
    }
// TODO: ideally, this method should be in the JobRepository class and just called from our repository here

    /**
     * Leaving the getAllJobs function code below incase we ever need to reference it back or revert to this
     * approach
     */
//    private fun getAllJobs(): List<JobModel> {
//        val db = DatabaseHelper(requireActivity())
//        jobRepository.jobModelList = db.getJobs()
//        Log.i(TAG, "jobModelList: " + println(jobRepository.jobModelList.toString()))
//
//        hasJobs = jobRepository.jobModelList!!.isNotEmpty()
//        return jobRepository.jobModelList!!
//    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}