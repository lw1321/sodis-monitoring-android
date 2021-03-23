package de.sodis.monitoring.ui.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.default
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.list.view.*
import java.util.*

class SurveyDialogFragment(val surveyId: Int) : DialogFragment() {

    lateinit var recyclerView: EpoxyRecyclerView

    private val surveyViewModel: SurveyViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(SurveyViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(de.sodis.monitoring.R.layout.continuable_list, container, false)
        recyclerView = view.list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.recycledViewPool.clear()
        surveyViewModel.question.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { questionList ->
                recyclerView.withModels {
                    default {
                        id(questionList.first().id)
                        text(questionList.first().name)
                        onClick { clicked ->
                        }
                    }
                    when (questionList.first().inputTypeId) {
                        1 -> { //Single Choice
                            questionList.forEach {
                                default {
                                    id(it.questionOptionId)
                                    text(it.optionChoiceName)
                                    onClick { clicked ->
                                    }
                                }
                            }
                        }
                        2 -> { //Text
                            default {
                                id(questionList.first().questionOptionId)
                                text("TEXTANTWORT")
                                onClick { clicked ->
                                }
                            }
                        }
                        4 -> {
                            default {
                                id(questionList.first().questionOptionId)
                                text("Bild aufnehmen!")
                                onClick { clicked ->
                                    //TODO image intent
                                }
                            }
                        }
                    }
                }
            })
        //TODO add move forward and backward UI and Logic
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm: InputMethodManager =
            (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }


}

