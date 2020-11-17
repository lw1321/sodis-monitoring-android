package de.sodis.monitoring.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.R
import de.sodis.monitoring.pictureListItem
import de.sodis.monitoring.show_bottom_navigation
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_picture_list_item.view.*


//TODO refactor redundant code
class IntervieweeOverviewFragment : BaseListFragment() {

    //val args: Args by navArgs()

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


    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }
    val args: IntervieweeOverviewFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var villageId = args.villageId
        intervieweeModel.getByVillage((villageId))
            .observe(this, Observer { intervieweesVillageList ->
                (activity as MainActivity).supportActionBar!!.title = getString(R.string.village)
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
                                    view.dataBinding.root.imageView.setImageResource(R.drawable.ic_person_black_24dp)
                                }
                                it.imagePath?.let {
                                    setPic(it, view.dataBinding.root.imageView)
                                }
                            }
                        }
                    }
                }
            })

        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.navigation_forward_button_1?.isGone = false
        view?.navigation_forward_button_1?.setImageResource(R.drawable.ic_person_add_black_24dp)
        view?.navigation_forward_button_1?.setOnClickListener {
            //show add new family dialog
            // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }
            val edittext = EditText(activity as MainActivity)
            builder?.setTitle(getString(R.string.add_new_family_dialog_title))
            builder?.setMessage(getString(R.string.enter_family_name))
            builder?.setView(edittext)

            builder?.setPositiveButton("Save",
                DialogInterface.OnClickListener { dialog, whichButton -> //What ever you want to do with the value
                    //todo create new family and technologies
                    if (edittext.text.isEmpty()) {
                        Snackbar.make(
                            view,
                            getString(R.string.name_empty_message),
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        intervieweeModel.createInterviewee(
                            name = edittext.text.toString(),
                            village = args.villageId
                        )
                    }

                })

            builder?.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, whichButton ->
                    // what ever you want to do with No option.
                })


// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            val dialog: AlertDialog? = builder?.create()
            dialog?.show()
        }
        view?.navigation_forward_button_left?.isGone = true
        (activity as MainActivity).show_bottom_navigation()
        return view
    }
}