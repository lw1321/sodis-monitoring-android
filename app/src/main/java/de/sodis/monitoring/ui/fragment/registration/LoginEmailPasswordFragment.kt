package de.sodis.monitoring.ui.fragment.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import de.sodis.monitoring.R
import de.sodis.monitoring.loginEmailPassword
import de.sodis.monitoring.registerEmailPassword
import de.sodis.monitoring.ui.fragment.BaseListFragment
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_register_email_password.view.*

class LoginEmailPasswordFragment: BaseListFragment() {

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
            loginEmailPassword {
                id("loginEmailPassword")
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

                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity!!) { task ->
                                if (task.isSuccessful) {
                                    if(task.result!=null) {
                                        if(task.result!!.user!!.uid!=null) {
                                            findNavController().navigate(R.id.mainActivity)
                                        }
                                    }

                                } else {
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