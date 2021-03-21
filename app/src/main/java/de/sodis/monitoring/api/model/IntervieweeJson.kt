package de.sodis.monitoring.api.model


data class IntervieweeJson(
    val id: String,
    val name: String,
    val village: Village,
    val user: User?,
    val imageUrl: String?
) {
    data class Village(
        val id: Int,
        val name: String
    )
    data class User(
        val id: Int,
        val firstName: String?,
        val lastName: String?,
        val firebaseId: String?,
        val email: String?,
        val type: Int
    )

}