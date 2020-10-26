package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import de.sodis.monitoring.default
import de.sodis.monitoring.history
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyHistoryViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*

class SurveyHistoryFragment : BaseListFragment() {


    private val surveyHistoryView: SurveyHistoryViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(SurveyHistoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surveyHistoryView.surveyHistoryList.observe(this, Observer {

            recyclerView.withModels {
                it.forEach {
                    history {
                        id(it.id)
                        firstText(it.surveyName )
                        secondText(it.name)
                        onClick { _ ->
                            val action = SurveyHistoryFragmentDirections.actionMonitoringHistoryFragmentToSurveyOverview(it.id)
                            findNavController().navigate(action)
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
        view?.navigation_forward_button_left?.isGone = true
        return view
    }
}