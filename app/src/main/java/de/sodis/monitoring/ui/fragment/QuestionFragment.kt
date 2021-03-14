package de.sodis.monitoring.ui.fragment

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.view.View
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
import de.sodis.monitoring.db.entity.QuestionOption
import de.sodis.monitoring.db.entity.QuestionOptionChoice
import de.sodis.monitoring.db.response.QuestionAnswer
import de.sodis.monitoring.todolist.TodoDialog
import de.sodis.monitoring.viewmodel.MyViewModelFactory
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


    private lateinit var currentQuestion: QuestionAnswer
    private val surveyViewModel: SurveyViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, listOf(surveyId)))
                    .get(SurveyViewModel::class.java)
        }!!
    }

    val args: QuestionFragmentArgs by navArgs()
    var surveyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).hide_bottom_navigation()
        surveyId = args.surveyId
        surveyViewModel.setSurveyId(surveyId)
        if (surveyViewModel.interviewee == null) {
            surveyViewModel.setInterviewee(args.intervieweeId)
        }
        surveyViewModel.questionItemList.observe(this, Observer { list ->
            currentQuestion = list.get(index = surveyViewModel.currentPosition)
            //if question is type "do image" don't load the UI, instead redirect to image take intent.
            if (currentQuestion.question.inputTypeId == 4) {
                dispatchTakePictureIntent()

            }


            recyclerView.withModels {
                question {
                    id("question")
                    title(currentQuestion.title)
                    questionText(currentQuestion.question.questionName)
                    onBind { model, view, position ->
                        if (currentQuestion.image != null) {
                            view.dataBinding.root.question_image.load(File(currentQuestion.image!!.path))
                        }
                    }
                }
                when (currentQuestion.question.inputTypeId) {
                    2 -> //todo
                        textInput {
                            id("input")
                            hint(getString(R.string.hint_monitoring_answer))
                            inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                            onBind { model, view, position ->
                                view.dataBinding.root.answerTextInput.requestFocus()
                                view.dataBinding.root.answerTextInput.addTextChangedListener {
                                    surveyViewModel.setAnswer(
                                            currentQuestion.question.id,
                                            it!!.toString(),
                                            currentQuestion.answers[0].questionOption.id
                                    )
                                }
                            }
                        }
                    1 ->
                        if (currentQuestion.answers.size == 2) {
                            textChoice {
                                id("choice")
                                option1(currentQuestion.answers[0].optionChoice.optionChoiceName)
                                option2(currentQuestion.answers[1].optionChoice.optionChoiceName)
                                onBind { model, view, position ->
                                    view.dataBinding.root.radio_group.clearCheck()
                                    view.dataBinding.root.radio_group.setOnCheckedChangeListener { group, checkedId ->
                                        val index = if (checkedId == R.id.optionButton) 0 else 1
                                        surveyViewModel.setAnswer(
                                                currentQuestion.question.id,
                                                currentQuestion.answers[index].optionChoice.optionChoiceName,
                                                currentQuestion.answers[index].questionOption.id //todo
                                        )
                                    }
                                }
                            }
                        } else if (currentQuestion.answers.size == 3) {
                            multipleChoice3 {
                                id("choice")
                                option1(currentQuestion.answers[0].optionChoice.optionChoiceName)
                                option2(currentQuestion.answers[1].optionChoice.optionChoiceName)
                                option3(currentQuestion.answers[2].optionChoice.optionChoiceName)
                                onBind { model, view, position ->
                                    view.dataBinding.root.radio_group.clearCheck()
                                    view.dataBinding.root.radio_group.setOnCheckedChangeListener { group, checkedId ->
                                        var index = -1
                                        when (checkedId) {
                                            R.id.optionButton1 -> index = 0
                                            R.id.optionButton2 -> index = 1
                                            R.id.optionButton3 -> index = 2
                                        }
                                        surveyViewModel.setAnswer(
                                                currentQuestion.question.id,
                                                currentQuestion.answers[index].optionChoice.optionChoiceName,
                                                currentQuestion.answers[index].questionOption.id //todo
                                        )
                                    }
                                }
                            }
                        }

                    3 -> numeric {
                        id("numeric")
                        onBind { model, view, position ->
                            view.dataBinding.root.number_picker.maxValue = 10
                            view.dataBinding.root.number_picker.minValue = 0
                            view.dataBinding.root.number_picker.setOnValueChangedListener { picker, oldVal, newVal ->
                                surveyViewModel.setAnswer(
                                        currentQuestion.question.id,
                                        newVal.toString(),
                                        currentQuestion.answers.first().questionOption.id //todo
                                )
                            }
                        }
                    }

                }
            }

            view?.navigation_forward_button_1?.setImageResource(if (surveyViewModel.currentPosition != (list.size - 1)) R.drawable.ic_arrow_forward_white_24dp else R.drawable.ic_check_white_24dp)

            view?.navigation_forward_button_1?.setOnClickListener {
                if (surveyViewModel.isAnswered(currentQuestion.question.id)) {
                    if (surveyViewModel.createTodo()) { //todo: anpassen wenn yes/no question geändert
                        val answerToCheck: Answer =
                                surveyViewModel.answerToID(currentQuestion.question.id)!!
                        val dialog = TodoDialog(
                                surveyViewModel.interviewee,
                                currentQuestion.title,
                                context!!,
                                this
                        )
                        dialog.show(childFragmentManager, "todo_in_survey")
                    } else {
                        surveyViewModel.listOfAnsweredQuestions += surveyViewModel.currentPosition
                        val hasNext = surveyViewModel.nextQuestion()
                        if (hasNext) {
                            val action =
                                    QuestionFragmentDirections.actionQuestionFragmentSelf(
                                            surveyId,
                                            intervieweeId = args.intervieweeId
                                    )
                            findNavController().navigate(action)
                        } else {
                            Snackbar.make(
                                    view!!.rootView.findViewById(R.id.nav_host_fragment),
                                    getString(R.string.message_monitoring_completed),
                                    Snackbar.LENGTH_LONG
                            ).show()
                            (activity as MainActivity).show_bottom_navigation()
                            val action =
                                    QuestionFragmentDirections.actionQuestionFragmentToIntervieweeDetailFragment(
                                            intervieweeId = args.intervieweeId
                                    )
                            findNavController().navigate(action)
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

            view?.navigation_forward_button_left?.isGone = surveyViewModel.currentPosition == 0

            view?.navigation_forward_button_left?.setOnClickListener {
                if (surveyViewModel.currentPosition != 0) {
                    surveyViewModel.previousQuestion()
                    val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                            surveyId,
                            intervieweeId = args.intervieweeId
                    )
                    findNavController().navigate(action)
                }
            }
        })

    }

    override fun onDismiss(dialog: DialogInterface?) {
        println("onDismissed called")
        surveyViewModel.listOfAnsweredQuestions += surveyViewModel.currentPosition
        val hasNext = surveyViewModel.nextQuestion()
        if (hasNext) {
            val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                    surveyId,
                    intervieweeId = args.intervieweeId
            )
            findNavController().navigate(action)
        } else {
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

    val REQUEST_TAKE_PHOTO = 1
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //save the image path in our database..
            //set image to iamgeview
            //store the file
            surveyViewModel.setAnswer(
                    currentQuestion.question.id,
                    currentQuestion.answers.first().optionChoice.optionChoiceName,
                    currentQuestion.answers.first().questionOption.id, //todo
                    currentPhotoPath
            )
        }
    }

}
