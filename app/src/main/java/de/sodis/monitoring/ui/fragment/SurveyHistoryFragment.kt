package de.sodis.monitoring.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProviders
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.sodis.monitoring.R
import de.sodis.monitoring.repository.worker.UploadWorker
import de.sodis.monitoring.viewmodel.HistoryViewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*

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
        view?.navigation_forward_button_1?.setImageResource(R.drawable.ic_baseline_backup_24)
        //view.dataBinding.root.survey1Icon.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (nutricionCompleted) R.color.colorGreen700 else R.color.colorGrey700))
        view?.navigation_forward_button_1?.setOnClickListener {
            //sync all completed surveys and show dialog with status and option to cancel the upload

            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()
            WorkManager.getInstance(activity!!.applicationContext).enqueue(uploadWorkRequest)


            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }
            builder?.setTitle(getString(R.string.upload_data_dialog_title))
            //builder?.setMessage(getString(R.string.enter_family_name))
            builder?.setView(inflater.inflate(R.layout.upload_dialog, container, false))
            //todo observe count of not synced surveys from all surveys

            /*
            builder?.setPositiveButton(getString(R.string.save),
                    DialogInterface.OnClickListener { dialog, whichButton -> //What ever you want to do with the value
                        //close if upload completed
                    })*/

            builder?.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        // what ever you want to do with No option.
                        //TODO stop the upload
                    })


// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            val dialog: AlertDialog? = builder?.create()
            dialog?.show()
        }
        view?.navigation_forward_button_left?.isGone = true
        view?.navigation_cancel_button?.isGone = true
        return view
    }
}