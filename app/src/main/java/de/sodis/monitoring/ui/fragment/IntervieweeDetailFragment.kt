package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.sodis.monitoring.default
import de.sodis.monitoring.header
import de.sodis.monitoring.keyValue
import de.sodis.monitoring.technology
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.continuable_list.view.*

class IntervieweeDetailFragment(private val intervieweeId: Int) : BaseListFragment() {

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intervieweeModel.intervieweeDetail.observe(this, Observer {
            recyclerView.withModels {
                header {
                    id("header")
                    text(it.interviewee.name)
                }
                keyValue {
                    id("keyValue")
                    key("village")
                    value(it.village.name)
                }
                keyValue {
                    id("keyValue")
                    key("miembro de la familia")
                    value(
                        (it.interviewee.boysCount
                                + it.interviewee.girlsCount
                                + it.interviewee.youngMenCount
                                + it.interviewee.youngWomenCount
                                + it.interviewee.womenCount
                                + it.interviewee.menCount
                                + it.interviewee.oldWomenCount
                                + it.interviewee.oldMenCount).toString()
                    )
                }
                it.intervieweeTechnologies.forEach { techno ->
                    //are there open tasks for this technology?
                    val taskFilteredList = it.tasks.filter { task ->
                        task.intervieweeTechnologyId == techno.id
                    }
                    var taskStatus = "Nothing to do"
                    if (taskFilteredList.isNotEmpty()) {
                        taskStatus = taskFilteredList.first().name!!
                    }
                    taskFilteredList ?: "All good"
                    technology {
                        id("technology")
                        state(techno.stateTechnology.toString())
                        knowledgeState(techno.stateKnowledge.toString())
                        name(techno.name)
                        taskName(taskStatus)
                    }
                }

            }
        })
        intervieweeModel.setInterviewee(intervieweeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.navigation_forward_button_1?.isGone = true
        return view
    }
}
