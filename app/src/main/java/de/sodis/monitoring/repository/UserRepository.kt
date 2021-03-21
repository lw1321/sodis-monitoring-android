package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.UserDao
import de.sodis.monitoring.db.entity.User

class UserRepository(
    private val monitoringApi: MonitoringApi
) {

    suspend fun register(user: User) {
        monitoringApi.registerUser(user)
    }

}