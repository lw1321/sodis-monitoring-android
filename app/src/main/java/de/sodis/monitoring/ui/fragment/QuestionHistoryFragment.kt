package de.sodis.monitoring.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import de.sodis.monitoring.db.response.CompletedSurveyDetail

/**
 * display answered question.
 * - the survey
 * - the answers
 */
class QuestionHistoryFragment : BaseListFragment() {

    private lateinit var currentQuestion: CompletedSurveyDetail


    val args: QuestionHistoryFragmentArgs by navArgs()
    var completedSurveyId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        completedSurveyId = args.completedSurveyId
    }
}
