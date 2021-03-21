package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.continuable_list.view.*

class MonitoringOverviewFragment : BaseListFragment() {

    val args: MonitoringOverviewFragmentArgs by navArgs()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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