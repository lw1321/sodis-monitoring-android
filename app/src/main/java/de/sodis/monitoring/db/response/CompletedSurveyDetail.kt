package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.entity.QuestionImage

data class CompletedSurveyDetail(
    val answerText: String?,
    val questionName: String,
    val sectionName: String,
    val inputType: Int,
    val path: String?
)