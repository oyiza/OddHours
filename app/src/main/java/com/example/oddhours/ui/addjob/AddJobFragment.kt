package com.example.oddhours.ui.addjob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.database.DatabaseHelper
import kotlinx.android.synthetic.main.fragment_addjob.*

class AddJobFragment : Fragment() {

    private lateinit var dashboardViewModel: AddJobViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(AddJobViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_addjob, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var db = DatabaseHelper(requireActivity())

        addjobBTN.setOnClickListener {
            var companyName = companyTV.text.toString().replace("'","\'").toUpperCase()
            var location = locationTV.text.toString().toUpperCase()
            var newJob = JobModel(1, companyName, location)

            // TODO: when job already exists, don't just return to homepage, allow user to edit before saving / canceling
            if(companyName != "" && location !="") {
                if (!db.checkJobNameAndJobLocationExists(newJob.jobName, newJob.jobLocation)) {
                    var addJob = db.insertJob(newJob)
                    if (!addJob.equals(-1)) {
                        Toast.makeText(activity, "Successfully added job", Toast.LENGTH_LONG).show()
                        findNavController().navigate(
                            R.id.navigation_home
                        )
                    } else {
                        Toast.makeText(activity, "Failed to add job", Toast.LENGTH_LONG).show()
                        clearFields()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Job with this Name & Location exists already",
                        Toast.LENGTH_LONG
                    ).show()
                    clearFields()
                }
            }
            else{
                Toast.makeText(activity, "Please enter a name & location", Toast.LENGTH_LONG).show()
                clearFields()
            }
        }
    }

    private fun clearFields(){
        companyTV.setText("")
        locationTV.setText("")

    }
}