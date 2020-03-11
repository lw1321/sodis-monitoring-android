package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Task
import de.sodis.monitoring.db.entity.Technology
import de.sodis.monitoring.db.entity.Village

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Query("SELECT COUNT(*) FROM Technology WHERE id=:id")
    fun count(id: Int): Int

    @Query("SELECT * FROM Task")
    fun getAll(): LiveData<List<Task>>

    @Update
    fun update(task: Task): Int

    @Query("SELECT * FROM Task JOIN IntervieweeTechnology ON Task.intervieweeTechnologyId==IntervieweeTechnology.id WHERE IntervieweeTechnology.intervieweeId=:intervieweeId")
    fun getTasksByInterviewee(intervieweeId: Int): List<Task>

}