package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.response.QuestionItem

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)

    @Query("SELECT * FROM Question ")
    fun getAll(): List<Question>

    @Query("SELECT * FROM Question WHERE surveySectionId IN (:surveySections)")
    fun getBySurveySections(surveySections: List<Int>): List<Question>

    @Query("SELECT * FROM Question JOIN QuestionOption ON QuestionOption.questionId=Question.id WHERE QuestionOption.id=:questionOptionId")
    fun getByQuestionOptionId(questionOptionId: Int?): Question?

    @Query("DELETE FROM Question WHERE id not in (:ids)")
    fun deleteAllExcluded(ids: List<Int>)

    @Query("SELECT q.id, q.questionName as name, q.dependentQuestionId, q.dependentQuestionOptionId, qi.path, it.id as inputTypeId, it.inputTypeName as inputTypeName, qo.id as questionOptionId, oc.optionChoiceName FROM Question q LEFT JOIN InputType it ON it.id = q.inputTypeId LEFT JOIN SurveySection ss ON ss.id = q.surveySectionId LEFT JOIN SurveyHeader sh ON sh.id = ss.surveyHeaderId LEFT JOIN QuestionImage qi ON qi.id = q.questionImageId LEFT JOIN QuestionOption qo ON qo.QuestionId = q.id LEFT JOIN OptionChoice oc ON oc.id = qo.optionChoiceId WHERE sh.id = :surveyId")
    fun getQuestions(surveyId: Int): List<QuestionItem>

    @Query("SELECT q.id FROM Question q LEFT JOIN SurveySection ss ON ss.id = q.surveySectionId LEFT JOIN Surveyheader sh ON sh.id = ss.surveyHeaderId WHERE sh.id = :surveyId")
    fun getBySurveyHeader(surveyId: Int): List<Int>

    @Query("SELECT sh.id FROM Question q LEFT JOIN SurveySection ss ON ss.id = q.surveySectionId LEFT JOIN SurveyHeader sh ON sh.id = ss.surveyHeaderId WHERE q.id=:id")
    fun findSurveyByQuestion(id: Int) : Int

}
