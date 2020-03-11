package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.default
import de.sodis.monitoring.header
import de.sodis.monitoring.replaceFragments
import de.sodis.monitoring.viewmodel.MonitoringOverviewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*

class TaskOverviewFragment : BaseListFragment() {


    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(TaskViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskViewModel.taskList.observe(this, Observer {

            recyclerView.withModels {
                it.forEach {
                    default {
                        id(it.id)
                        text(it.name)
                        onClick { _ ->
                            (activity as MainActivity).replaceFragments(SurveyFragment(it.id), "SURVEY_TAG")
                        }
                    }
                }

            }

        })

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