package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import de.sodis.monitoring.repository.worker.DownloadWorker
import de.sodis.monitoring.repository.worker.UploadWorker

class RootViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {

    lateinit var workInfoByIdLiveData: LiveData<WorkInfo>
    fun requestData(){
        //start worker
        //start worker manager
        // Check if there is local data (dependencies on intervieee infos, completed_surveys)
        // which is not pushed to Server before downloading new data
        // to provide conflicts!
        //start worker manager
        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()
        WorkManager.getInstance(mApplication.applicationContext).enqueue(uploadWorkRequest)

        // download new data if available

        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()
        WorkManager.getInstance(mApplication.applicationContext).enqueue(downloadWorkRequest)

        val workerId = downloadWorkRequest.id
        workInfoByIdLiveData = WorkManager.getInstance(mApplication.applicationContext)
            .getWorkInfoByIdLiveData(workerId)

    }
}