package de.sodis.monitoring.ui.fragment

import android.app.Activity
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

class QuestionFragment : BaseListFragment() {


    private val surveyViewModel: SurveyViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(SurveyViewModel::class.java)
    }


    val args: QuestionFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surveyViewModel.startSurvey()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        //Survey logic
        surveyViewModel.question.observe(viewLifecycleOwner, Observer { questionList ->
            if (questionList != null) {
                createQuestion(questionList)
            }
        })

        //TODO add move forward and backward UI and Logic
        view?.navigation_forward_button_1?.setOnClickListener {
            //TODO check if answer is set
            surveyViewModel.position += 1
            val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                surveyId = args.surveyId,
                intervieweeId = args.intervieweeId,
                position = args.position + 1
            )
            findNavController().navigate(action)
        }
        view?.navigation_forward_button_left?.setOnClickListener {
            surveyViewModel.position -= 1
            val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                surveyId = args.surveyId,
                intervieweeId = args.intervieweeId,
                position = args.position - 1
            )
            findNavController().navigate(action)
        }
        return view
    }

    private fun finishSurvey(view: View?) {
        surveyViewModel.requestLocationAndSaveSurvey()
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
                                surveyViewModel.setAnswer(
                                    questionOption = it.questionOptionId,
                                    answerText = null,
                                    imagePath = null
                                )
                                surveyViewModel.position += 1
                                val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                                    surveyId = args.surveyId,
                                    intervieweeId = args.intervieweeId,
                                    position = args.position + 1
                                )
                                findNavController().navigate(action)
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
                                surveyViewModel.setAnswer(
                                    questionOption = null,
                                    answerText = it!!.toString(),
                                    imagePath = null
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
            surveyViewModel.setAnswer(
                imagePath = currentPhotoPath,
                questionOption = null,
                answerText = null
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


}
