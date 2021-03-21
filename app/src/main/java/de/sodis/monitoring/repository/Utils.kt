package de.sodis.monitoring.repository

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.api.get
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class Utils {
    companion object {
        suspend fun urlToFile(imageUrl: String, context: Context): String? {
            // Get the context wrapper
            val wrapper = ContextWrapper(context.applicationContext)
            //load bitmap from url
            val bitmap = Coil.get(imageUrl).toBitmap()
            // Initialize a new file instance to save bitmap object
            val file =
                File(wrapper.getDir("Images", Context.MODE_PRIVATE), "${UUID.randomUUID()}.jpg")
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
    }

}
