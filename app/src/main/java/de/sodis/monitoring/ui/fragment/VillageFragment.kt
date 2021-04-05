package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.PlaceViewModel
import de.sodis.monitoring.viewmodel.VillageModel
import kotlinx.android.synthetic.main.continuable_list.view.*


//TODO refactor redundant code
class VillageFragment : BaseListFragment() {
    private val villageModel: VillageModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(VillageModel::class.java)
    }
    private val placeViewModel: PlaceViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(PlaceViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.navigation_forward_button_1?.isGone = true
        view?.navigation_forward_button_left?.isGone = true
        view?.navigation_cancel_button?.isGone = true
        (activity as MainActivity).show_bottom_navigation()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeViewModel.intervieweeItem.observe(viewLifecycleOwner, Observer {intervieweeList ->
            villageModel.villageList.observe(viewLifecycleOwner, Observer {villageList ->
                recyclerView.withModels {
                    villageList.forEach {village ->
                        village {
                            id(village.id)
                            text(village.name)
                            familyCount(intervieweeList.filter { it.villageId == village.id}.size)
                            onClick { clicked ->
                                val action =
                                        VillageFragmentDirections.actionVillageFragmentToIntervieweeOverviewFragment(
                                                village.id!!
                                        )
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
            })

        })
    }

}