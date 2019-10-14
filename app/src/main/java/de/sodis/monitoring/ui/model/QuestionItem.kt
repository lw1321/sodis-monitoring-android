package de.sodis.monitoring.ui.model


class QuestionItem(
    val title: String,
    val questionText: String,
    val imageUri: String?
) : SodisItem(TYPE.TYPE_QUESTION, childItemList = emptyList())
