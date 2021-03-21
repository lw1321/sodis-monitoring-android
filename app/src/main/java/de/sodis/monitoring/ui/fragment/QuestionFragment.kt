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
import android.util.Log
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


        view?.navigation_forward_button_left?.setOnClickListener {
            val action = QuestionFragmentDirections.actionQuestionFragmentSelf(
                surveyId,
                intervieweeId = args.intervieweeId
            )
            findNavController().navigate(action)
        }
    }

    private fun moveForward() {
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

    override fun onDismiss(dialog: DialogInterface?) {

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
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
        return imageFile
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
            //TODO SET ANSWER
            moveForward()
        }
    }

}
