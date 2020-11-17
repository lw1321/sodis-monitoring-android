package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.IntervieweeTechnology
import de.sodis.monitoring.db.response.IntervieweeTechnologyDetail

@Dao
interface IntervieweeTechnologyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(intervieweeTechnology: IntervieweeTechnology)

    @Query("SELECT IntervieweeTechnology.stateKnowledge,IntervieweeTechnology.stateTechnology, Technology.name, IntervieweeTechnology.id, IntervieweeTechnology.technologyId FROM IntervieweeTechnology JOIN Technology ON IntervieweeTechnology.technologyId=Technology.id WHERE intervieweeId=:intervieweeId")
    fun getByInterviewee(intervieweeId: String): List<IntervieweeTechnologyDetail>

    @Query("SELECT * FROM IntervieweeTechnology WHERE intervieweeId=:intervieweeId AND technologyId=:technologyId")
    fun getByIntervieweeAndTechnoology(intervieweeId: String, technologyId: Int): IntervieweeTechnology?

    @Update
    fun update(intervieweeTechnology: IntervieweeTechnology)

    @Query("SELECT * FROM IntervieweeTechnology WHERE intervieweeId=:intervieweeId")
    fun getByIntervieweeLive(intervieweeId: String): LiveData<List<IntervieweeTechnology>>
}
