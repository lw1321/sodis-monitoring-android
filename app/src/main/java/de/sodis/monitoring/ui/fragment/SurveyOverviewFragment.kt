package de.sodis.monitoring.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.R
import de.sodis.monitoring.db.dao.QuestionImageDao
import de.sodis.monitoring.db.dao.QuestionImageDao_Impl
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.entity.QuestionImage
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyHistoryViewModel
import java.io.File


class SurveyOverviewFragment: Fragment() {

    private val surveyHistoryView: SurveyHistoryViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(SurveyHistoryViewModel::class.java)
    }


    var questionList: List<CompletedSurveyDetail>? = null

    lateinit var mainView: View

    lateinit var recyclerView: RecyclerView

    var completedSurveyID: Int  = 0

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
        imageDao = surveyHistoryView.monitoringDatabase.questionImageDao()


        surveyHistoryView.setCompletedSurveyId(completedSurveyId = this.completedSurveyID)

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        questionList = surveyHistoryView.getCompletedSurvey()

        val observer = Observer<List<CompletedSurveyDetail>> {
            newList ->
            if(newList !=null) {
                questionList = newList
                surveyOverviewAdapter.updateQuestions(questionList!!)
            }

        }
        surveyHistoryView.surveyCompletedList.observe(viewLifecycleOwner , observer)

        println(message = "onCreatView wird ausgeführt")
        mainView = inflater.inflate(R.layout.survey_overview, container, false)

        if(questionList == null) {
            println(message = "questionList ist null")
            questionList = listOf()
        }
        recyclerView = mainView.findViewById(R.id.question_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)


        surveyOverviewAdapter =
            SurveyOverviewAdapter(
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

class SurveyOverviewAdapter(@NonNull context: Context?, questions: List<CompletedSurveyDetail>, val imageDao: QuestionImageDao): RecyclerView.Adapter<SurveyOverviewViewHolder>() {



    var questions: List<CompletedSurveyDetail>

    var context: Context?

    fun updateQuestions(newList:List<CompletedSurveyDetail>) {
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
        val question: Question = questions[position].question
        holder.questionTextView.text = question.questionName

        val questionImage: QuestionImage = imageDao.getById(question.questionImageId)
        if(questionImage == null) {
            holder.imageView.visibility = View.GONE
        }
        else {
            holder.imageView.load(File(questionImage.path))
        }

        /*try {
            holder.imageView.setImageResource(question.questionImageId)
        }catch(error:Error) {

        }*/


        val answer: Answer = questions[position].answer
        if(answer.answerText!=null) {
            holder.answerTextView.text = answer.answerText
            if(answer.answerText == "Si") {
                holder.emojiView.setImageResource(R.drawable.ic_emoji_happy)
            }
            else {
                holder.emojiView.setImageResource(R.drawable.ic_emoji_sad)
            }

        }
        else {
            holder.answerTextView.visibility = View.GONE
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