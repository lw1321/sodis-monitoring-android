package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Village

data class IntervieweeDetail(
    var interviewee: Interviewee,
    var intervieweeTechnologies: List<IntervieweeTechnologyDetail>,
    var village: Village
    )