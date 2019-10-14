package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.OptionChoice
import de.sodis.monitoring.db.entity.QuestionOption

@Dao
interface QuestionOptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionOption: QuestionOption)

    @Query("SELECT * FROM OptionChoice INNER JOIN QuestionOption ON OptionChoice.id=QuestionOption.optionChoiceId WHERE QuestionOption.questionId=:id")
    fun getByQuestion(id: Int): List<OptionChoice>

}
