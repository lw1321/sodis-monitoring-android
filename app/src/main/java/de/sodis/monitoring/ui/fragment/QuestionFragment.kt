package de.sodis.monitoring.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.QuestionOption
import de.sodis.monitoring.db.entity.QuestionOptionChoice
import de.sodis.monitoring.db.response.QuestionAnswer
import de.sodis.monitoring.db.response.QuestionItem
import de.sodis.monitoring.todolist.TodoDialog
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.QuestionViewModel
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_numeric.view.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
import kotlinx.android.synthetic.main.view_holder_question.view.*
import kotlinx.android.synthetic.main.view_holder_text_choice.view.*
import kotlinx.android.synthetic.main.view_holder_text_input.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class QuestionFragment : BaseListFragment(), DialogInterface.OnDismissListener {


    private val questionViewModel: QuestionViewModel by lazy {
        ViewModelProviders.of(
            activity!!,
            MyViewModelFactory(activity!!.application, listOf(args.surveyId))
        )
            .get(QuestionViewModel::class.java)
    }

    val args: QuestionFragmentArgs by navArgs()
    private lateinit var currentQuestion: List<QuestionItem>
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).hide_bottom_navigation()
        questionViewModel.questionItemLiveList.observe(this, Observer { list ->
            questionViewModel.currentPosition.observe(this, Observer { position ->
                if (list.isNotEmpty()) {
                    currentPosition = position
                    currentQuestion =
                        list.filter { it.id == questionViewModel.questionIdList[position] }
                    createQuestion(currentQuestion)
                }
            })
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)
        view?.navigation_forward_button_1?.setOnClickListener {
            if (questionViewModel.isAnswered(currentQuestion.first().id)) {
                if (questionViewModel.createTodo()) { //todo: anpassen wenn yes/no question geändert
                    /* //TODO pass the interviewee id, request interviewee entity in dialog from placeviewmodel
                    val answerToCheck: Answer =
                        questionViewModel.answerToID(currentQuestion.first().id)!!
                    val dialog = TodoDialog(

                        questionViewModel.interviewee,
                        currentQuestion.title,
                        context!!,
                        this
                    )
                    dialog.show(childFragmentManager, "todo_in_survey")
                    */
                } else {
                    questionViewModel.listOfAnsweredQuestions += currentPosition
                    val hasNext = questionViewModel.nextQuestion()
                    if (hasNext) {
                        val action =
                            QuestionFragmentDirections.actionQuestionFragmentSelf(
                                args.surveyId,
                                intervieweeId = args.intervieweeId
                            )
                        findNavController().navigate(action)
                    } else {
                        val finishDialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
                        finishDialog.setTitle("enviar cuestionario")
                        finishDialog.setMessage("¿Guardar respuestas?")
                        finishDialog.setPositiveButton(
                            "Si"
                        ) { _, _ ->
                            questionViewModel.finishSurvey()
                            Snackbar.make(
                                view!!.rootView.findViewById(R.id.nav_host_fragment),
                                getString(R.string.message_monitoring_completed),
                                Snackbar.LENGTH_LONG
                            ).show()
                            val action =
                                QuestionFragmentDirections.actionQuestionFragmentToIntervieweeDetailFragment(
                                    intervieweeId = args.intervieweeId
                                )
                            findNavController().navigate(action)
                            (activity as MainActivity).show_bottom_navigation()
                        }
                        finishDialog.setNegativeButton(
                            "No"
                        ) { _, _ ->
                            Snackbar.make(
                                view!!.rootView.findViewById(R.id.nav_host_fragment),
                                getString(R.string.message_monitoring_answer_required),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        val alert: AlertDialog = finishDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    }
                }


            } else {
                Snackbar.make(
                    view!!,
                    getString(R.string.message_monitoring_answer_required),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        view?.navigation_forward_button_left?.isGone = currentPosition == 0

        view?.navigation_forward_button_left?.setOnClickListener {
            if (currentPosition != 0) {
                questionViewModel.previousQuestion()
                val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                    args.surveyId,
                    intervieweeId = args.intervieweeId
                )
                findNavController().navigate(action)
            }
        }



        return view
    }

    private fun createQuestion(questionList: List<QuestionItem>) {
        recyclerView.recycledViewPool.clear()
        recyclerView.withModels {
            question {
                id("question")
                questionText(questionList.first().name)
                onBind { model, view, position ->
                    if (questionList.first().path != null) {
                        view.dataBinding.root.question_image.load(File(questionList.first().path!!))
                    }
                }
            }
            when (questionList.first().inputTypeId) {

                1 -> {
                    //Single Choice
                    questionList.forEach {
                        default {
                            id(it.questionOptionId)
                            text(it.optionChoiceName)
                            onClick { clicked ->
                                questionViewModel.setAnswer(
                                    imagePath = null,
                                    questionOption = it.questionOptionId,
                                    answerText = null,
                                    questionId = currentQuestion.first().id
                                )

                            }
                        }
                    }
                }
                2 -> {
                    // Text
                    textInput {
                        id("textInput")
                        hint(getString(R.string.hint_monitoring_answer))
                        inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                        onBind { model, view, position ->
                            view.dataBinding.root.answerTextInput.requestFocus()
                            view.dataBinding.root.answerTextInput.addTextChangedListener {
                                print("Text changed")
                                questionViewModel.setAnswer(
                                    imagePath = null,
                                    questionOption = null,
                                    answerText = it.toString(),
                                    questionId = currentQuestion.first().id
                                )
                            }
                        }
                    }
                }

                4 -> {
                    //Image
                    default {
                        id(questionList.first().questionOptionId)
                        text("Bild aufnehmen!")
                        onClick { clicked ->
                            dispatchTakePictureIntent()
                        }
                    }
                }
            }
        }
    }


    val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //save the image path in our database..
            //set image to iamgeview
            //store the file
            questionViewModel.setAnswer(
                imagePath = currentPhotoPath,
                questionOption = null,
                answerText = null,
                questionId = currentQuestion.first().id
            )
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm: InputMethodManager =
            (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)

    }

    override fun onDismiss(dialog: DialogInterface?) {
        println("onDismissed called")
        questionViewModel.listOfAnsweredQuestions += currentPosition
        val hasNext = questionViewModel.nextQuestion()
        if (hasNext) {
            val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                args.surveyId,
                intervieweeId = args.intervieweeId
            )
            findNavController().navigate(action)
        } else {
            questionViewModel.finishSurvey()
            Snackbar.make(
                view!!.rootView.findViewById(R.id.nav_host_fragment),
                getString(R.string.message_monitoring_completed),
                Snackbar.LENGTH_LONG
            ).show()
            val action =
                QuestionFragmentDirections.actionQuestionFragmentToIntervieweeDetailFragment(
                    intervieweeId = args.intervieweeId
                )
            findNavController().navigate(action)
            (activity as MainActivity).show_bottom_navigation()

        }
    }
}
