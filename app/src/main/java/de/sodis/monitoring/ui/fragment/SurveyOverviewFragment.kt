package de.sodis.monitoring.ui.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.R
import de.sodis.monitoring.db.dao.QuestionImageDao
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.entity.QuestionImage
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import java.io.File


class SurveyOverviewFragment : Fragment() {

    var questionList: List<CompletedSurveyDetail>? = null

    lateinit var mainView: View

    lateinit var recyclerView: RecyclerView

    var completedSurveyID: String = ""

    val args: SurveyOverviewFragmentArgs by navArgs()

    lateinit var surveyOverviewAdapter: SurveyOverviewAdapter

    lateinit var imageDao: QuestionImageDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            println("back pressed called")
            findNavController().popBackStack()
        }*/
        completedSurveyID = args.completedSurveyID
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        val observer = Observer<List<CompletedSurveyDetail>> { newList ->
            if (newList != null) {
                questionList = newList
                surveyOverviewAdapter.updateQuestions(questionList!!)
            }

        }

        println(message = "onCreatView wird ausgeführt")
        mainView = inflater.inflate(R.layout.survey_overview, container, false)

        if (questionList == null) {
            println(message = "questionList ist null")
            questionList = listOf()
        }
        recyclerView = mainView.findViewById(R.id.question_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)


        surveyOverviewAdapter =
                SurveyOverviewAdapter(
                        activity!!,
                        context,
                        questionList!!,
                        imageDao
                )

        recyclerView.adapter = surveyOverviewAdapter
        println(message = "main View wird zurückgegeben")
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm: InputMethodManager =
                (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)

    }
}

class SurveyOverviewAdapter(val activity: Activity, @NonNull context: Context?, questions: List<CompletedSurveyDetail>, val imageDao: QuestionImageDao) : RecyclerView.Adapter<SurveyOverviewViewHolder>() {


    var questions: List<CompletedSurveyDetail>

    var context: Context?

    fun updateQuestions(newList: List<CompletedSurveyDetail>) {
        questions = newList
        super.notifyDataSetChanged()
    }

    init {
        this.context = context
        this.questions = questions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyOverviewViewHolder {
        var toReturnView: View = LayoutInflater.from(context).inflate(
                R.layout.view_holder_survey_overview_item, parent, false
        )
        return SurveyOverviewViewHolder(
                toReturnView
        )
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun onBindViewHolder(holder: SurveyOverviewViewHolder, position: Int) {
        val question = questions[position]
        var toSetText = questions[position].sectionName
        if (question.questionName != "Estado") {
            toSetText += "\n" + question.questionName
        }
        holder.questionTextView.text = toSetText
        Thread(Runnable {
            if (question.path == null) {
                activity.runOnUiThread {
                    holder.imageView.visibility = View.GONE
                }
            } else {
                activity.runOnUiThread {
                    holder.imageView.load(File(question.path))
                }
            }
        }).start()


        /*
        todo join the option choice name to answer, show text on text answer, imageAnswer?,
        todo single choice answer with other options than si/no/no response,
        todo add ? for no response as icon

        val answer = question.answerText
        if(answer.answerText!=null) {
            if(answer.answerText == "Si") {
                holder.emojiView.setImageResource(R.drawable.ic_emoji_happy)
                holder.emojiView.setColorFilter(Color.GREEN)
                holder.answerTextView.visibility = View.GONE
            }
            else {
                holder.emojiView.setImageResource(R.drawable.ic_emoji_sad)
                holder.emojiView.setColorFilter(Color.RED)
                if ( answer.answerText == "No"){
                    holder.answerTextView.visibility = View.GONE
                }
                holder.answerTextView.text = answer.answerText
            }
          else {
            holder.answerTextView.visibility = View.GONE
        }
        }*/


    }
}

class SurveyOverviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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