package de.sodis.monitoring.todolist

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.TodoPoint
import de.sodis.monitoring.db.response.IntervieweeItem
import de.sodis.monitoring.viewmodel.PlaceViewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.TodoPointModel

import java.io.File
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoDialog(
        val intervieweeId: String?,
        val passedText: String?,
        applicationContext: Context,
        val onDismissListener: DialogInterface.OnDismissListener?
) : DialogFragment() {

    private val placeViewModel: PlaceViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(PlaceViewModel::class.java)
    }


    val REQUEST_TAKE_PHOTO = 1
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            activity!!,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_todo", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            savedPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPicture()
        }
    }


    //bis hier reinkopiert


    private val todoPointModel: TodoPointModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(TodoPointModel::class.java)
    }

    lateinit var searchEditText: EditText
    lateinit var searchRecyclerView: RecyclerView
    lateinit var titleText: EditText
    lateinit var dueText: EditText
    lateinit var descriptionText: EditText

    lateinit var imageView: ImageView

    lateinit var cancelButton: Button
    lateinit var continueButton: Button


    var due: Calendar
    var intervieweeChosen: IntervieweeItem? = null

    lateinit var intervieweeResults: List<IntervieweeItem>

    lateinit var datePickerDialog: DatePickerDialog

    lateinit var dueTextOnClickListener: View.OnClickListener

    var takePhotoOnClickListener: View.OnClickListener = View.OnClickListener {
        dispatchTakePictureIntent()
    }

    fun setPicture() {
        if (savedPath != null) {
            val bo: BitmapFactory.Options = BitmapFactory.Options()
            bo.inSampleSize = 8
            BitmapFactory.decodeFile(savedPath, bo)
                    ?.also { bitmap ->
                        imageView.setImageBitmap(bitmap)

                    }
        } else {
            imageView.setImageResource(R.drawable.ic_camera_alt_black_24dp)
        }
    }

    var onCancelPressed: View.OnClickListener

    var onSavePressed: View.OnClickListener


    init {
        due = Calendar.getInstance()

        onCancelPressed = View.OnClickListener {
            dismiss()
        }

        onSavePressed = View.OnClickListener {
            saveAndDismiss()
        }
    }

    var savedPath: String? = null

    fun saveAndDismiss() {
        var intervieweeidtoset: String? = null
        var villageidtoset: Int? = null
        if (intervieweeChosen != null) {
            intervieweeidtoset = intervieweeChosen!!.id
            villageidtoset = intervieweeChosen!!.villageId
        }
        var todoPoint = TodoPoint(
                null,
                false,
                Calendar.getInstance(),
                due,
                null,
                intervieweeidtoset,
                villageidtoset,
                titleText.text.toString(),
                descriptionText.text.toString(),
                savedPath
        )
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
        if (dialog != null) {
            var width = ViewGroup.LayoutParams.MATCH_PARENT
            var height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    lateinit var searchAdapter: SearchAdapter
    lateinit var onTouchListener: View.OnTouchListener

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.todo_dialog_layout, container, false)

        intervieweeResults = listOf()



        datePickerDialog = DatePickerDialog(
                context!!,
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
        onTouchListener = View.OnTouchListener { v, d ->
            v.performClick()
            datePickerDialog.show()
            return@OnTouchListener true
        }


        searchRecyclerView = view!!.findViewById(R.id.tododialog_recyclerview)
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(intervieweeResults, requireContext(), object : CallBackTodo {
            override fun OnIntervieweeChosen(interviewee: IntervieweeItem?) {
                intervieweeChosen = interviewee
            }
        }, null)
        searchRecyclerView.adapter = searchAdapter


        placeViewModel.intervieweeItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer { intervieweeList ->
            searchEditText = view.findViewById(R.id.tododialog_searchview)
            if(intervieweeId != null){
                intervieweeChosen = intervieweeList.first { intervieweeItem -> intervieweeItem.id == intervieweeId }
                if(intervieweeChosen!=null) {
                    searchEditText.setText(intervieweeChosen!!.name)
                }

            }
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchAdapter.filter.filter(s)
                }
            })
            searchAdapter?.setDataSet(intervieweeList)
            searchAdapter?.filter.filter(searchEditText?.text)
        })
        titleText = view!!.findViewById(R.id.tododialog_title)
        if (passedText != null) {
            titleText.setText(passedText)
        }
        descriptionText = view!!.findViewById(R.id.tododialog_text)
        dueText = view!!.findViewById(R.id.tododialog_due)
        dueText.setText(SimpleDateFormat("dd.MM.yyyy").format(due.time))
        cancelButton = view!!.findViewById(R.id.tododialog_cancel)
        continueButton = view!!.findViewById(R.id.tododialog_save)
        //dueText.setOnClickListener(dueTextOnClickListener)
        dueText.setOnTouchListener(onTouchListener)
        cancelButton.setOnClickListener(onCancelPressed)
        continueButton.setOnClickListener(onSavePressed)

        imageView = view!!.findViewById(R.id.todoDialogImageView)
        imageView.setOnClickListener(takePhotoOnClickListener)
        setPicture()
        return view
    }


}

class SearchAdapter(
        interviewees: List<IntervieweeItem>,
        val context: Context,
        val callBack: CallBackTodo,
        originallySelected: IntervieweeItem?
) : Filterable, RecyclerView.Adapter<SearchListViewHolder>() {
    var filteredInterviewees: ArrayList<Array<Any>>
    var originalInterviewees: ArrayList<Array<Any>>

    init {
        this.originalInterviewees = ArrayList()
        this.filteredInterviewees = ArrayList()
        interviewees.forEach {
            var isChecked = false
            if (originallySelected != null) {
                if (originallySelected.id == it.id) {
                    isChecked = true
                }
            }
            var toAdd = arrayOf(isChecked, it)
            originalInterviewees.add(toAdd)
            filteredInterviewees.add(toAdd)
        }
    }

    var ourFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var filteredToReturn: ArrayList<Array<Any>> = ArrayList()
            if (constraint.isNullOrEmpty()) {
                var toRemove: ArrayList<Array<Any>> = originalInterviewees
                val indexToAdd = toRemove.indexOfFirst {
                    it[0] as Boolean
                }
                if (indexToAdd != -1) {
                    filteredToReturn.add(toRemove.removeAt(indexToAdd))
                }
                filteredToReturn.addAll(toRemove)
            } else {
                for (array in originalInterviewees) {
                    if (array[0] as Boolean) {
                        filteredToReturn.add(0, array)
                    } else {
                        if ((array[1] as IntervieweeItem).name.toLowerCase()
                                        .contains(constraint.toString().toLowerCase())
                        ) {
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
            if (results!!.values != null) {
                filteredInterviewees.addAll(results.values as ArrayList<Array<Any>>)
            }
            notifyDataSetChanged()
        }

    }


    override fun getFilter(): Filter {
        return ourFilter
    }

    fun setDataSet(newInterviewees: List<IntervieweeItem>) {
        val selectedbeforeArray: Array<Any>? = originalInterviewees.firstOrNull {
            it[0] as Boolean
        }
        var selectedbefore: IntervieweeItem? = null
        if (selectedbeforeArray != null) {
            if (selectedbeforeArray[1] != null) {
                selectedbefore = selectedbeforeArray[1] as IntervieweeItem
            }
        }
        this.originalInterviewees = ArrayList()
        this.filteredInterviewees = ArrayList()
        newInterviewees.forEach {
            var isChecked = false
            if (selectedbefore != null) {
                if (selectedbefore.id == it.id) {
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
        var toReturnView =
                LayoutInflater.from(context).inflate(R.layout.todo_dialog_searchitem, parent, false)
        return SearchListViewHolder(toReturnView)
    }

    override fun getItemCount(): Int {
        return filteredInterviewees.size
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.checkBox.isChecked = filteredInterviewees[position][0] as Boolean
        holder.contentField.text = (filteredInterviewees[position][1] as IntervieweeItem).name
        holder.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            holder.checkBox.isChecked = isChecked; manageCheckedChange(position, isChecked)
        })
    }

    fun manageCheckedChange(position: Int, checked: Boolean) {
        filteredInterviewees[position][0] = checked
        for (i in 0 until (filteredInterviewees.size - 1)) {
            if (i != position) {
                if (filteredInterviewees[i][0] as Boolean) {
                    filteredInterviewees[i][0] = false
                    notifyItemChanged(i)
                }

            }
        }
        originalInterviewees.forEach {
            if ((it[1] as IntervieweeItem).id == (filteredInterviewees[position][1] as IntervieweeItem).id) {
                it[0] = checked
            } else {
                it[0] = false
            }
        }
        originalInterviewees.forEach {
            if ((it[1] as IntervieweeItem).id == (filteredInterviewees[position][1] as IntervieweeItem).id) {
                it[0] = checked
            } else {
                it[0] = false
            }
        }
        if (checked) {
            callBack.OnIntervieweeChosen(filteredInterviewees[position][1] as IntervieweeItem)
        } else {
            callBack.OnIntervieweeChosen(null)
        }
    }

}

interface CallBackTodo {
    fun OnIntervieweeChosen(interviewee: IntervieweeItem?)
}


class SearchListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var checkBox: CheckBox
    lateinit var contentField: TextView


    init {
        checkBox = itemView.findViewById(R.id.tododialog_searchinterviewee_checkbox)
        contentField = itemView.findViewById(R.id.tododialog_searchinterviewee_title)

    }

}