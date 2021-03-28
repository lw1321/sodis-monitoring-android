package de.sodis.monitoring.db.response

import androidx.lifecycle.LiveData
import de.sodis.monitoring.db.entity.*

data class IntervieweeDetail(
    var interviewee: Interviewee,
    var intervieweeTechnologies: List<IntervieweeTechnologyDetail>,
    var village: Village,
    var user: User?,
    var todoPoints: List<TodoPoint>?
)