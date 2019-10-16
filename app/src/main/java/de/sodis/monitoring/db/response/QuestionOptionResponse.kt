package de.sodis.monitoring.db.response

import androidx.room.Embedded
import androidx.room.Relation
import de.sodis.monitoring.db.entity.OptionChoice
import de.sodis.monitoring.db.entity.QuestionOption

data class QuestionOptionResponse(

    @Relation(
        parentColumn = "id",
        entityColumn = "optionChoiceId",
        entity = QuestionOption::class)
    val questionOption: List<QuestionOption>,
    @Embedded
    val optionChoice: OptionChoice
)
