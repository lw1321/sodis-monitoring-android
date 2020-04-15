package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.UserDao
import de.sodis.monitoring.db.entity.User

class UserRepository(
    private val monitoringApi: MonitoringApi,
    private val userDao: UserDao
) {

    suspend fun register(user: User) {
        monitoringApi.registerUser(user)
    }

    suspend fun loadAllUsers() {
        //get all users from api
        val usersList = monitoringApi.getAllUsers()
        //save users in local database
        usersList.forEach {
            userDao.insert(it)
        }

    }
}