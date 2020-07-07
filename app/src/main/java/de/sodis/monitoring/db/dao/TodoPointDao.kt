package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.Task
import de.sodis.monitoring.db.entity.TodoPoint

@Dao
interface TodoPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(todoPoint: TodoPoint)

    @Query("SELECT COUNT(*) FROM TodoPoint WHERE id=:id")
    fun count(id: Int): Int

    @Query("SELECT * FROM TodoPoint")
    fun getAll(): LiveData<List<TodoPoint>>

    @Update
    fun update(todoPoint: TodoPoint): Int

    @Query("SELECT * FROM TodoPoint WHERE family=:familyId")
    fun getTasksByInterviewee(familyId: Int): List<TodoPoint>

    @Delete
    fun delete(todoPoint: TodoPoint)

    @Query("SELECT * FROM TodoPoint ORDER BY family ASC")
    fun getTodoPointsSortedByInterviewee():List<TodoPoint>

    @Query("SELECT * FROM TodoPoint ORDER BY duedate ASC")
    fun getTodoPointsSortedByDueDate():List<TodoPoint>

}