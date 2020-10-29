package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.StatsDao
import de.sodis.monitoring.db.entity.Stats

class StatsRepository(private val monitoringApi: MonitoringApi, private val statsDao: StatsDao) {

    suspend fun dataUpdateAvailable(): Boolean {
        //monito
        if(statsDao.exists(0) == 0){
            return true
        }
        val serverStats = monitoringApi.getStats()
        val recentLocalSyn  = statsDao.getById(0).modificationDate
        if(serverStats.modificationDate > recentLocalSyn){
            return true
        }
        return false
    }

    fun updateLastSyncTime(){
        // data sync was successful now set the modificationDate of stats to the last updated time
        //edge case
        //TODO - new data available between last check and now. Solution: save the server modificationDate
        val stats = Stats(id = 0, modificationDate = System.currentTimeMillis())
        statsDao.insert(stat = stats)
    }
}
