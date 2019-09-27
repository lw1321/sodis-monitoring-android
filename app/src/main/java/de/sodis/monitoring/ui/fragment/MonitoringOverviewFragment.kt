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
import de.sodis.monitoring.ui.adapter.RecyclerViewListerner
import de.sodis.monitoring.ui.model.DefaultParentItem
import de.sodis.monitoring.viewmodel.MonitoringOverviewModel

class MonitoringOverviewFragment : Fragment(), RecyclerViewListerner {
    override fun recyclerViewListCLicked(view: View, id: Any) {
        print(id)
        (activity as MainActivity).replaceFragments(SurveyFragment())
        //open survey
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    private lateinit var monitoringOverviewModel: MonitoringOverviewModel
    private lateinit var adapter: ExpandableRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        monitoringOverviewModel = activity?.run {
            ViewModelProviders.of(this).get(MonitoringOverviewModel::class.java)
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_surveys_overview, container, false)
        this.adapter = ExpandableRecyclerViewAdapter(this)
        // Set the adapter
        if (view is RecyclerView) {
            view.adapter = this.adapter
            view.layoutManager = LinearLayoutManager(context)
        }
        monitoringOverviewModel.surveyHeaderList.observe(this, Observer {
            adapter.setItems(it!!.map { header -> DefaultParentItem(title = header.surveyName)})
        })
        return view
    }

}
