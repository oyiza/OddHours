package com.example.oddhours.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.oddhours.R
import com.example.oddhours.data.repository.JobRepository
import im.dacer.androidcharts.BarView
import im.dacer.androidcharts.LineView


class ChartsFragment : Fragment() {

    private lateinit var chartsViewModel: ChartsViewModel
    private var jobRepository = JobRepository()

    private val DEFAULT = "Choose Chart Type"
    private val LINE = "Line Chart"
    private val BAR = "Bar Chart"

    // onCreateView, grab all the data we want to work with for the diff types of charts and load a default view chart?
    // then present a drop down option for the user to select what type of chart they want to see
    // depending on the choice of chart, call the function that will load the layout with the chart
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartsViewModel =
                ViewModelProvider(this).get(ChartsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_charts, container, false)

        // dropdown menu
        val chartTypes = resources.getStringArray(R.array.chart_types)
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, chartTypes)
        val chartDropdownTV = root.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        chartDropdownTV.setAdapter(arrayAdapter)

        chartDropdownTV.setOnItemClickListener{ parent, view, position, id ->
            if (chartDropdownTV != null) {
                Log.d(TAG, chartDropdownTV.text.toString())

                when (chartDropdownTV.text.toString()) {
                    DEFAULT -> hideOtherCharts(root, DEFAULT)
                    LINE -> buildLineChart(root)
                    BAR -> buildBarChart(root)
                    else -> {
                        Log.e(TAG, "something went wrong, selected item isn't accounted for.")
                    }
                }
            }
        }

        return root
    }

    private fun buildBarChart(root: View) {
        Log.d(TAG, "buildBarChart() called")
        hideOtherCharts(root, BAR)

        val strList = arrayListOf("test1", "test2", "test3", "test4", "test5", "test6")
        val dataList = arrayListOf(1,2,3,4,5,6)

        val barView = root.findViewById(R.id.bar_view) as BarView
        barView.setBottomTextList(strList)
        barView.setDataList(dataList, 100)
    }

    private fun buildLineChart(root: View) {
        Log.d(TAG, "buildLineChart() called")
        hideOtherCharts(root, LINE)

        val strList = getJobNameAndLocationForDisplay() // list of job names
        val dataList1: ArrayList<Int> = getTotalHoursListForLine() // list of hours worked
        val dataLists = arrayListOf(dataList1)

        val lineView = root.findViewById(R.id.line_view) as LineView
        lineView.setDrawDotLine(false) //optional

        lineView.setShowPopup(LineView.SHOW_POPUPS_All) //optional

        lineView.setBottomTextList(strList)
        lineView.setColorArray(intArrayOf(Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN))

        lineView.setDataList(dataLists)
    }

    // should be a better way of doing this but we're hiding other charts individually
    // LINE, BAR
    private fun hideOtherCharts(root: View, chartType: String) {
        // LINE
        if (LINE != chartType) {
            val chartView = root.findViewById(R.id.line_view) as LineView
            chartView.visibility = View.GONE
        } else {
            val chartView = root.findViewById(R.id.line_view) as LineView
            chartView.visibility = View.VISIBLE
        }

        // BAR
        if (BAR != chartType) {
            val chartView = root.findViewById(R.id.bar_view) as BarView
            chartView.visibility = View.GONE
        } else {
            val chartView = root.findViewById(R.id.bar_view) as BarView
            chartView.visibility = View.VISIBLE
        }
    }

    private fun getJobNameAndLocationForDisplay(): ArrayList<String> {
        val jobNameAndLocation: MutableList<String> = ArrayList()

        for (job in jobRepository.jobModelList!!) {
            jobNameAndLocation.add(job.jobName + ", " + job.jobLocation)
        }

        return jobNameAndLocation.toCollection(ArrayList())
    }

    private fun getTotalHoursListForLine(): ArrayList<Int> {
        val tempJobHours: MutableList<Int> = ArrayList()
        for (job in jobRepository.jobModelList!!) {
            tempJobHours.add(jobRepository.getTotalHoursForJobAsInt(job))
        }
        Log.d(TAG, "displaying tempJobHours")
        Log.d(TAG, tempJobHours.toCollection(ArrayList()).toString())

        return tempJobHours.toCollection(ArrayList())
    }

    companion object {
        private const val TAG = "ChartsFragment"
    }
}