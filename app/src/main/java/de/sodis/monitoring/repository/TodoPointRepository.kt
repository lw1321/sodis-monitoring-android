package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.TodoPointDao
import de.sodis.monitoring.db.entity.TodoPoint
import java.util.*

class TodoPointRepository (private val todoPointDao: TodoPointDao, private val monitoringApi: MonitoringApi) {
    fun getAllTodoPoints():LiveData<List<TodoPoint>> {
        return todoPointDao.getAll()
    }

    fun getTodoPointsSortedByID():LiveData<List<TodoPoint>> {
        return todoPointDao.getTodoPointsSortedByInterviewee()
    }

    fun getTodoPointsByDueDate():LiveData<List<TodoPoint>> {
        return todoPointDao.getTodoPointsSortedByDueDate()
    }

    fun deleteTodoPoint(todoPoint: TodoPoint) {
        return todoPointDao.delete(todoPoint)
    }

    fun updateTodoPoint(todoPoint: TodoPoint):Int {
        return todoPointDao.update(todoPoint)
    }

    fun insertTodoPoint(todoPoint: TodoPoint) {
        return todoPointDao.insert(todoPoint)
    }

    fun getUndoneTodoPoints():LiveData<List<TodoPoint>> {
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return todoPointDao.getUndoneTodoPoints(calendar)
    }

    fun getUndoneTodoPointsByFamily():LiveData<List<TodoPoint>> {
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return todoPointDao.getUndoneTodoPointsSortedByFamily(calendar)
    }

    fun getUndoneTodoPointsByDueDate():LiveData<List<TodoPoint>> {
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return todoPointDao.getUndoneTodoPointsSortedByDueDate(calendar)
    }

    fun getUndoneTodoPointsByVillage():LiveData<List<TodoPoint>> {
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return todoPointDao.getUndoneTodoPointsSortedByVillage(calendar)
    }


}