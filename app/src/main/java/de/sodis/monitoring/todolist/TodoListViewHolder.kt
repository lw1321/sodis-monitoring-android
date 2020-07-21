package de.sodis.monitoring.todolist

import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R

class TodoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var checkBox: CheckBox
    lateinit var contentField: TextView
    lateinit var dueField: TextView
    lateinit var familyField: TextView
    lateinit var familyDescriptionField: TextView
    lateinit var villageField: TextView
    lateinit var villageDescriptionField: TextView

    init {
        checkBox = itemView.findViewById(R.id.checkbox)
        contentField = itemView.findViewById(R.id.todotext)
        dueField = itemView.findViewById(R.id.dateView)
        familyField = itemView.findViewById(R.id.familyView)
        familyDescriptionField = itemView.findViewById(R.id.familyDescriptionView)
        villageField = itemView.findViewById(R.id.villageView)
        villageDescriptionField = itemView.findViewById(R.id.villageDescriptionView)
    }

}