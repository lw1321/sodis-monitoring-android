package de.sodis.monitoring.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.TodoPoint
import de.sodis.monitoring.repository.TodoPointRepository
import de.sodis.monitoring.todolist.*
import java.util.*
import kotlin.concurrent.thread

class TodoPointFragment: Fragment(
) {

    lateinit var todoPoints: MutableList<TodoPoint>

    var sortedByDate: Boolean = true

    lateinit var recyclerView: RecyclerView

    lateinit var layoutManager: LinearLayoutManager

    lateinit var todoListAdapter: TodoListAdapter

    lateinit var addButton: Button
    val onAddPressed: View.OnClickListener

    lateinit var todoPointRepository: TodoPointRepository

    lateinit var applicationContext: Context

    init {
        onAddPressed = View.OnClickListener {
            var dialog = TodoDialog(null, applicationContext)
            dialog.show(childFragmentManager, "todo")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //this.todoPoints = todoPointRepository.getTodoPointsByDueDate()
        this.todoPoints = mutableListOf(TodoPoint(1, true, Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), 1, "test"), TodoPoint(2, false, Calendar.getInstance(), Calendar.getInstance(), null, 1, "test"))

    }

    fun deletePoint(pos: Int) {
        val toDelete = todoPoints.removeAt(pos)
        thread (start = true) {
            todoPointRepository.deleteTodoPoint(toDelete)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_list, container, false)
        applicationContext = activity!!.applicationContext
        this.addButton = view.findViewById(R.id.add_todo_button)
        this.addButton.setOnClickListener(onAddPressed)
        this.todoPointRepository = TodoPointRepository(de.sodis.monitoring.db.MonitoringDatabase.getDatabase(requireContext()).todoPointDao(), MonitoringApi())
        this.recyclerView = view.findViewById(R.id.todo_recyclerview)
        this.layoutManager = LinearLayoutManager(context)
        this.recyclerView.layoutManager = this.layoutManager

        val swipe = object: TodoSwipeHelper(requireContext(), recyclerView, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<TodoDeleteButton>
            ) {
                buffer.add(
                    TodoDeleteButton(
                    requireContext(), object: TodoDeleteButtonListener{
                            override fun onClick(pos: Int) {
                                deletePoint(pos)
                            }
                        }
                )
                )
            }
        }
        this.todoListAdapter = TodoListAdapter(todoPoints, context, todoPointRepository)
        this.recyclerView.adapter = this.todoListAdapter
        return view
    }

}