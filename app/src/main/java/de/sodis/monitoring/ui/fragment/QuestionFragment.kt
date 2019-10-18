package de.sodis.monitoring.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_question.view.*
import kotlinx.android.synthetic.main.view_holder_text_choice.view.*
import kotlinx.android.synthetic.main.view_holder_text_input.view.*
import java.io.File

class QuestionFragment(private val surveyId: Int) : BaseListFragment() {


    private val surveyViewModel: SurveyViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, listOf(surveyId)))
                .get(SurveyViewModel::class.java)
        }!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surveyViewModel.questionItemList.observe(this, Observer { list ->
            val currentQuestion = list.get(index = surveyViewModel.currentPosition)

            recyclerView.withModels {
                question {
                    id("question")
                    title(currentQuestion.title)
                    questionText(currentQuestion.question.questionName)
                    onBind { model, view, position ->
                        Glide.with(view.dataBinding.root.context)
                            .load(Uri.fromFile(File(currentQuestion.image.path)))
                            .into(view.dataBinding.root.question_image)
                    }
                }
                when (currentQuestion.question.inputTypeId) {
                    2 -> //todo
                        textInput {
                            id("input")
                            hint("Respuesta")
                            onBind { model, view, position ->
                                view.dataBinding.root.answerTextInput.addTextChangedListener {
                                    surveyViewModel.setAnswer(
                                        currentQuestion.question.id,
                                        it!!.toString(),
                                        currentQuestion.answers[0].questionOption.id
                                    )
                                }
                            }
                        }
                    1 -> textChoice {
                        id("choice")
                        option1(currentQuestion.answers[0].optionChoice.optionChoiceName)
                        option2(currentQuestion.answers[1].optionChoice.optionChoiceName)
                        onBind { model, view, position ->
                            view.dataBinding.root.radio_group.setOnCheckedChangeListener { group, checkedId ->
                                val index = if (checkedId == R.id.optionButton) 0 else 1
                                surveyViewModel.setAnswer(
                                    currentQuestion.question.id,
                                    currentQuestion.answers[index].optionChoice.optionChoiceName,
                                    currentQuestion.answers[index].questionOption.id //todo
                                )
                            }
                        }
                    }
                }
            }

            view?.navigation_forward_button_1?.setImageResource(if (surveyViewModel.currentPosition != (list.size - 1)) R.drawable.ic_arrow_forward_white_24dp else R.drawable.ic_check_white_24dp)

            view?.navigation_forward_button_1?.setOnClickListener {
                if (surveyViewModel.isAnswered(currentQuestion.question.id)) {

                    val hasNext = surveyViewModel.nextQuestion()
                    if (!hasNext)
                        Snackbar.make(view!!, "Los datos se guardan", Snackbar.LENGTH_LONG).show()
                    (activity as MainActivity).replaceFragments(
                        if (hasNext) QuestionFragment(
                            surveyId
                        ) else MonitoringOverviewFragment()
                    )
                } else {
                    Snackbar.make(
                        view!!,
                        "Por favor seleccione una opción de respuesta!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        })

    }

}
