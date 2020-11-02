package de.sodis.monitoring.repository

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import coil.Coil
import coil.api.get
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class QuestionImageRepository(
    private val questionImageDao: QuestionImageDao,
    private val monitoringApi: MonitoringApi
) {

    //TODO extract method to bitmap extension?!
    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap, context: Context): String? {
        // Get the context wrapper
        val wrapper = ContextWrapper(context.applicationContext)

        // Initialize a new file instance to save bitmap object
        val file = File(wrapper.getDir("Images", Context.MODE_PRIVATE), "${UUID.randomUUID()}.jpg")
        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath).encodedPath
    }

    /**
     * If internet connection is available, load all question images
     */
    suspend fun downloadQuestionImages(context: Context) {
        val allNotDownloadedList = questionImageDao.getAllNotDownloaded()
        //loop through surveys
        for (questionImage: QuestionImage in allNotDownloadedList) {
            //save survey Header
            //load bitmap from url
            val bitmap = Coil.get(questionImage.url).toBitmap()

            //write bitmap to file
            val absolutePath = bitmapToFile(bitmap, context)
            questionImage.path = absolutePath
            //get file path
            //Save Images
            questionImageDao.update(
                questionImage
            )
        }
    }

    /**
     * If internet connection is available, load all question images
     */
    suspend fun downloadMetaData() {
        val response = monitoringApi.getQuestionImages()

        //loop through surveys
        for (questionImage: SurveyHeaderJson.SurveySectionJson.QuestionJson.QuestionImageJson in response) {
            //save survey Header
            //load bitmap from url
            if (questionImageDao.exists(questionImage.id) == 0) {
                //get file path
                //Save Images
                questionImageDao.insert(
                    QuestionImage(
                        id = questionImage.id,
                        url = questionImage.url,
                        path = null
                    )
                )
            }

        }
    }
}