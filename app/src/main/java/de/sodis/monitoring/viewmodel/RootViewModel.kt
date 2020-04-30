package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import de.sodis.monitoring.repository.worker.DownloadWorker

class RootViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {

    lateinit var workInfoByIdLiveData: LiveData<WorkInfo>
    //todo api endpoint to check if there are updated data before downloading all
    fun requestData(){
        //start worker
        //start worker manager
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()
        WorkManager.getInstance(mApplication.applicationContext).enqueue(downloadWorkRequest)

        val workerId = downloadWorkRequest.id
        workInfoByIdLiveData = WorkManager.getInstance(mApplication.applicationContext)
            .getWorkInfoByIdLiveData(workerId)

    }
}