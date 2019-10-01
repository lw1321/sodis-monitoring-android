package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.R
import de.sodis.monitoring.replaceFragments
import de.sodis.monitoring.ui.adapter.ExpandableRecyclerViewAdapter
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.AutoCompleteHeaderItem
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.interviewee_item.view.*

class SurveyFragment(private val surveyId: Int) : Fragment(), RecyclerViewListener {
    override fun recyclerViewListCLicked(view: View, id: Any) {
        surveyViewModel.setInterviewee(view.multiAutoCompleteTextView.text.toString())
        (activity as MainActivity).replaceFragments(QuestionFragment(surveyId))

    }
    private lateinit var surveyViewModel: SurveyViewModel
    private lateinit var adapter: ExpandableRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surveyViewModel = activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, listOf(surveyId))).get(SurveyViewModel::class.java)

        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list, container, false)
        this.adapter = ExpandableRecyclerViewAdapter(this)
        // Set the adapter
        if (view is RecyclerView) {
            view.adapter = this.adapter
            view.layoutManager = LinearLayoutManager(context)
        }
        surveyViewModel.intervieweeList.observe(this, Observer {
            adapter.setItems(
                mutableListOf(
                    AutoCompleteHeaderItem(
                        title = "Befragter",
                        list = it!!.map { interviewee -> interviewee.name }
                    )
                )
            )
        })

        return view
    }


}
