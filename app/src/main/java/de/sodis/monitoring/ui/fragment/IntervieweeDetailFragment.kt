package de.sodis.monitoring.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import de.sodis.monitoring.R
import de.sodis.monitoring.default
import de.sodis.monitoring.picture
import de.sodis.monitoring.technology
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.PlaceViewModel
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.todo_dialog_layout.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
import kotlinx.android.synthetic.main.view_holder_question.view.*
import kotlinx.android.synthetic.main.view_holder_technology.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class IntervieweeDetailFragment : BaseListFragment() {

    private val placeViewModel: PlaceViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(PlaceViewModel::class.java)
    }
    private val surveyViewModel: SurveyViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(SurveyViewModel::class.java)
    }
    val args: IntervieweeDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        placeViewModel.familyList.observe(viewLifecycleOwner, Observer { familyList ->
            surveyViewModel.surveyList.observe(viewLifecycleOwner, Observer { surveyList ->
                recyclerView.withModels {
                    familyList.filter { it.id == args.intervieweeId }.forEach {
                        picture {
                            id(it.id)
                            name(it.name)
                            village(it.villageName)
                            onClick { clicked ->
                                dispatchTakePictureIntent()
                            }
                            onBind { model, view, position ->
                                if (it.imagePath != null) {
                                    view.dataBinding.root.imageView.load(File(it.imagePath))
                                }
                            }
                        }
                    }
                    technology {
                        id("Nutricion")
                        survey1OnClick { clicked ->
                            //Agriculture
                            openSurvey(surveyList.filter { it.projectName == "Nutricion" }[0].surveyId)

                        }
                        survey2OnClick { clicked ->
                            //Nutricion
                            openSurvey(surveyList.filter { it.projectName == "Nutricion" }[1].surveyId)
                        }
                        onBind { model, view, position ->
                            view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_agriculture)//TODO icons einfügen
                            view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_vegetables)
                            view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGreen700))
                            view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGrey700))

                        }
                    }
                    technology {
                        id("QuariWarmi")
                        survey1OnClick { clicked ->
                            //Empresa
                            openSurvey(surveyList.first { it.surveyName == "empresa" }.surveyId)
                        }
                        survey2OnClick { clicked ->
                            //Tara
                            openSurvey(surveyList.first { it.surveyName == "Tara" }.surveyId)
                        }
                        onBind { model, view, position ->
                            view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconunternehmertum)//TODO icons einfügen
                            view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconunternehmertum)//TODO einfärben nach status
                            view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorYellow700))
                            view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGreen700))

                        }
                    }
                    //cocina
                    technology {
                        id("Cocina Ecologica")
                        survey1OnClick { clicked ->
                            openSurvey(surveyList.first { it.surveyName == "Cocinas Ecológicas" }.surveyId)
                        }
                        survey2OnClick { clicked ->
                            openSurvey(surveyList.first { it.surveyName == "Mantenimiento" }.surveyId)
                        }
                        onBind { model, view, position ->
                            view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconcocina)//TODO icons einfügen
                            view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusotechnologia)//TODO einfärben nach status
                            view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGrey700))
                            view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorRed700))
                        }
                    }
                    //cocina
                    technology {
                        id("filtro")
                        survey1OnClick { clicked ->
                            openSurvey(surveyList.first { it.surveyName == "Filtro de Agua" }.surveyId)
                        }
                        survey2OnClick { clicked ->
                            openSurvey(surveyList.first { it.surveyName == "Agua Segura (Filtro)\n" }.surveyId)
                        }
                        onBind { model, view, position ->
                            view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconfiltro)//TODO icons einfügen
                            view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusotechnologia)//TODO einfärben nach status
                            view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGreen700))
                            view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGrey700))
                        }
                    }
                    //cocina
                    technology {
                        id("toilet")
                        survey1OnClick { clicked ->
                            openSurvey(surveyList.first { it.surveyName == "Baños (Saneamiento)" }.surveyId)
                        }
                        survey2OnClick { clicked ->
                            openSurvey(surveyList.first { it.surveyName == "Saneamiento (Baños)" }.surveyId)
                        }
                        onBind { model, view, position ->
                            view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconba_o)//TODO icons einfügen
                            view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusotechnologia)//TODO einfärben nach status
                            view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorRed700))
                            view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorYellow700))
                        }
                    }
                    //cocina
                    technology {
                        id("wash")
                        survey1OnClick { clicked ->
                            openSurvey(surveyList.first { it.surveyName == "Lavado de manos (Higiene)" }.surveyId)
                        }
                        survey2OnClick { clicked ->
                        }
                        onBind { model, view, position ->
                            view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconwash)//TODO icons einfügen
                            view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusotechnologia)//TODO einfärben nach status
                            view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGreen700))
                            view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorGrey700))
                        }
                    }

                }
            })

        })

        view?.navigation_forward_button_1?.isGone = true
        view?.navigation_forward_button_left?.isGone = true
        return view
    }

    private fun openSurvey(surveyId: Int) {
        surveyViewModel.surveyId = surveyId
        val action =
            IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToQuestionFragment(
                intervieweeId = args.intervieweeId,
                surveyId = surveyId
            )
        findNavController().navigate(action)
    }


    /**
     * TAKE AND SAVE Image
     */

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
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
            placeViewModel.storeImagePath(currentPhotoPath, args.intervieweeId)
        }
    }

}
