package de.sodis.monitoring.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.tabs.TabLayout
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.VillageModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.list.view.*
import kotlinx.android.synthetic.main.view_holder_default.*
import kotlinx.android.synthetic.main.view_holder_picture_list_item.view.imageView
import kotlinx.android.synthetic.main.view_holder_tab.*


//TODO refactor redundant code
class VillageFragment : BaseListFragment() {
    private val villageModel: VillageModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(VillageModel::class.java)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        villageModel.villageList.observe(viewLifecycleOwner, Observer {
            recyclerView.withModels {
                it.forEach {
                    default {
                        id(it.id)
                        text(it.name)
                        onClick { clicked ->
                            (activity as MainActivity).hide_bottom_navigation()
                            val action =
                                VillageFragmentDirections.actionVillageFragmentToIntervieweeOverviewFragment(
                                    it.id!!
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        })
    }
}