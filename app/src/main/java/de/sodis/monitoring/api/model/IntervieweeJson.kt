package de.sodis.monitoring.api.model

data class IntervieweeJson(
    val id: Int,
    val name: String,
    val village: Village,
    val intervieweeTechnologies: List<IntervieweeTechnology>,
    val girlsCount: Int,
    val boysCount: Int,
    val youngMenCount: Int,
    val youngWomenCount: Int,
    val oldMenCount: Int,
    val hasKnowledge: Boolean,
    val oldWomenCount: Int,
    val menCount: Int,
    val womenCount: Int
    ) {
    data class Village(
        val id: Int,
        val name: String
    )

    data class IntervieweeTechnology(
        val id: Int,
        val technology: SurveyHeaderJson.Technology,
        val stateTechnology: Int,
        val stateKnowledge: Int
    )
}