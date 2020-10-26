package de.sodis.monitoring.survey_overview

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.todolist.TodoListViewHolder
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyHistoryViewModel


class SurveyOverview(val completedSurveyID: Int): Fragment() {

    private val surveyHistoryView: SurveyHistoryViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(SurveyHistoryViewModel::class.java)
    }


    var questionList: List<CompletedSurveyDetail>? = null

    lateinit var mainView: View

    lateinit var recyclerView: RecyclerView

    lateinit var surveyOverviewAdapter: SurveyOverviewAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        surveyHistoryView.setCompletedSurveyId(completedSurveyId = completedSurveyID)
        questionList = surveyHistoryView.getCompletedSurvey()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.survey_overview, container)
        if(questionList == null) {
            questionList = listOf()
        }
        recyclerView = mainView.findViewById(R.id.question_recyclerview)
        surveyOverviewAdapter = SurveyOverviewAdapter(context, questionList!!)

        recyclerView.adapter = surveyOverviewAdapter


        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

class SurveyOverviewAdapter(@NonNull context: Context?, questions: List<CompletedSurveyDetail>): RecyclerView.Adapter<SurveyOverviewViewHolder>() {

    var questions: List<CompletedSurveyDetail>

    var context: Context?

    init {
        this.context = context
        this.questions = questions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyOverviewViewHolder {
        var toReturnView: View = LayoutInflater.from(context).inflate(
            R.layout.view_holder_survey_overview_item, parent, false
        )
        return SurveyOverviewViewHolder(toReturnView)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun onBindViewHolder(holder: SurveyOverviewViewHolder, position: Int) {
        var question: Question = questions[position].question
        holder.questionTextView.text = question.questionName
        holder.imageView.setImageResource(question.questionImageId)

        var answer: Answer = questions[position].answer
        if(answer.answerText!=null) {
            holder.answerTextView.text = answer.answerText
        }
        else {
            holder.emojiView.visibility = View.GONE
        }

        if(answer.answerYn!=null) {
            if(answer.answerYn!!) {
                holder.emojiView.setImageResource(R.drawable.ic_emoji_happy)
            }
            else {
                holder.emojiView.setImageResource(R.drawable.ic_emoji_sad)
            }
        }
        else {
            holder.emojiView.visibility = View.GONE
        }

    }
}

class SurveyOverviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    lateinit var questionTextView: TextView
    lateinit var answerTextView: TextView
    lateinit var imageView: ImageView
    lateinit var emojiView: ImageView

    init {
        questionTextView = itemView.findViewById(R.id.question_overview_question)
        answerTextView = itemView.findViewById(R.id.question_overview_answer)
        imageView = itemView.findViewById(R.id.question_overview_image)
        emojiView = itemView.findViewById(R.id.question_overview_yes_no_image)
    }




}