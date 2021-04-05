package de.sodis.monitoring.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.response.QuestionItem
import de.sodis.monitoring.todolist.TodoDialog
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.QuestionViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_answer_image.*
import kotlinx.android.synthetic.main.view_holder_question.view.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).hide_bottom_navigation()
        questionViewModel.setSurvey(args.surveyId)
        questionViewModel.questionItemLiveList.observe(this, Observer { list ->
            if (list.isNotEmpty()) {
                currentQuestion =
                        list.filter { it.id == questionViewModel.questionIdList[questionViewModel.currentPosition] }
                createQuestion(currentQuestion)
            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view?.navigation_forward_button_1?.setOnClickListener {
            if (questionViewModel.isAnswered(currentQuestion.first().id) || currentQuestion.first().inputTypeId == 2 || currentQuestion.first().inputTypeId == 4) {
                //on input type single choice answer is required, for text(2) or image(4) it is optional.
                nextQuestion()
            } else {
                Snackbar.make(
                        view!!,
                        getString(R.string.message_monitoring_answer_required),
                        Snackbar.LENGTH_LONG
                ).show()
            }
        }

        view?.navigation_forward_button_left?.isGone = questionViewModel.currentPosition == 0

        view?.navigation_forward_button_left?.setOnClickListener {
            moveQuestionBack()
        }

        return view
    }

    private fun moveQuestionBack() {
            questionViewModel.previousQuestion()
            val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                    args.surveyId,
                    intervieweeId = args.intervieweeId
            )
            findNavController().navigate(action)

    }


    private fun nextQuestion() {
        questionViewModel.listOfAnsweredQuestions += questionViewModel.currentPosition
        val hasNext = questionViewModel.nextQuestion()
        if (hasNext) {
            val action =
                    QuestionFragmentDirections.actionQuestionFragmentSelf(
                            args.surveyId,
                            intervieweeId = args.intervieweeId
                    )
            findNavController().navigate(action)
        } else {
            val finishDialog: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
            finishDialog.setTitle("enviar cuestionario")
            finishDialog.setMessage("¿Guardar respuestas?")
            finishDialog.setPositiveButton(
                    "Si"
            ) { _, _ ->
                questionViewModel.finishSurvey(args.intervieweeId)
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
                //Remove last answer
                moveQuestionBack()
            }
            val alert: AlertDialog = finishDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }

    }

    private fun createQuestion(questionList: List<QuestionItem>) {
        if (questionList.isNotEmpty()) {
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
                                    if (it.optionChoiceName == "Escribir en la lista de tareas") {
                                        val dialog = TodoDialog(args.intervieweeId,
                                                currentQuestion.first().name,
                                                context!!,
                                                this@QuestionFragment
                                        )
                                        dialog.show(childFragmentManager, "todo_in_survey")
                                    } else {
                                        nextQuestion()
                                    }
                                    // go directly to next question

                                }
                            }
                        }
                        //Add placeholder to present the last answer option properly
                        placeholder {
                            id("placeholder")
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
                        answerImage {
                            id(questionList.first().questionOptionId)
                            onClick { clicked ->
                                dispatchTakePictureIntent()
                            }
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
            val bo: BitmapFactory.Options = BitmapFactory.Options()
            bo.inSampleSize = 8
            BitmapFactory.decodeFile(currentPhotoPath, bo)?.also { bitmap ->
                        answerImage.setImageBitmap(bitmap)
                    }
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

        //Cancel survey dialog todo
        view?.navigation_cancel_button?.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
            alertDialog.setTitle("cancelar el cuestionario")
            alertDialog.setMessage("¿Descartar todas las respuestas?")
            alertDialog.setPositiveButton(
                    "Si"
            ) { _, _ ->
                questionViewModel.currentPosition = 0
                questionViewModel.listOfAnsweredQuestions = mutableListOf()
                Snackbar.make(
                        view!!.rootView.findViewById(R.id.nav_host_fragment),
                        getString(R.string.message_monitoring_cancelled),
                        Snackbar.LENGTH_LONG
                ).show()

                val action =
                        QuestionFragmentDirections.actionQuestionFragmentToIntervieweeDetailFragment(
                                intervieweeId = args.intervieweeId
                        )
                findNavController().navigate(action)
                (activity as MainActivity).show_bottom_navigation()
            }
            alertDialog.setNegativeButton(
                    "No"
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()

        }
    }


    override fun onDismiss(dialog: DialogInterface?) {
        println("onDismissed called")
        nextQuestion()
    }
}
