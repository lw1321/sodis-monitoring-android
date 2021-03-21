package de.sodis.monitoring.api.models

data class QuestionJson(
    val dependentQuestionId: Int?,
    val dependentQuestionOptionId: Int?,
    val id: Int,
    val inputType: InputType,
    val questionImage: QuestionImage?,
    val questionName: String,
    val questionOptions: List<QuestionOption>,
    val surveySection: SurveySection
) {
    data class InputType(
        val id: Int,
        val inputTypeName: String
    )

    data class QuestionOption(
        val id: Int,
        val optionChoice: OptionChoice
    ) {
        data class OptionChoice(
            val id: Int,
            val optionChoiceName: String
        )
    }

    data class QuestionImage(
        val id: Int,
        val url: String
    )

    data class SurveySection(
        val id: Int,
        val sectionName: String
    )
}
