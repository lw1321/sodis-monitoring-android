package de.sodis.monitoring.db.response

import androidx.lifecycle.LiveData
import de.sodis.monitoring.db.entity.*

data class IntervieweeDetail(
    var interviewee: Interviewee,
    var village: Village,
    var user: User?,
    var todoPoints: List<TodoPoint>?
)