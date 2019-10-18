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
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.replaceFragments
import de.sodis.monitoring.ui.adapter.ExpandableRecyclerViewAdapter
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.DefaultParentItem
import de.sodis.monitoring.ui.model.HeaderItem
import de.sodis.monitoring.ui.model.SodisItem
import de.sodis.monitoring.viewmodel.MonitoringOverviewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory

class MonitoringOverviewFragment : Fragment(), RecyclerViewListener {
    override fun recyclerViewListCLicked(view: View, id: Any) {
       (activity as MainActivity).replaceFragments(SurveyFragment(headerList[id as Int - 1].id))
        //open survey
    }

    private lateinit var headerList: List<SurveyHeader>
    private lateinit var monitoringOverviewModel: MonitoringOverviewModel
    private lateinit var adapter: ExpandableRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        monitoringOverviewModel = activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, emptyList())).get(MonitoringOverviewModel::class.java)
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list, container, false)
        this.adapter = ExpandableRecyclerViewAdapter(this)
        // Set the adapter
        if (view is RecyclerView) {
            view.adapter = this.adapter
            view.layoutManager = LinearLayoutManager(context)
        }
        monitoringOverviewModel.surveyHeaderList.observe(this, Observer {
            headerList = it
            val tempList = mutableListOf<SodisItem>()
            tempList.add(0, HeaderItem(title = "Cuestionario"))
            tempList.addAll(it!!.map { header -> DefaultParentItem(title = header.surveyName) })
            adapter.setItems(tempList)
        })
        return view
    }

}
