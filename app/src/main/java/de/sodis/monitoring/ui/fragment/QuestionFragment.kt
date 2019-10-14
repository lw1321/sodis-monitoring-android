package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.R
import de.sodis.monitoring.db.response.QuestionAnswer
import de.sodis.monitoring.replaceFragments
import de.sodis.monitoring.ui.adapter.ExpandableRecyclerViewAdapter
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.*
import de.sodis.monitoring.ui.viewholder.AnswerSelectViewHolder
import de.sodis.monitoring.ui.viewholder.AnswerTextViewHolder
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.text_choice_item.view.*
import kotlinx.android.synthetic.main.text_input_item.view.*

class QuestionFragment(private val surveyId: Int) : Fragment(), RecyclerViewListener {
    override fun recyclerViewListCLicked(view: View, id: Any) {
        /**
         * save answer depending on type
         */
        if (currentQuestion.question.inputTypeId == 2) {//text!
            surveyViewModel.setAnswer(
                currentQuestion.question.id,
                (mView.findViewHolderForAdapterPosition(1) as AnswerTextViewHolder).itemView.answerTextInput.text.toString()
            )
        }
        if (currentQuestion.question.inputTypeId == 1) {//single choice
            val itemView =
                (mView.findViewHolderForAdapterPosition(1) as AnswerSelectViewHolder).itemView
            val option1Checked = itemView.optionButton.isChecked
            val option2Checked =
                (mView.findViewHolderForAdapterPosition(2) as AnswerSelectViewHolder).itemView.optionButton.isChecked
            if (!option1Checked && !option2Checked) {
                Toast.makeText(
                    context,
                    "Bitte eine Antwortmöglichkeit auswählen!",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            surveyViewModel.setAnswer(
                currentQuestion.question.id,
                if (option1Checked) itemView.optionButton.text.toString() else itemView.optionButton2.text.toString()
            )

        }
        //show loading screen
        Toast.makeText(context, "Übermitteln des Fragebogens", Toast.LENGTH_LONG)
            .show()//todo show loading animation
        val hasNext = surveyViewModel.nextQuestion()
        (activity as MainActivity).replaceFragments(if (hasNext) SurveyFragment(surveyId) else MonitoringOverviewFragment())
    }

    private lateinit var currentQuestion: QuestionAnswer
    private lateinit var surveyViewModel: SurveyViewModel
    private lateinit var adapter: ExpandableRecyclerViewAdapter
    private lateinit var mView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surveyViewModel = activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, listOf(surveyId)))
                .get(SurveyViewModel::class.java)
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.list, container, false) as RecyclerView
        this.adapter = ExpandableRecyclerViewAdapter(this)
        // Set the adapter
        mView.adapter = this.adapter
        mView.layoutManager = LinearLayoutManager(context)

        surveyViewModel.questionItemList.observe(this, Observer {
            //at which position are we?? todo
            currentQuestion = it.get(index = surveyViewModel.currentPosition)
            val tempItemList = mutableListOf<SodisItem>()
            tempItemList.add(
                QuestionItem(
                    title = currentQuestion.title,
                    questionText = currentQuestion.question.questionName,
                    imageUri = currentQuestion.image.path
                )
            )
            if (currentQuestion.question.inputTypeId == 2) {//todo
                tempItemList.add(
                    TextItem(hiddenText = "Bitte hier die Antwort eingeben")
                )
            }
            if (currentQuestion.question.inputTypeId == 1) {
                tempItemList.add(
                    SelectItem(
                        option1 = currentQuestion.answers[0].optionChoiceName,
                        option2 = currentQuestion.answers[1].optionChoiceName
                    )
                )
            }
            tempItemList.add(
                if (surveyViewModel.currentPosition == (it.size - 1)) NavigationFinishItem() else NavigationForwardItem()
            )
            adapter.setItems(tempItemList)
        })

        return view
    }

}
