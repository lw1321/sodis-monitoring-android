package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.IntervieweeTechnology
import de.sodis.monitoring.db.response.IntervieweeTechnologyDetail

@Dao
interface IntervieweeTechnologyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(intervieweeTechnology: IntervieweeTechnology)

    @Query("SELECT IntervieweeTechnology.stateKnowledge,IntervieweeTechnology.stateTechnology, Technology.name, IntervieweeTechnology.id, IntervieweeTechnology.technologyId FROM IntervieweeTechnology JOIN Technology ON IntervieweeTechnology.technologyId=Technology.id WHERE intervieweeId=:intervieweeId")
    fun getByInterviewee(intervieweeId: Int):List<IntervieweeTechnologyDetail>
}
