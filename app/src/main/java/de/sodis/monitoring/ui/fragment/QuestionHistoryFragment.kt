package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.db.response.QuestionAnswer
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyHistoryViewModel
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_question.view.*
import kotlinx.android.synthetic.main.view_holder_text_choice.view.*
import kotlinx.android.synthetic.main.view_holder_text_input.view.*
import java.io.File

/**
 * display answered question.
 * - the survey
 * - the answers
 */
class QuestionHistoryFragment : BaseListFragment() {

    private lateinit var currentQuestion: CompletedSurveyDetail

    private val historyViewModel: SurveyHistoryViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, emptyList()))
                .get(SurveyHistoryViewModel::class.java)
        }!!
    }

    val args: QuestionHistoryFragmentArgs by navArgs()
    var completedSurveyId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        completedSurveyId = args.completedSurveyId
        historyViewModel.setCompletedSurveyId(completedSurveyId)
        historyViewModel.surveyCompletedList.observe(this, Observer { list ->
            currentQuestion = list.get(index = historyViewModel.currentPosition)
            recyclerView.withModels {
                question {
                    id("question")
                    title(currentQuestion.title)
                    questionText(currentQuestion.question.questionName)
                    onBind { model, view, position ->
                        view.dataBinding.root.question_image.load(File(currentQuestion.image?.path))
                    }
                }
                when (currentQuestion.question.inputTypeId) {
                    2 -> //todo
                        textInput {
                            id("input")
                            inputType(InputType.TYPE_NULL)
                            text(currentQuestion.answer.answerText)
                        }
                    1 -> textChoice {
                        id("choice")
                        if (currentQuestion.answer.answerText.equals("Si")) {
                            option1(currentQuestion.answer.answerText)
                            option1checked(true)
                            option2visible(View.GONE)
                        }
                        if (currentQuestion.answer.answerText.equals("No")) {
                            option2(currentQuestion.answer.answerText)
                            option2checked(true)
                            option1visible(View.GONE)
                        }

                    }
                }
            }

            view?.navigation_forward_button_1?.setOnClickListener {
                val hasNext = historyViewModel.nextQuestion()
                if (hasNext) {
                    val action =
                        QuestionHistoryFragmentDirections.actionMonitoringHistoryFragmentToQuestionHistoryFragment(
                            completedSurveyId
                        )

                    findNavController().navigate(action)
                } else {
                    (activity as MainActivity).show_bottom_navigation()
                    findNavController().navigate(R.id.monitoringHistoryFragment)
                }

            }
            view?.navigation_forward_button_left?.isGone = historyViewModel.currentPosition == 0

            view?.navigation_forward_button_left?.setOnClickListener {
                if (historyViewModel.currentPosition != 0) {
                    historyViewModel.previousQuestion()
                    val action =
                        QuestionHistoryFragmentDirections.actionMonitoringHistoryFragmentToQuestionHistoryFragment(
                            completedSurveyId
                        )
                    findNavController().navigate(action)
                }
            }
        })


    }
}
