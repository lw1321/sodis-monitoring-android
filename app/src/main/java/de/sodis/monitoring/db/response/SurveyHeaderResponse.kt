package de.sodis.monitoring.db.response

import androidx.room.Embedded
import androidx.room.Relation
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.entity.SurveySection


data class SurveyHeaderResponse (
    @Embedded
    val surveyHeader: SurveyHeader,
    @Relation(
        parentColumn = "id",
        entityColumn = "surveyHeaderId",
        entity = SurveySection::class
    )
    val surveySectionList: List<SurveySection>
)
