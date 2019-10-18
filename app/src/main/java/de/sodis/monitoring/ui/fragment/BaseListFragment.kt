package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import kotlinx.android.synthetic.main.list.view.*


abstract class BaseListFragment : Fragment() {

    lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(de.sodis.monitoring.R.layout.continuable_list, container, false)

        recyclerView = view.list
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

}
