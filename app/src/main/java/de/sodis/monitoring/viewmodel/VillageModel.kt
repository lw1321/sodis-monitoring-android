package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.repository.SurveyRepository

class VillageModel(application: Application) : AndroidViewModel(application) {
    var villageList: LiveData<List<Village>>
    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val villageDao = monitoringDatabase.villageDao()
    init {
        villageList = villageDao.getAll()
    }
}
