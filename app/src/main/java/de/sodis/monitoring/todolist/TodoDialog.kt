package de.sodis.monitoring.todolist

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.TodoPoint
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.TodoPointModel
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class TodoDialog(val passedInterviewee: Interviewee?, val passedText: String?, applicationContext: Context): DialogFragment() {

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }

    private val todoPointModel: TodoPointModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(TodoPointModel::class.java)
    }

    lateinit var searchEditText: EditText
    lateinit var searchRecyclerView: RecyclerView
    lateinit var titleText: EditText
    lateinit var dueText: EditText

    lateinit var cancelButton: Button
    lateinit var continueButton: Button

    var due: Calendar
    var intervieweeChosen: Interviewee?
    lateinit var intervieweeResults: List<Interviewee>

    lateinit var  datePickerDialog: DatePickerDialog

    lateinit var dueTextOnClickListener: View.OnClickListener

    var onCancelPressed: View.OnClickListener

    var onSavePressed: View.OnClickListener

    init {
        due = Calendar.getInstance()
        intervieweeChosen = passedInterviewee

        onCancelPressed = View.OnClickListener {
            dismiss()
        }

        onSavePressed= View.OnClickListener{
            saveAndDismiss()
        }
    }

    fun saveAndDismiss (){
        var intervieweeidtoset: Int? = null
        var villageidtoset: Int? = null
        if(intervieweeChosen!=null) {
            intervieweeidtoset = intervieweeChosen!!.id
            if(intervieweeChosen!!.villageId!=null) {
                villageidtoset = intervieweeChosen!!.villageId
            }
        }
            var todoPoint = TodoPoint(
                null, false, Calendar.getInstance(), due, null, intervieweeidtoset, villageidtoset ,titleText.text.toString())
        Thread(
            Runnable { todoPointModel.insertTodoPoint(todoPoint!!) }).start()
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        var dialog = dialog
        if(dialog!=null) {
            var width = ViewGroup.LayoutParams.MATCH_PARENT
            var height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }

    lateinit var searchAdapter: SearchAdapter
    lateinit var onTouchListener:View.OnTouchListener


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        intervieweeResults = listOf()



        datePickerDialog = DatePickerDialog(
                context,
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            due.set(Calendar.YEAR, year)
            due.set(Calendar.MONTH, month)
            due.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dueText.setText(SimpleDateFormat("dd.MM.yyyy").format(due.time))
        },
        due.get(Calendar.YEAR),
        due.get(Calendar.MONTH),
        due.get(Calendar.DAY_OF_MONTH)

        )
        dueTextOnClickListener = View.OnClickListener {
            datePickerDialog.show()
        }
        onTouchListener = View.OnTouchListener{
            v, d ->
            v.performClick()
            datePickerDialog.show()
            return@OnTouchListener true
        }
        var view = inflater.inflate(R.layout.todo_dialog_layout, container, false)
        searchEditText = view.findViewById(R.id.tododialog_searchview)
        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchAdapter.filter.filter(s)
            }

        })
        if(passedInterviewee!=null) {
            searchEditText.setText(passedInterviewee.name)
        }


        searchRecyclerView = view.findViewById(R.id.tododialog_recyclerview)
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(intervieweeResults, requireContext(), object:CallBackTodo{
            override fun OnIntervieweeChosen(interviewee: Interviewee?) {
                intervieweeChosen = interviewee
            }
        }, null)
        searchRecyclerView.adapter = searchAdapter
        intervieweeModel.intervieweeList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            searchAdapter?.setDataSet(it)
            searchAdapter?.filter.filter(searchEditText?.text)
        })
        titleText = view.findViewById(R.id.tododialog_title)
        if(passedText!=null){
            titleText.setText(passedText)
        }
        dueText = view.findViewById(R.id.tododialog_due)
        dueText.setText(SimpleDateFormat("dd.MM.yyyy").format(due.time))
        cancelButton = view.findViewById(R.id.tododialog_cancel)
        continueButton = view.findViewById(R.id.tododialog_save)
        //dueText.setOnClickListener(dueTextOnClickListener)
        dueText.setOnTouchListener(onTouchListener)
        cancelButton.setOnClickListener(onCancelPressed)
        continueButton.setOnClickListener(onSavePressed)
        return view
    }


}

class SearchAdapter(interviewees: List<Interviewee>, val context: Context, val callBack: CallBackTodo, originallySelected: Interviewee?):Filterable, RecyclerView.Adapter<SearchListViewHolder>(){
    var filteredInterviewees: ArrayList<Array<Any>>
    var originalInterviewees: ArrayList<Array<Any>>

    init {
        this.originalInterviewees = ArrayList()
        this.filteredInterviewees = ArrayList()
        interviewees.forEach {
            var isChecked = false
            if(originallySelected!=null) {
                if(originallySelected.id == it.id) {
                    isChecked = true
                }
            }
            var toAdd = arrayOf(isChecked, it)
            originalInterviewees.add(toAdd)
            filteredInterviewees.add(toAdd)
        }
    }

    var ourFilter:Filter = object: Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var filteredToReturn:ArrayList<Array<Any>> = ArrayList()
            if(constraint.isNullOrEmpty()) {
                var  toRemove:ArrayList<Array<Any>> = originalInterviewees
                val indexToAdd = toRemove.indexOfFirst {
                    it[0] as Boolean
                }
                if(indexToAdd!=-1) {
                    filteredToReturn.add(toRemove.removeAt(indexToAdd))
                }
                filteredToReturn.addAll(toRemove)
            }
            else {
                for(array in originalInterviewees) {
                    if(array[0] as Boolean) {
                        filteredToReturn.add(0, array)
                    }
                    else {
                        if((array[1] as Interviewee).name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredToReturn.add(array)
                        }
                    }

                }
            }
            var filterResults = FilterResults()
            filterResults.values = filteredToReturn
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredInterviewees.clear()
            if(results!=null) {
                filteredInterviewees.addAll(results.values as ArrayList<Array<Any>>)
            }
            notifyDataSetChanged()
        }

    }


    override fun getFilter(): Filter {
        return ourFilter
    }

    fun setDataSet(newInterviewees:List<Interviewee>) {
        val selectedbeforeArray:Array<Any>? = originalInterviewees.firstOrNull {
            it[0] as Boolean
        }
        var selectedbefore: Interviewee? = null
        if(selectedbeforeArray!=null) {
            if(selectedbeforeArray[1]!=null) {
                selectedbefore = selectedbeforeArray[1] as Interviewee
            }
        }
        this.originalInterviewees = ArrayList()
        this.filteredInterviewees = ArrayList()
        newInterviewees.forEach {
            var isChecked = false
            if(selectedbefore!=null) {
                if(selectedbefore.id == it.id) {
                    isChecked = true
                }
            }
            var toAdd = arrayOf(isChecked, it)
            originalInterviewees.add(toAdd)
            filteredInterviewees.add(toAdd)
        }
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        var toReturnView = LayoutInflater.from(context).inflate(R.layout.todo_dialog_searchitem, parent, false)
        return SearchListViewHolder(toReturnView)
    }

    override fun getItemCount(): Int {
        return  filteredInterviewees.size
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.checkBox.isChecked = filteredInterviewees[position][0] as Boolean
        holder.contentField.text = (filteredInterviewees[position][1] as Interviewee).name
        holder.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener {buttonView, isChecked ->  holder.checkBox.isChecked = isChecked; manageCheckedChange(position, isChecked)})
    }

    fun manageCheckedChange(position: Int, checked: Boolean) {
        filteredInterviewees[position][0] = checked
        for(i in 0..(filteredInterviewees.size-1)) {
            if(i!=position) {
                if(filteredInterviewees[i][0] as Boolean) {
                    filteredInterviewees[i][0] = false
                    notifyItemChanged(i)
                }

            }
        }
        originalInterviewees.forEach {
            if((it[1] as Interviewee).id == (filteredInterviewees[position][1]as Interviewee).id) {
                it[0] = checked
            }
            else {
                it[0] = false
            }
        }
        originalInterviewees.forEach {
            if((it[1] as Interviewee).id == (filteredInterviewees[position][1]as Interviewee).id) {
                it[0] = checked
            }
            else {
                it[0] = false
            }
        }
        if(checked) {
            callBack.OnIntervieweeChosen(filteredInterviewees[position][1] as Interviewee)
        }
        else {
            callBack.OnIntervieweeChosen(null)
        }
    }

}

interface CallBackTodo {
    fun OnIntervieweeChosen(interviewee: Interviewee?)
}


class SearchListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var checkBox: CheckBox
    lateinit var contentField: TextView


    init {
        checkBox = itemView.findViewById(R.id.tododialog_searchinterviewee_checkbox)
        contentField = itemView.findViewById(R.id.tododialog_searchinterviewee_title)

    }

}