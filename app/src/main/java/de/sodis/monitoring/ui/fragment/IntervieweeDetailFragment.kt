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
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.sodis.monitoring.default
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.PlaceViewModel
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.todo_dialog_layout.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
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
                        default {//TODO USE families view holder, set image creation routine
                            id(it.id)
                            text(it.name)
                            onClick { clicked ->
                            }
                        }
                    }
                    surveyList.forEach { survey ->
                        default {
                            id(survey.surveyId)
                            text(survey.surveyName + " / " + survey.projectName)
                            onClick { clicked ->
                                //go to survey
                                surveyViewModel.surveyId = survey.surveyId
                                val action =
                                    IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToQuestionFragment(
                                        intervieweeId = args.intervieweeId,
                                        surveyId = survey.surveyId,
                                        position = 0
                                    )
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
            })

        })

        view?.navigation_forward_button_1?.isGone = true
        view?.navigation_forward_button_left?.isGone = true
        return view
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
            //save the image path in our database..
            //set image to iamgeview
            //todo show image immediately, //recreate recyclerview
            //store the file
            placeViewModel.storeImagePath(currentPhotoPath)
            BitmapFactory.decodeFile(currentPhotoPath)?.also { bitmap ->
                view!!.imageView.setImageBitmap(bitmap)
            }
        }
    }

}
