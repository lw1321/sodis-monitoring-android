package de.sodis.monitoring.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.sodis.monitoring.*
import de.sodis.monitoring.repository.worker.DownloadWorker
import de.sodis.monitoring.repository.worker.UploadWorker
import de.sodis.monitoring.viewmodel.HistoryViewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_technology.view.*
import kotlinx.android.synthetic.main.view_holder_upload_survey.view.*

class SurveyHistoryFragment : BaseListFragment() {



    private val historyViewModel: HistoryViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(HistoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.navigation_forward_button_1?.isGone = false
        view?.navigation_forward_button_left?.isGone = true
        view?.navigation_cancel_button?.isGone = true
        view?.navigation_forward_button_1?.setImageResource(R.drawable.ic_baseline_backup_24)

        historyViewModel.allSurveyItems.observe(viewLifecycleOwner, Observer { completedSurveyList ->
            historyViewModel.notSubmittedSurveyItems.observe(viewLifecycleOwner, Observer { unsubmittedSurveyList ->
                recyclerView.withModels {
                    completedSurveyList.forEach {
                        uploadSurvey {
                            id(it.id)
                            interviewee(it.interviewee)
                            village(it.village)
                            survey(it.surveyName)
                            date(it.date.split(".").first())
                            onBind { model, view, position ->
                                var isSubmitted = unsubmittedSurveyList.none { item -> item.id == it.id }
                                view.dataBinding.root.iconStatus.setImageResource(if (isSubmitted) R.drawable.ic_check_white_24dp else R.drawable.ic_baseline_backup_24)
                                view.dataBinding.root.iconStatus.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (isSubmitted) R.color.colorGreen700 else R.color.colorGrey700))
                            }
                        }
                    }
                }
            })
        })

        view?.navigation_forward_button_1?.setOnClickListener {
            //sync all completed surveys and show dialog with status and option to cancel the upload

            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()

            WorkManager.getInstance(activity!!.applicationContext).enqueue(uploadWorkRequest)

            val workInfoByIdLiveData = WorkManager.getInstance(activity!!.applicationContext).getWorkInfoByIdLiveData(uploadWorkRequest.id)

            workInfoByIdLiveData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    val progress = it.progress
                    val value = progress.getInt(UploadWorker.Progress, 0)
                    print("progress:$value")
                    (activity as MainActivity).showProgressBar(value)
                    if (value == 100) {
                        (activity as MainActivity).hideProgressBar()
                        //todo workaround cause livedata seems not to refresh correctly
                        val action =
                                SurveyHistoryFragmentDirections.actionMonitoringHistoryFragmentSelf()
                        findNavController().navigate(action)

                    }
                }
            })

            /**
             * status:
             * - upload running => show loading animation + progress 73/192 (+ cancel option?)
             * - upload suceeded => show close button
             * - upload failed => retry button + close button
             */
        }
        return view
    }
}