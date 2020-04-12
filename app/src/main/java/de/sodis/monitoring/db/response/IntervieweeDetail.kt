package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.*

data class IntervieweeDetail(
    var interviewee: Interviewee,
    var intervieweeTechnologies: List<IntervieweeTechnologyDetail>,
    var village: Village,
    var sector: Sector?,
    var localExpert: LocalExpert,
    var tasks: List<Task>
)