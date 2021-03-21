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


}