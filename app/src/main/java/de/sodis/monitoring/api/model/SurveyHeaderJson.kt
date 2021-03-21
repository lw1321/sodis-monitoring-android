package de.sodis.monitoring.api.model

data class SurveyHeaderJson(
    val id: Int,
    val surveyName: String,
    val surveySection: List<SurveySectionJson>,
    val technology: Technology
) {
    data class SurveySectionJson(
        val id: Int,
        val questions: List<QuestionJson>,
        val sectionName: String
    ) {
        data class QuestionJson(
            val dependentQuestionId: Int,
            val dependentQuestionOptionId: Int,
            val id: Int,
            val inputType: InputTypeJson,
            val questionImage: QuestionImageJson?,
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

    data class Technology(
        val id:Int,
        val name: String
    )
}