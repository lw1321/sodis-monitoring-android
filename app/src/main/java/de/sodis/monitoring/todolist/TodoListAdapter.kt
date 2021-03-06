package de.sodis.monitoring.todolist


import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.db.entity.TodoPoint
import de.sodis.monitoring.viewmodel.PlaceViewModel
import de.sodis.monitoring.viewmodel.TodoPointModel
import java.text.SimpleDateFormat
import java.util.*

class TodoListAdapter(val activity: Activity, @NonNull todoPointS: List<TodoPoint>, @NonNull context: Context?, @NonNull todoPointModel: TodoPointModel, val placeViewModel: PlaceViewModel): RecyclerView.Adapter<TodoListViewHolder>() {
    var context: Context?
    var todoPoints: List<TodoPoint>
    val todoPointModel: TodoPointModel

    fun setDataSet(todoPointsNew: List<TodoPoint>) {
        todoPoints = todoPointsNew
        notifyDataSetChanged()
    }

    init {
        this.todoPoints = todoPointS
        this.context = context
        this.todoPointModel = todoPointModel
    }

    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    fun onRadioButtonClicked(view: View, position: Int) {
        if (view is CheckBox) {
            val checked = view.isChecked
            if(checked) {
                todoPoints[position].donedate = Calendar.getInstance()
            }
            else {
                todoPoints[position].donedate = null
            }
            todoPoints[position].done = checked
            updateAsync(todoPoints[position])
        }
    }

    fun updateAsync(todoPoint: TodoPoint){
        Thread(Runnable{
            todoPointModel.updateTodoPoint(todoPoint)
        }).start()
        sort()
    }

    fun sort() {
        var newTodoPoints: List<TodoPoint> = todoPoints
        newTodoPoints = newTodoPoints.sortedByDescending {
            it.done
        }
        setDataSet(newTodoPoints)
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
        holder.subTextField.text = todoPoints[position].subText
        holder.dueField.text = simpleDateFormat.format(todoPoints[position].duedate?.time)
        if(todoPoints[position].family!=null) {
            Thread(Runnable {
                var ts = placeViewModel.getByID(todoPoints[position].family!!)
                activity.runOnUiThread(Runnable {
                    holder.familyField.text = ts
                })
            }).start();




        }
        else {
            holder.familyDescriptionField.visibility = GONE
            holder.familyField.visibility = GONE
        }

        if(todoPoints[position].picturePath!=null) {
            val bo : BitmapFactory.Options = BitmapFactory.Options()
            bo.inSampleSize = 8
            BitmapFactory.decodeFile(todoPoints[position].picturePath, bo)
                ?.also { bitmap ->
                    holder.imageView.setImageBitmap(bitmap)

                }
        }


        if(todoPoints[position].village!=null) {
            Thread(Runnable{
                val ts = placeViewModel.getVillageByID(todoPoints[position].village!!)
                activity.runOnUiThread(Runnable {
                    holder.villageField.text = ts
                })

            }).start()

        }else {
            holder.villageDescriptionField.visibility = GONE
            holder.villageField.visibility = GONE
        }

    }

}


