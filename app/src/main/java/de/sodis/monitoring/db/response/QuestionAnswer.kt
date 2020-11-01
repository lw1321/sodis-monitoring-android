package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.entity.QuestionImage
import de.sodis.monitoring.db.entity.QuestionOptionChoice

data class QuestionAnswer(
    var title: String,
    val question: Question,
    val answers: List<QuestionOptionChoice>,
    val image: QuestionImage?
)