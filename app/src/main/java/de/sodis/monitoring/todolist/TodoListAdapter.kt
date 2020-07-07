package de.sodis.monitoring.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.db.entity.TodoPoint
import de.sodis.monitoring.repository.TodoPointRepository
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class TodoListAdapter(@NonNull todoPointS: List<TodoPoint>, @NonNull context: Context?, @NonNull todoPointRepository: TodoPointRepository): RecyclerView.Adapter<TodoListViewHolder>() {
    var context: Context?
    var todoPoints: List<TodoPoint>
    var todoPointRepository: TodoPointRepository

    init {
        this.todoPoints = todoPointS
        this.context = context
        this.todoPointRepository = todoPointRepository
    }

    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    fun onRadioButtonClicked(view: View, position: Int) {
        if (view is RadioButton) {
            val checked = view.isChecked
            todoPoints[position].done = checked
            updateAsync(todoPoints[position])
        }
    }

    fun updateAsync(todoPoint: TodoPoint){
        thread (start = true){
            todoPointRepository.updateTodoPoint(todoPoint)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        var toReturnView: View = LayoutInflater.from(context).inflate(
            R.layout.todo_item_layout, parent, false
        )
        return TodoListViewHolder(toReturnView)
    }

    override fun getItemCount(): Int {
        return todoPoints.size
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.checkBox.isChecked = todoPoints[position].done?:false
        holder.checkBox.setOnClickListener {
            onRadioButtonClicked(it, position)
        }
        holder.contentField.text = todoPoints[position].text
        holder.dueField.text = simpleDateFormat.format(todoPoints[position].duedate?.time)
        holder.familyField.text = todoPoints[position].family.toString()
    }

}


