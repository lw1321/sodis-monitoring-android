package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.db.response.QuestionAnswer
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_question.view.*
import kotlinx.android.synthetic.main.view_holder_text_choice.view.*
import kotlinx.android.synthetic.main.view_holder_text_input.view.*
import java.io.File

class QuestionFragment : BaseListFragment() {


    private lateinit var currentQuestion: QuestionAnswer
    private val surveyViewModel: SurveyViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, listOf(surveyId)))
                .get(SurveyViewModel::class.java)
        }!!
    }

    val args: QuestionFragmentArgs by navArgs()
    var surveyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surveyId = args.surveyId
        surveyViewModel.questionItemList.observe(this, Observer { list ->
            currentQuestion = list.get(index = surveyViewModel.currentPosition)

            recyclerView.withModels {
                question {
                    id("question")
                    title(currentQuestion.title)
                    questionText(currentQuestion.question.questionName)
                    onBind { model, view, position ->
                        view.dataBinding.root.question_image.load(File(currentQuestion.image.path))
                    }
                }
                when (currentQuestion.question.inputTypeId) {
                    2 -> //todo
                        textInput {
                            id("input")
                            hint("Respuesta")
                            onBind { model, view, position ->
                                view.dataBinding.root.answerTextInput.requestFocus()
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
                            view.dataBinding.root.radio_group.clearCheck()
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
                    surveyViewModel.listOfAnsweredQuestions += surveyViewModel.currentPosition
                    val hasNext = surveyViewModel.nextQuestion()
                    if (hasNext) {
                        val action = QuestionFragmentDirections.actionQuestionFragmentSelf(surveyId)
                        findNavController().navigate(action)
                    } else {
                        Snackbar.make(view!!, "Los datos se guardan", Snackbar.LENGTH_LONG).show()
                        (activity as MainActivity).show_bottom_navigation()
                        findNavController().navigate(R.id.monitoringOverviewFragment)
                    }
                } else {
                    Snackbar.make(
                        view!!,
                        "Por favor seleccione una opción de respuesta!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            view?.navigation_forward_button_left?.isGone = surveyViewModel.currentPosition == 0

            view?.navigation_forward_button_left?.setOnClickListener {
                if (surveyViewModel.currentPosition != 0) {
                    surveyViewModel.previousQuestion()
                    val action = QuestionFragmentDirections.actionQuestionFragmentSelf(surveyId)
                    findNavController().navigate(action)
                }
            }
        })

    }

}
