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
                    auth.signInAnonymously().addOnCompleteListener(activity!!) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            findNavController().navigate(R.id.registrationNameFragment)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("E", "signInAnonymously:failure", task.exception)
                            Toast.makeText(
                                activity!!.baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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