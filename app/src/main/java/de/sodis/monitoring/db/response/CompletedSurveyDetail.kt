package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.entity.QuestionImage
import de.sodis.monitoring.db.entity.QuestionOptionChoice

data class CompletedSurveyDetail(
    val question: Question,
    val answer: Answer,
    val title: String,
    val image: QuestionImage
)