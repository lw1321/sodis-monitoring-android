package de.sodis.monitoring.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.tabs.TabLayout
import de.sodis.monitoring.R
import de.sodis.monitoring.pictureListItem
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.list.view.*
import kotlinx.android.synthetic.main.view_holder_picture_list_item.view.imageView
import kotlinx.android.synthetic.main.view_holder_tab.*


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
                                val options = navOptions {
                                    anim {
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_right
                                    }
                                }
                                val action =
                                    IntervieweeOverviewFragmentDirections.actionIntervieweeOverviewFragmentToIntervieweeDetailFragment(
                                        intervieweeId = it.id
                                    )
                                findNavController().navigate(action)
                            }
                            onBind { model, view, position ->
                                if (it.imagePath == null) {
                                    view.dataBinding.root.imageView.setImageResource(R.drawable.sodis_logo)
                                }
                                it.imagePath?.let {
                                    setPic(it, view.dataBinding.root.imageView)
                                }
                            }
                        }
                    }
                }
            })
    }

    private fun setPic(currentPhotoPath: String, imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = 64
        val targetH: Int = 64

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    lateinit var recyclerView: EpoxyRecyclerView

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
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
        intervieweeModel.villageList.observe(viewLifecycleOwner, Observer {
            tab_layout.addOnTabSelectedListener(this)
            it.forEach {
                tab_layout.addTab(tab_layout.run { newTab().setText(it.name).setTag(it.id) })
            }
        })
    }
}