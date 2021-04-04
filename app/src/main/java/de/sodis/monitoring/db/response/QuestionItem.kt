package de.sodis.monitoring.db.response

data class QuestionItem (
    val id: Int,
    val name: String,
    val dependentQuestionId: Int?,
    val dependentQuestionOptionId: Int?,
    val path: String?,
    val inputTypeId: Int,
    val inputTypeName: String,
    val questionOptionId: Int?,
    val optionChoiceName: String?
)
