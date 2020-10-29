package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.StatsDao

class StatsRepository(private val monitoringApi: MonitoringApi, private val statsDao: StatsDao) {

    suspend fun dataUpdateAvailable(): Boolean {
        //monito
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
        val stats = statsDao.getById(0)
        stats.modificationDate = System.currentTimeMillis()
        statsDao.update(stat = stats)
    }
}
