package com.example.oddhours.ui.addjob

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.oddhours.R
import com.example.oddhours.data.model.JobModel
import com.example.oddhours.data.repository.JobRepository
import com.example.oddhours.utils.Constants
import kotlinx.android.synthetic.main.fragment_add_job.*
import java.util.*

class AddJobFragment : Fragment() {

    private lateinit var dashboardViewModel: AddJobViewModel

    private var jobRepository = JobRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this)[AddJobViewModel::class.java]
        return inflater.inflate(R.layout.fragment_add_job, container, false)
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val locationPattern = Regex("[^0-9A-Za-z, ]")
        val companyPattern = Regex("[^\\+\\'\\ A-Za-z0-9&@-]")

        val args = arguments
        var jobIdToEdit: Int? = null

        if (args != null && !args.isEmpty && args.getBoolean(Constants.CURRENTLY_EDITING_JOB)) { // we're currently editing a job
            Log.d(TAG, args.toString())
            jobIdToEdit = jobRepository.getJobID(args.getString(Constants.JOB_NAME)!!, args.getString(Constants.JOB_LOCATION)!!)
            addJobBtn.visibility = View.GONE
            companyTv.setText(args.getString(Constants.JOB_NAME))
            locationTv.setText(args.getString(Constants.JOB_LOCATION))
        } else { // we're attempting to add a new job
            Log.d(TAG, "args is null or empty")
            saveEditedJobBtn.visibility = View.GONE
        }

        // onClick listener for saveEditedJobBtn
        saveEditedJobBtn.setOnClickListener {
            if (jobIdToEdit != null) {
                val companyName = companyTv.text.toString().replace("'","\'").uppercase(Locale.getDefault())
                val location = locationTv.text.toString().uppercase(Locale.getDefault())
                if (locationPattern.containsMatchIn(location) || location == "") {
                    Toast.makeText(activity, "❌ Please enter a valid location name", Toast.LENGTH_LONG).show()
                }
                else if (companyPattern.containsMatchIn(companyName) || companyName == "") {
                    Toast.makeText(activity, "❌ Please enter a valid company name", Toast.LENGTH_LONG).show()
                }
                else if (jobRepository.jobExists(companyName, location)) {
                    Toast.makeText(activity, "❌ already exists with this name and location.", Toast.LENGTH_LONG).show()
                } else {
                    val jobModel = JobModel(1, companyName, location)
                    val isEdited = jobRepository.editJob(jobModel, jobIdToEdit)
                    if (isEdited) {
                        Toast.makeText(activity,"Successfully edited $companyName", Toast.LENGTH_SHORT).show()
                        hideKeyboard()
                        findNavController().navigate(R.id.navigationHomeFragment)
                    } else {
                        Log.i(TAG, "Error, not able to edit $companyName")
                    }
                }
            } else {
                Log.e(TAG, "job to edit has null ID")
            }
        }

        // onClick listener for addJobBTN
        addJobBtn.setOnClickListener {
            val companyName = companyTv.text.toString().replace("'","\'").uppercase(Locale.getDefault())
            val location = locationTv.text.toString().uppercase(Locale.getDefault())
            val newJob = JobModel(1, companyName, location)

            if (locationPattern.containsMatchIn(location)) {
                Toast.makeText(activity, "❌ Please enter a valid location name", Toast.LENGTH_LONG).show()
            }
            else if (companyPattern.containsMatchIn(companyName)) {
                Toast.makeText(activity, "❌ Please enter a valid company name", Toast.LENGTH_LONG).show()
            }
            else if (companyName.isNotBlank() && location.isNotBlank()) {
                if (!jobRepository.jobExists(newJob.jobName, newJob.jobLocation)) {
                    val addJob = jobRepository.addNewJob(newJob)
                    if (!addJob.equals(-1)) {
                        Toast.makeText(activity, "Successfully added job. Press and hold job card for more options.", Toast.LENGTH_LONG).show()
                        hideKeyboard()
                        findNavController().navigate(R.id.navigationHomeFragment)
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
                }
            } else if (companyName.isEmpty()) {
                Toast.makeText(activity, "Please enter a name for the company", Toast.LENGTH_LONG).show()
            } else if (location.isEmpty()) {
                Toast.makeText(activity, "Please enter a location", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearFields() {
        companyTv.setText("")
        locationTv.setText("")
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val TAG = "AddJobFragment"
    }
}