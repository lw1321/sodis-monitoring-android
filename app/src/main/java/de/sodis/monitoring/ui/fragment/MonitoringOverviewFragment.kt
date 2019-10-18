package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.default
import de.sodis.monitoring.header
import de.sodis.monitoring.replaceFragments
import de.sodis.monitoring.viewmodel.MonitoringOverviewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory

class MonitoringOverviewFragment : BaseListFragment() {


    private val monitoringOverviewModel: MonitoringOverviewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(MonitoringOverviewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        monitoringOverviewModel.surveyHeaderList.observe(this, Observer {

            recyclerView.withModels {
                header {
                    id("header")
                    text("Cuestionario")
                }

                it.forEach {
                    default {
                        id(it.id)
                        text(it.surveyName)
                        onClick { _ ->
                            (activity as MainActivity).replaceFragments(SurveyFragment(it.id))
                        }
                    }
                }

            }

        })

    }

}