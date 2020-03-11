package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.sodis.monitoring.repository.worker.DownloadWorker

class RootViewModel(application: Application) : AndroidViewModel(application) {

    init {
        //start worker
        //start worker manager
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()
        WorkManager.getInstance(application.applicationContext).enqueue(downloadWorkRequest)

    }
}