package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.db.entity.OptionChoice
import de.sodis.monitoring.ui.adapter.ExpandableRecyclerViewAdapter
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.QuestionItem
import de.sodis.monitoring.ui.model.SelectItem
import de.sodis.monitoring.ui.model.SodisItem
import de.sodis.monitoring.ui.model.TextItem
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel

class QuestionFragment(private val surveyId: Int) : Fragment(), RecyclerViewListener {
    override fun recyclerViewListCLicked(view: View, id: Any) {
        //todo save user input
        //Todo increase position, if position is == maxPosition, finish survey
    }

    private lateinit var surveyViewModel: SurveyViewModel
    private lateinit var adapter: ExpandableRecyclerViewAdapter

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
        val view = inflater.inflate(R.layout.list, container, false)
        this.adapter = ExpandableRecyclerViewAdapter(this)
        // Set the adapter
        if (view is RecyclerView) {
            view.adapter = this.adapter
            view.layoutManager = LinearLayoutManager(context)
        }

        surveyViewModel.questionItemList.observe(this, Observer {
            //at which position are we?? todo
            val currentQuestion = it.get(index = surveyViewModel.currentPosition)
            val tempItemList = mutableListOf<SodisItem>()
            tempItemList.add(
                QuestionItem(
                    title = "Title",//TODO
                    questionText = currentQuestion.question.questionName,
                    imageUri = currentQuestion.image.path
                )
            )
            if (currentQuestion.question.inputTypeId == 1) {//todo
                tempItemList.add(
                    TextItem(hiddenText = "Bitte hier die Antwort eingeben")
                )
            }
            if (currentQuestion.question.inputTypeId == 2) {
                for (answers: OptionChoice in currentQuestion.answers) {
                    tempItemList.add(
                        SelectItem(
                            name = answers.optionChoiceName
                        )
                    )
                }
            }

            adapter.setItems(tempItemList)
        })

        return view
    }

}
