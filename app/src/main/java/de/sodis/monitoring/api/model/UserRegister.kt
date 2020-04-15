package de.sodis.monitoring.api.model

data class UserRegister(
    val firstName: String? = null,
    val lastName: String? = null,
    val type: Int = 0,
    val id: Int? = null,
    val firebaseId: String? = null,
    val email: String? = null
)
