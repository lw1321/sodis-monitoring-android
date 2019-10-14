package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.OptionChoice
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.entity.QuestionImage

data class QuestionAnswer(
    var title: String,
    val question: Question,
    val answers: List<OptionChoice>,
    val image: QuestionImage
)