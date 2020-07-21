package de.sodis.monitoring.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import de.sodis.monitoring.R
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.TodoPoint
import de.sodis.monitoring.repository.TodoPointRepository
import de.sodis.monitoring.todolist.*
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.TodoPointModel
import java.util.*
import kotlin.concurrent.thread

class TodoPointFragment: Fragment(
) {

    private val todoPointModel: TodoPointModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(TodoPointModel::class.java)
    }

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }

    lateinit var todoPoints: MutableList<TodoPoint>

    var sortedByDate: Boolean = true

    lateinit var recyclerView: RecyclerView


    lateinit var materialButtonToggleGroup: MaterialButtonToggleGroup

    lateinit var layoutManager: LinearLayoutManager

    lateinit var todoListAdapter: TodoListAdapter

    lateinit var addButton: Button
    val onAddPressed: View.OnClickListener

    lateinit var todoPointRepository: TodoPointRepository

    lateinit var applicationContext: Context

    init {
        onAddPressed = View.OnClickListener {
            var dialog = TodoDialog(null, null,applicationContext)
            dialog.show(childFragmentManager, "todo")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.todoPoints = mutableListOf()

    }

    var switchBoolean: Boolean = false

    fun deletePoint(pos: Int) {
        if(materialButtonToggleGroup.checkedButtonId == R.id.toggle_button_duedate) {
            val toDelete = todoPointModel.undoneTodoPointsByDueDate.value!![pos]
            //todoListAdapter.notifyDataSetChanged()
            Thread(Runnable{
                todoPointModel.deleteTodoPoint(toDelete)
            }).start()
        }
        else if(materialButtonToggleGroup.checkedButtonId == R.id.toggle_button_family){
            val toDelete = todoPointModel.undoneTodoPointsByFamily.value!![pos]
            //todoListAdapter.notifyDataSetChanged()
            Thread(Runnable{
                todoPointModel.deleteTodoPoint(toDelete)
            }).start()
        }
        else {
            val toDelete = todoPointModel.undoneTodoPointsByVillage.value!![pos]
            //todoListAdapter.notifyDataSetChanged()
            Thread(Runnable{
                todoPointModel.deleteTodoPoint(toDelete)
            }).start()
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
        this.materialButtonToggleGroup = view.findViewById(R.id.todo_toggle_group)
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
        this.todoListAdapter = TodoListAdapter(todoPoints, context, todoPointModel, intervieweeModel)
        this.recyclerView.adapter = this.todoListAdapter
        todoPointModel.undoneTodoPointsByDueDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            todoListAdapter.setDataSet(it)
        })
        this.materialButtonToggleGroup.check(R.id.toggle_button_duedate)
        this.materialButtonToggleGroup.addOnButtonCheckedListener{
                group, id, ischecked ->
                println(id)
                println(ischecked.toString())
                if(ischecked) {
                    if(id == R.id.toggle_button_duedate) {
                        todoPointModel.undoneTodoPointsByFamily.removeObservers(viewLifecycleOwner)
                        todoPointModel.undoneTodoPointsByVillage.removeObservers(viewLifecycleOwner)
                        todoPointModel.undoneTodoPointsByDueDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            todoListAdapter.setDataSet(it)
                        })
                    }
                    else if(id == R.id.toggle_button_family) {
                        todoPointModel.undoneTodoPointsByVillage.removeObservers(viewLifecycleOwner)
                        todoPointModel.undoneTodoPointsByDueDate.removeObservers(viewLifecycleOwner)
                        todoPointModel.undoneTodoPointsByFamily.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            todoListAdapter.setDataSet(it)
                        })
                    }
                    else {
                        todoPointModel.undoneTodoPointsByFamily.removeObservers(viewLifecycleOwner)
                        todoPointModel.undoneTodoPointsByDueDate.removeObservers(viewLifecycleOwner)
                        todoPointModel.undoneTodoPointsByVillage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            todoListAdapter.setDataSet(it)
                        })
                    }
                }

            if(this.materialButtonToggleGroup.checkedButtonIds.size == 0) {
                this.materialButtonToggleGroup.check(id)
            }

        }
        return view
    }

}