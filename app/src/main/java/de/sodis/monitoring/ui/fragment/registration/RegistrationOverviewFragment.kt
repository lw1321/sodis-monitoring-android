package de.sodis.monitoring.ui.fragment.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import de.sodis.monitoring.*
import de.sodis.monitoring.ui.fragment.BaseListFragment
import de.sodis.monitoring.ui.fragment.IntervieweeOverviewFragment
import de.sodis.monitoring.viewmodel.*
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_register_name.view.*

class RegistrationOverviewFragment : BaseListFragment() {
    private lateinit var auth: FirebaseAuth

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
            registerOverview {
                id("registration")
                onClickAnonymousRegistation { _ ->
                    //do the navigat
                    findNavController().navigate(R.id.registrationNameFragment)
                }
                onClickEmailPasswordRegistration { _ ->
                    findNavController().navigate(R.id.registrationEmailPasswordFragment)
                }
                onLoginEmailPassword {
                    _ ->
                    findNavController().navigate(R.id.loginEmailPassword)
                }
            }
        }
        return view
    }
}