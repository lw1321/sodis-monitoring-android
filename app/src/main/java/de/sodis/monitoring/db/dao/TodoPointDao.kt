package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.CalendarConverter
import de.sodis.monitoring.db.entity.TodoPoint
import java.util.*

@Dao
@TypeConverters(CalendarConverter::class)
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
    fun getTasksByInterviewee(familyId: String): LiveData<List<TodoPoint>>

    @Delete
    fun delete(todoPoint: TodoPoint)

    @Query("SELECT * FROM TodoPoint ORDER BY family ASC")
    fun getTodoPointsSortedByInterviewee():LiveData<List<TodoPoint>>

    @Query("SELECT * FROM TodoPoint ORDER BY duedate ASC")
    fun getTodoPointsSortedByDueDate():LiveData<List<TodoPoint>>

    @Query("SELECT * FROM TodoPoint WHERE donedate>=:calendar OR done=0")
    fun getUndoneTodoPoints(calendar: Calendar):LiveData<List<TodoPoint>>

    @Query("SELECT * FROM TodoPoint WHERE donedate>=:calendar OR done=0  ORDER BY duedate ASC")
    fun getUndoneTodoPointsSortedByDueDate(calendar: Calendar):LiveData<List<TodoPoint>>

    @Query("SELECT * FROM TodoPoint WHERE donedate>=:calendar OR done=0 ORDER BY family ASC")
    fun getUndoneTodoPointsSortedByFamily(calendar: Calendar):LiveData<List<TodoPoint>>

    @Query("SELECT * FROM TodoPoint WHERE donedate>=:calendar OR done=0 ORDER BY village ASC")
    fun getUndoneTodoPointsSortedByVillage(calendar: Calendar):LiveData<List<TodoPoint>>

    @Query("SELECT * FROM TodoPoint WHERE family=:id AND done=0 ORDER BY duedate ASC")
    fun getUndoneTasksByInterviewee(id: String):LiveData<List<TodoPoint>>

}