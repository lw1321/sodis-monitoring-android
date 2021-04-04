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
import de.sodis.monitoring.viewmodel.*
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_register_email_password.view.*
import kotlinx.android.synthetic.main.view_holder_register_name.view.*

class RegistrationEmailPasswordFragment : BaseListFragment() {


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
        view?.navigation_cancel_button?.isGone = true

        recyclerView.withModels {
            registerEmailPassword {
                id("registration")
                onClick{ _ ->
                    //validation
                    val email = view!!.registration_email.text.toString()
                    if(!email.contains("@")){
                        Snackbar.make(
                            view,
                            getString(R.string.invalid_email_message),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }else{
                        val password = view.registration_password.text.toString()

                        if(password.length < 7){
                            Snackbar.make(
                                view,
                                getString(R.string.password_too_short_message),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else{
                            // ok all valid, lets create an account
                            //Sign in with email and password

                            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity!!) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI request the name of the user and send it to the sodis api
                                    findNavController().navigate(R.id.registrationNameFragment)
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("E", "registrationEmailPassword:failure", task.exception)
                                    Toast.makeText(
                                        activity!!.baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        }
                    }
                }

            }
        }

        return view
    }
}