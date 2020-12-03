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
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.db.entity.QuestionImage
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MonitoringOverviewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.continuable_list.view.*

import kotlinx.android.synthetic.main.view_holder_header.*
import kotlinx.android.synthetic.main.view_holder_key_value.view.*
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

    private val monitoringOverviewModel: MonitoringOverviewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(MonitoringOverviewModel::class.java)
    }

    val args: IntervieweeDetailFragmentArgs by navArgs()
    var intervieweeId: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intervieweeId = args.intervieweeId
        intervieweeModel.setInterviewee(intervieweeId)
        intervieweeModel.intervieweeDetail.observe(this, Observer { intervieweeD ->
            (activity as MainActivity).supportActionBar!!.title = intervieweeD.interviewee.name
            recyclerView.withModels {
                picture {
                    id("pictureHeader${intervieweeId}")
                    onClick { _ ->
                        dispatchTakePictureIntent()
                    }
                    onBind { model, view, position ->
                        if (intervieweeD.interviewee.imagePath != null) {
                            val bo : BitmapFactory.Options = BitmapFactory.Options()
                            bo.inSampleSize = 8
                            BitmapFactory.decodeFile(intervieweeD.interviewee.imagePath, bo)
                                ?.also { bitmap ->
                                    view.dataBinding.root.imageView.setImageBitmap(bitmap)

                                }
                        } else {
                            view.dataBinding.root.imageView.setImageResource(R.drawable.ic_person_black_24dp)//TODO add C for Carlos etc
                        }

                    }
                }
                keyValue {
                    id("keyValueName")
                    key("Persona")
                    value(intervieweeD.interviewee.name)
                    onBind { model, view, position ->
                        view.dataBinding.root.imageView2.setImageResource(R.drawable.ic_person_black_24dp)
                    }
                }

                keyValue {
                    id("keyValueVillage")
                    key(getString(R.string.village))
                    value(intervieweeD.village.name)
                    onBind { model, view, position ->
                        view.dataBinding.root.imageView2.setImageResource(R.drawable.ic_village)
                    }
                }
                keyValue {
                    id("keyValueGeneral")
                    key("Miembros de la familia")
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
                    onBind { model, view, position ->
                        view.dataBinding.root.imageView2.setImageResource(R.drawable.ic_family_silhouette_svgrepo_com)
                        view.dataBinding.root.imageEditable.visibility = View.VISIBLE
                        view.dataBinding.root.imageEditable.setOnClickListener {
                            val action =
                                IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToMonitoringOverviewFragment(
                                    6,// technology id of datos generales TODO replace hardcoded id
                                    args.intervieweeId
                                )
                            findNavController().navigate(action)
                        }
                    }
                }

                intervieweeD.intervieweeTechnologies.forEach { techno ->
                    //are there open tasks for this technology?


                    technology {
                        id("technology${techno.id}")
                        state(techno.stateTechnology.toString())
                        knowledgeState(techno.stateKnowledge.toString())
                        name(techno.name)
                        taskName("")//TODO
                        onClickTechnology { _ ->
                            Thread(Runnable {
                                val technologyList: List<SurveyHeader> = monitoringOverviewModel.getSurveyHeaderListByTechnologyIDSynchronous(techno.technologyId)
                                val item = technologyList.single { surveyHeader ->  surveyHeader.surveyName.toLowerCase().contains("practicas") || surveyHeader.surveyName.toLowerCase().contains("practicas")}
                                if(item!=null) {
                                    val action =
                                        IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToQuestionFragment(
                                            item.id,
                                            intervieweeId
                                        )
                                    (activity as MainActivity).runOnUiThread {
                                        findNavController().navigate(action)
                                    }
                                }

                            }).start()
                        }
                        onClickPerson { _ ->
                            Thread(Runnable {
                                val technologyList: List<SurveyHeader> = monitoringOverviewModel.getSurveyHeaderListByTechnologyIDSynchronous(techno.technologyId)
                                val item = technologyList.single { surveyHeader ->  surveyHeader.surveyName.toLowerCase().contains("infraestructura") || surveyHeader.surveyName.toLowerCase().contains("infrastructura")}
                                if(item!=null) {

                                    val action =
                                        IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToQuestionFragment(
                                            item.id,
                                            intervieweeId
                                        )
                                    (activity as MainActivity).runOnUiThread {
                                        findNavController().navigate(action)
                                    }
                                }
                            }).start()

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
                                    1 -> Color.RED
                                    2 -> ResourcesCompat.getColor(
                                        resources,
                                        R.color.colorPrimary,
                                        null
                                    )
                                    else -> Color.GRAY
                                }
                            ) //bild braucht onClickListener


                            view.dataBinding.root.technologyKnowledgeImage.setColorFilter(
                                when (techno.stateKnowledge) {
                                    0 -> Color.GRAY
                                    1 -> Color.RED
                                    2 -> ResourcesCompat.getColor(
                                        resources,
                                        R.color.colorPrimary,
                                        null
                                    )
                                    else -> Color.GRAY
                                }
                            )
                            view.dataBinding.root.technologyTaskImage.setColorFilter(Color.YELLOW)
                            view.dataBinding.root.technologyTaskImage.visibility =
                                View.GONE//TODO task
                        }
                    }
                }

            }
            recyclerView.recycledViewPool.clear()
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
            if (currentPhotoPath != null) {//todo show image immediately
                //store the file
                intervieweeModel.storeImagePath(currentPhotoPath)
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
