package com.example.oddhours.ui.addjob

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import com.example.oddhours.database.TableJobs
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

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dbRef = TableJobs()

        // TODO: on the first entry of a job into the db, the keyboard covers the addjobBTN completely
        addjobBTN.setOnClickListener {
            val companyName = companyTV.text.toString().replace("'","\'").toUpperCase()
            val location = locationTV.text.toString().toUpperCase()
            val newJob = JobModel(1, companyName, location)

            // TODO: when typing, navbar is still visible. small issue but might need correcting
            if (companyName != "" && location !="") {
                if (!dbRef.checkJobNameAndJobLocationExists(newJob.jobName, newJob.jobLocation)) {
                    // TODO: should add job be done through the repository instead of the database class? now that we're implementing a singleton db
                    val addJob = dbRef.insertJob(newJob)
                    if (!addJob.equals(-1)) {
                        Toast.makeText(activity, "Successfully added job", Toast.LENGTH_LONG).show()
                        // TODO: hide keyboard before navigating to home page
                        hideKeyboard()
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
//                    clearFields()
                }
            } else if (companyName == "") {
                Toast.makeText(activity, "Please enter a name for the company", Toast.LENGTH_LONG).show()
            } else if (location == "") {
                Toast.makeText(activity, "Please enter a location", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearFields(){
        companyTV.setText("")
        locationTV.setText("")
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}