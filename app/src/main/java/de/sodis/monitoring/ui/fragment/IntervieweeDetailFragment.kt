package de.sodis.monitoring.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.*
import android.graphics.Shader.TileMode
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_family_age_structure.view.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
import kotlinx.android.synthetic.main.view_holder_technology.view.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class IntervieweeDetailFragment : BaseListFragment() {

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }

    val args: IntervieweeDetailFragmentArgs by navArgs()
    var intervieweeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intervieweeId = args.intervieweeId
        intervieweeModel.setInterviewee(intervieweeId)
        intervieweeModel.intervieweeDetail.observe(this, Observer { intervieweeD ->
            recyclerView.withModels {
                picture {
                    id("pictureHeader${intervieweeId}")
                    onClick { _ ->
                        dispatchTakePictureIntent()
                    }
                    onBind { model, view, position ->
                        val bitmapdata = try {
                            context!!.openFileInput("interviewee_${intervieweeId}.jpg").readBytes()
                        } catch (ex: FileNotFoundException) {
                            null
                        }
                        if (bitmapdata != null) {
                            val bitmap =
                                BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size)
                            val size = Math.min(bitmap.width, bitmap.height)
                            val cropedBitmap = if (bitmap.width < bitmap.height) {
                                Bitmap.createBitmap(
                                    bitmap,
                                    0,
                                    (bitmap.height - bitmap.width) / 2,
                                    size,
                                    size
                                )
                            } else {
                                Bitmap.createBitmap(
                                    bitmap,
                                    (bitmap.width - bitmap.height) / 2,
                                    0,
                                    size,
                                    size
                                )
                            }

                            val roundedBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(resources, cropedBitmap)

                            //cut corners
                            roundedBitmapDrawable.cornerRadius = Math.min(
                                bitmap.width,
                                bitmap.height
                            ) * 0.05f
//                                    view.dataBinding.root.imageView.setImageBitmap(bitmap)
                            view.dataBinding.root.imageView.setImageDrawable(roundedBitmapDrawable)
                        } else {
                            view.dataBinding.root.imageView.setImageResource(R.drawable.ic_add_a_photo_black_24dp)//TODO add C for Carlos etc
                        }

                    }
                }

                keyValue {
                    id("keyValueVillage")
                    key("village")
                    value(intervieweeD.village.name)
                }

                /**
                keyValue {
                id("keyValueLocalExpert")
                key("Local Expert")
                value(intervieweeD.user?.firstName + " " + intervieweeD.user?.lastName)
                }

                keyValue {
                id("keyValueCount")
                key("miembro de la familia")
                value(
                (intervieweeD.interviewee.boysCount
                + intervieweeD.interviewee.girlsCount
                + intervieweeD.interviewee.youngMenCount
                + intervieweeD.interviewee.youngWomenCount
                + intervieweeD.interviewee.womenCount
                + intervieweeD.interviewee.menCount
                + intervieweeD.interviewee.oldWomenCount
                + intervieweeD.interviewee.oldMenCount).toString()
                )
                }**/

                //general data
                generalData {
                    id("generalData")
                    key("Datos Generales")
                    onClick { _ ->
                        //show survey for datos generales
                        val action =
                            IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToMonitoringOverviewFragment(
                                6,// technology id of datos generales TODO replace hardcoded id
                                args.intervieweeId
                            )
                        findNavController().navigate(action)
                    }

                }

                intervieweeD.intervieweeTechnologies.forEach { techno ->
                    //are there open tasks for this technology?
                    val taskFilteredList = intervieweeD.tasks.filter { task ->
                        task.intervieweeTechnologyId == techno.id
                    }
                    var taskStatus: String? = null
                    if (taskFilteredList.isNotEmpty()) {
                        taskStatus = taskFilteredList.first().name!!
                    }
                    taskFilteredList ?: "All good"
                    technology {
                        id("technology")
                        state(techno.stateTechnology.toString())
                        knowledgeState(techno.stateKnowledge.toString())
                        name(techno.name)
                        taskName(taskStatus ?: "")
                        onClick { _ ->
                            //show surveys for the corresponding technoology
                            val action =
                                IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToMonitoringOverviewFragment(
                                    intervieweeId = intervieweeId,
                                    technologyId = techno.technologyId
                                )
                            findNavController().navigate(action)
                        }
                        onBind { model, view, position ->
                            val bo = BitmapFactory.Options()
                            bo.inMutable = true
                            val b = BitmapFactory.decodeResource(
                                resources,
                                when (techno.name) {
                                    "Cocina Ecologica" -> R.drawable.ofen_kreis_basic
                                    "Lavado de Manos" -> R.drawable.handwaschstation_icon
                                    "Baño" -> R.drawable.toilette_icon
                                    "Tecnología para Agua Segura (Filtro)" -> R.drawable.wasserfilter_icon

                                    else -> R.drawable.sodis_logo
                                },
                                bo
                            )
                            view.dataBinding.root.technolgyImage.setImageBitmap(b)

                            view.dataBinding.root.technolgyImage.setColorFilter(
                                when (techno.stateTechnology) {
                                    0 -> Color.GRAY
                                    1 -> Color.YELLOW
                                    2 -> Color.GREEN
                                    else -> Color.RED
                                }
                            )
                            view.dataBinding.root.technologyKnowledgeImage.setColorFilter(
                                when (techno.stateKnowledge) {
                                    0 -> Color.GRAY
                                    1 -> Color.YELLOW
                                    2 -> Color.GREEN
                                    else -> Color.RED
                                }
                            )
                            view.dataBinding.root.technologyTaskImage.setColorFilter(Color.YELLOW)
                            if (taskStatus == null) {
                                view.dataBinding.root.technologyTaskImage.visibility = View.GONE
                            } else {
                                view.dataBinding.root.technologyTaskImage.visibility = View.VISIBLE
                            }

                        }
                    }
                }

            }
            recyclerView.recycledViewPool.clear()
            view?.navigation_forward_button_1?.setImageResource(R.drawable.ic_baseline_save_24)
            view?.navigation_forward_button_1?.setOnClickListener {
                intervieweeModel.saveInterviewee()
                Snackbar.make(
                    view!!,
                    "Saved",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        })
    }


    val REQUEST_TAKE_PHOTO = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity!!,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //save the image path in our database..
            //set image to iamgeview
            if (currentPhotoPath != null) {
                BitmapFactory.decodeFile(currentPhotoPath)?.also { bitmap ->
                    view!!.imageView.setImageBitmap(bitmap)
                }
            }
        }
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
