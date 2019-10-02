package de.sodis.monitoring.ui.model


class QuestionItem(
    val title: String,
    val questionText: String,
    val imageUri: String?,
    questionType: String,
    answerOptions: List<SodisItem> = emptyList()
) : SodisItem(TYPE.TYPE_QUESTION, childItemList = answerOptions)
