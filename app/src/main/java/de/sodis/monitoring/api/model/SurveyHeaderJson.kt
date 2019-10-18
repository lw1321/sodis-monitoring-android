package de.sodis.monitoring.api.model

data class SurveyHeaderJson(
    val id: Int,
    val instructions: String,
    val otherHeaderInfo: String,
    val surveyName: String,
    val surveySection: List<SurveySectionJson>
) {
    data class SurveySectionJson(
        val id: Int,
        val questions: List<QuestionJson>,
        val sectionName: String,
        val sectionSubheading: String,
        val sectionTitle: String
    ) {
        data class QuestionJson(
            val dependentQuestionId: Int,
            val dependentQuestionOptionId: Int,
            val id: Int,
            val inputType: InputTypeJson,
            val questionImage: QuestionImageJson,
            val questionName: String,
            val questionOptions: List<QuestionOptionJson>,
            val questionSubtext: String
        ) {
            data class InputTypeJson(
                val id: Int,
                val inputTypeName: String
            )

            data class QuestionImageJson(
                val id: Int,
                val url: String
            )

            data class QuestionOptionJson(
                val id: Int,
                val optionChoice: OptionChoiceJson
            ) {
                data class OptionChoiceJson(
                    val id: Int,
                    val optionChoiceName: String
                )
            }
        }
    }
}