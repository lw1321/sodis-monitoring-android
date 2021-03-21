package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.*

data class IntervieweeDetail(
    var interviewee: Interviewee,
    var village: Village,
    var user: User?
)