package de.sodis.monitoring.ui.fragment

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
import de.sodis.monitoring.MainActivity
import de.sodis.monitoring.R
import de.sodis.monitoring.register
import de.sodis.monitoring.show_bottom_navigation
import kotlinx.android.synthetic.main.continuable_list.view.*

class RegistrationFragment : BaseListFragment() {


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

        recyclerView.withModels {
            register {
                id("registration")
                onClick { _ ->
                    //Firebase auth
                    auth.signInAnonymously().addOnCompleteListener(activity!!) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            Snackbar.make(view!!, "Registro exitoso!", Snackbar.LENGTH_LONG).show()

                            (activity as MainActivity).show_bottom_navigation()
                            findNavController().navigate(R.id.intervieweeOverviewFragment)
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
            }
        }

        return view
    }
}