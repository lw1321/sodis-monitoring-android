package de.sodis.monitoring.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import de.sodis.monitoring.picture
import de.sodis.monitoring.technology
import de.sodis.monitoring.*
import de.sodis.monitoring.todolist.TodoDialog
import de.sodis.monitoring.viewmodel.*
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
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
    private val todoViewModel: TodoPointModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(TodoPointModel::class.java)
    }
    val args: IntervieweeDetailFragmentArgs by navArgs()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        placeViewModel.intervieweeItem.observe(viewLifecycleOwner, Observer { familyList ->
            surveyViewModel.surveyList.observe(viewLifecycleOwner, Observer { surveyList ->
                surveyViewModel.completedSurveyList.observe(viewLifecycleOwner, Observer { completedSurveyList ->
                    todoViewModel.todoPointList.observe(viewLifecycleOwner, Observer { todoList ->
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
                            todoList.filter { it.family == args.intervieweeId }.forEach { todoPoint ->
                                task {
                                    id(todoPoint.id.toString() + "td")
                                    text(todoPoint.text)
                                    checked(todoPoint.done)
                                    date(SimpleDateFormat("dd.MM.yyyy").format(todoPoint.duedate!!.time))
                                    onClickedCheckbox { _ ->
                                        Thread(Runnable {
                                            //todo intervieweeModel.checkChangeTodoPoint(todoPoint = todoPoint)
                                        }).start()
                                    }
                                }
                            }

                            centeredButton {
                                id("addtodobutton")
                                text("Añadir una tarea")
                                onClick { _ ->
                                    val dialog = TodoDialog(
                                            args.intervieweeId,
                                            null,
                                            context!!,
                                            null
                                    )
                                    dialog.show(childFragmentManager, "todo_in_survey")
                                }
                            }
                            //SURVEYS
                            val familyMemberSurvey = surveyList.first { it.surveyName == "Miembros de la familia" }
                            val familyMemberSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == familyMemberSurvey.surveyId }
                            //get count of family members, get status survey done, get survey id
                            singleSurvey {
                                id("FamilyMemberSurvey")
                                survey1OnClick { _ ->
                                    openSurvey(familyMemberSurvey.surveyId)
                                }
                                projectName(familyMemberSurvey.projectName)
                                onBind { model, view, position ->
                                    view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (familyMemberSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                }
                            }
                            //Nutricion
                            val agricultureSurvey = surveyList.first { it.surveyName == "Agricultura / disponibilidad de agua" }
                            val agricultureSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == agricultureSurvey.surveyId }
                            val nutricionSurvey = surveyList.first { it.surveyName == "Nutrición" }
                            val nutricionCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == nutricionSurvey.surveyId }
                            technology {
                                id("Nutricion")
                                survey1OnClick { clicked ->
                                    //Nutricion
                                    openSurvey(nutricionSurvey.surveyId)
                                }
                                survey2OnClick { clicked ->
                                    //Agriculture
                                    openSurvey(agricultureSurvey.surveyId)
                                }
                                name(nutricionSurvey.projectName)
                                onBind { model, view, position ->
                                    view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (nutricionCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                    view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_vegetables)
                                    view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (agricultureSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                    view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_agriculture)//TODO icons einfügen
                                }
                            }

                            //Quari Wharmi
                            val empresaSurvey = surveyList.first { it.surveyName == "empresa" }
                            val empresaSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == empresaSurvey.surveyId }
                            val taraSurvey = surveyList.first { it.surveyName == "Tara" }
                            val taraSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == taraSurvey.surveyId }
                            technology {
                                id("QuariWarmi")
                                survey1OnClick { clicked ->
                                    //Tara
                                    openSurvey(taraSurvey.surveyId)
                                }
                                survey2OnClick { clicked ->
                                    //Empresa
                                    openSurvey(empresaSurvey.surveyId)
                                }
                                name(taraSurvey.projectName)
                                onBind { model, view, position ->
                                    view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_icontaraplant)
                                    view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (taraSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                    view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconunternehmertum)
                                    view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (empresaSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))

                                }
                            }

                            //cocina
                            val kitchenSurvey = surveyList.first { it.surveyName == "Cocinas Ecológicas" }
                            val kitchenSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == kitchenSurvey.surveyId }
                            val kitchenKnowledgeSurvey = surveyList.first { it.surveyName == "Uso Cocinas Ecológicas" }
                            val kitchenKnowledgeSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == kitchenKnowledgeSurvey.surveyId }
                            technology {
                                id("Cocina Ecologica")
                                survey1OnClick { clicked ->
                                    openSurvey(kitchenSurvey.surveyId)
                                }
                                survey2OnClick { clicked ->
                                    openSurvey(kitchenKnowledgeSurvey.surveyId)
                                }
                                name(kitchenSurvey.projectName)
                                onBind { model, view, position ->
                                    view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconcocina)//TODO icons einfügen
                                    view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusococina)//TODO einfärben nach status
                                    view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (kitchenSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                    view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (kitchenKnowledgeSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                }
                            }

                            //filtro
                            val filterSurvey = surveyList.first { it.surveyName == "Filtro de Agua" }
                            val filterSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == filterSurvey.surveyId }
                            val filterKnowledgeSurvey = surveyList.first { it.surveyName == "Uso Filtro de Agua" }
                            val filterKnowledgeSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == filterKnowledgeSurvey.surveyId }
                            technology {
                                id("filtro")
                                survey1OnClick { clicked ->
                                    openSurvey(filterSurvey.surveyId)
                                }
                                survey2OnClick { clicked ->
                                    openSurvey(filterKnowledgeSurvey.surveyId)
                                }
                                onBind { model, view, position ->
                                    view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconfiltro)
                                    view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusofiltro)
                                    view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (filterSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                    view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (filterKnowledgeSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                }
                            }

                            //toilet
                            val toiletSurvey = surveyList.first { it.surveyName == "Baños" }
                            val toiletSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == toiletSurvey.surveyId }
                            val toiletKnowledgeSurvey = surveyList.first { it.surveyName == "Uso Baños " }
                            val toiletKnowledgeSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == toiletKnowledgeSurvey.surveyId }
                            technology {
                                id("toilet")
                                name(toiletSurvey.projectName)
                                survey1OnClick { clicked ->
                                    openSurvey(toiletSurvey.surveyId)
                                }
                                survey2OnClick { clicked ->
                                    openSurvey(toiletKnowledgeSurvey.surveyId)
                                }
                                onBind { model, view, position ->
                                    view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconba_o)//TODO icons einfügen
                                    view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusoba_o)//TODO einfärben nach status
                                    view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (toiletSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                    view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (toiletKnowledgeSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                }
                            }
                            //hand washing
                            val washSurvey = surveyList.first { it.surveyName == "Lavado de manos" }
                            val washSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == washSurvey.surveyId }
                            val washKnowledgeSurvey = surveyList.first { it.surveyName == "Uso Lavado de manos " }
                            val washKnowledgeSurveyCompleted = completedSurveyList.any { it.intervieweeId == args.intervieweeId && it.surveyHeaderId == washKnowledgeSurvey.surveyId }

                            technology {
                                id("wash")
                                name(washSurvey.projectName)
                                survey1OnClick { clicked ->
                                    openSurvey(washSurvey.surveyId)
                                }
                                survey2OnClick { clicked ->
                                    openSurvey(washKnowledgeSurvey.surveyId)
                                }
                                onBind { model, view, position ->
                                    view.dataBinding.root.survey1Icon.setImageResource(R.drawable.ic_iconwash)
                                    view.dataBinding.root.survey2Icon.setImageResource(R.drawable.ic_iconusolavamanos)
                                    view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (washSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                    view.dataBinding.root.survey2Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (washKnowledgeSurveyCompleted) R.color.colorGreen700 else R.color.colorGrey700))
                                }
                            }
                        }
                    })
                })
            })
        })
        view?.navigation_forward_button_1?.isGone = true
        view?.navigation_forward_button_left?.isGone = true
        view?.navigation_cancel_button?.isGone = true
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
    lateinit var currentPhotoPath: String

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
            // Store image path (currentPhotoPath) in database. data also includes thumbnail and full size image to set
            // it manually to the UI, better use Livedata observe on the imagePath to set the image automatically
            // if the imagePath in the database has changed. (See Line 60-72 IntervieweeDetailFragment)
            // Store Image path with the family(intervieweeId)
            placeViewModel.storeImagePath(currentPhotoPath, args.intervieweeId)
        }
    }
}
