package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.hide_bottom_navigation
import de.sodis.monitoring.interviewee
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_interviewee.view.*

class SurveyFragment : BaseListFragment(){


    private val surveyViewModel: SurveyViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, listOf(surveyId))).get(SurveyViewModel::class.java)
        }!!
    }

    val args: SurveyFragmentArgs by navArgs()
    var surveyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surveyId = args.surveyId
        surveyViewModel.setSurveyId(surveyId)

        surveyViewModel.intervieweeList.observe(this, Observer { list ->
            recyclerView.withModels {
                interviewee {
                    id("interviewee")
                    titleText("Interrogado")
                    hint("Interrogado")
                    onBind { model, view, position ->
                        view.dataBinding.root.multiAutoCompleteTextView.apply {
                            val adapter = ArrayAdapter<String>(
                                context,
                                android.R.layout.simple_dropdown_item_1line,
                                list.map { it.name }
                            )
                            threshold = 1 //will start working from first character
                            multiAutoCompleteTextView.setText("")

                            setAdapter(adapter)
                            setOnItemClickListener { parent, view, position, id ->
                                surveyViewModel.setInterviewee(list.map { it.name }[position])
                            }
                        }
                    }
                }
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view?.navigation_forward_button_left?.isGone = true

        view?.navigation_forward_button_1?.setOnClickListener {
            if (surveyViewModel.interviewee == null) {
                Snackbar.make(view, "Ingrese el nombre del encuestado", Snackbar.LENGTH_LONG)
                    .show()
            }
            else {
                (activity as MainActivity).hide_bottom_navigation()
                findNavController().navigate(SurveyFragmentDirections.actionSurveyFragmentToQuestionFragment(surveyId))
            }
        }


        return view
    }


}
