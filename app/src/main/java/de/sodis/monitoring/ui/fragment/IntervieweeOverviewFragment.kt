package de.sodis.monitoring.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.tabs.TabLayout
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.list.view.*
import kotlinx.android.synthetic.main.view_holder_picture_list_item.view.*
import kotlinx.android.synthetic.main.view_holder_question.view.*
import kotlinx.android.synthetic.main.view_holder_tab.*
import java.io.FileNotFoundException

//TODO refactor redundant code
class IntervieweeOverviewFragment : Fragment(), TabLayout.OnTabSelectedListener {
    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        intervieweeModel.getByVillage((tab!!.tag as Int?)!!)
            .observe(this, Observer { intervieweesVillageList ->
                recyclerView.withModels {
                    intervieweesVillageList.forEach {
                        pictureListItem {
                            id(it.id)
                            text(it.name)
                            onClick { _ ->
                                (activity as MainActivity).replaceFragments(
                                    IntervieweeDetailFragment(it.id),
                                    "TAG_INTERVIEW_DETAIL"
                                )
                            }
                            onBind { model, view, position ->
                                val bitmapdata = try {
                                    context!!.openFileInput("interviewee_${it.id}.jpg").readBytes()
                                } catch (ex: FileNotFoundException) {
                                    null
                                }
                                if (bitmapdata != null) {
                                    val bitmap =
                                        BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size)
//                                    view.dataBinding.root.imageView.setImageBitmap(bitmap)
                                    view.dataBinding.root.imageView.setImageBitmap(bitmap)
                                } else {
                                    view.dataBinding.root.imageView.setImageResource(R.drawable.ic_emoji_happy)
                                }

                            }
                        }
                    }
                }
            })
    }

    lateinit var recyclerView: EpoxyRecyclerView

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.view_holder_tab, container, false)
        recyclerView = view.list
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intervieweeModel.villageList.observe(this, Observer {
            tab_layout.addOnTabSelectedListener(this)
            it.forEach {
                tab_layout.addTab(tab_layout.run { newTab().setText(it.name).setTag(it.id) })
            }
        })
    }
}