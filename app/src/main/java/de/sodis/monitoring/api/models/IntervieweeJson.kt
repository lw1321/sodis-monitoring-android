package de.sodis.monitoring.api.models

data class IntervieweeJson(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val village: Village
) {
    data class Village(
        val id: Int,
        val name: String?
    )
}