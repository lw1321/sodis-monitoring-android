package de.sodis.monitoring.ui.fragment.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.internal.LifecycleCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import de.sodis.monitoring.*
import de.sodis.monitoring.repository.worker.DownloadWorker
import de.sodis.monitoring.ui.fragment.BaseListFragment
import de.sodis.monitoring.viewmodel.*
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_register_name.view.*

//Registration on Firebase Succeeded, new User is created. Now lets get the name of the user and
// register it with name and type
class RegistrationNameFragment : BaseListFragment() {


    private lateinit var auth: FirebaseAuth

    private val registerViewModel: RegisterViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, emptyList()))
                .get(RegisterViewModel::class.java)
        }!!
    }
    private val rootViewModel: RootViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, emptyList()))
                .get(RootViewModel::class.java)
        }!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.navigation_forward_button_1?.isGone = true
        view?.navigation_forward_button_left?.isGone = true

        recyclerView.withModels {
            registerName {
                id("registration")
                onClick { _ ->
                    val user = auth.currentUser
                    val lastName = view!!.registration_second_name.text
                    val firstName = view.registration_name.text
                    if (lastName.isEmpty() || firstName.isEmpty()) {
                        Snackbar.make(
                            view,
                            getString(R.string.name_empty_message),
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        val type = if (user!!.isAnonymous) 0 else 1
                        registerViewModel.register(firstName.toString(), lastName.toString(), type)
                        rootViewModel.requestData()
                        rootViewModel.workInfoByIdLiveData.observe(viewLifecycleOwner, Observer {
                            if(it != null){
                                val progress = it.progress
                                val value = progress.getInt(DownloadWorker.Progress, 0)
                                print("progress:$value")
                                (activity as MainActivity).showProgressBar(value)
                                if(value == 100){
                                    (activity as MainActivity).hideProgressBar()
                                    findNavController().navigate(R.id.mainActivity)
                                }
                            }
                        })
                        //register user on Sodis API
                        Snackbar.make(
                            view!!,
                            getString(R.string.registration_successfull),
                            Snackbar.LENGTH_LONG
                        ).show()
                    
                    }
                }
            }
        }
        return view
    }
}