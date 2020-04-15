package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.UserRegister

class UserRepository(
    private val monitoringApi: MonitoringApi
) {

    suspend fun register(userRegister: UserRegister){
        //Todo register user via sodis api
        monitoringApi.registerUser(userRegister)
    }

}