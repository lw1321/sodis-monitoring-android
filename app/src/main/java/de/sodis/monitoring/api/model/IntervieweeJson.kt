package de.sodis.monitoring.api.model

data class IntervieweeJson(
    val id: Int,
    val name: String,
    val village: Village
) {
    data class Village(
        val id: Int,
        val name: String
    )
}