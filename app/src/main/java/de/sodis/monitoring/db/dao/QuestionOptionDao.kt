package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.OptionChoice
import de.sodis.monitoring.db.entity.QuestionOption
import de.sodis.monitoring.db.response.QuestionOptionResponse

@Dao
interface QuestionOptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionOption: QuestionOption)

    @Query("SELECT * FROM OptionChoice INNER JOIN QuestionOption ON OptionChoice.id=QuestionOption.optionChoiceId WHERE QuestionOption.questionId=:id")
    fun getByQuestion(id: Int): List<OptionChoice>

    @Query("SELECT QuestionOption.*, OptionChoice.* FROM QuestionOption LEFT JOIN  OptionChoice  ON QuestionOption.optionChoiceId=OptionChoice.id WHERE QuestionOption.questionId=:id ")
    fun getOptionsByQuestion(id: Int): List<QuestionOptionResponse>

    @Query("SELECT * FROM QuestionOption WHERE questionId=:id")
    fun getQuestionOptionsByQuestion(id: Int): List<QuestionOption>


}
