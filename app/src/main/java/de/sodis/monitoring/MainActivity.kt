package de.sodis.monitoring

import android.os.Bundle
import android.transition.Visibility
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import de.sodis.monitoring.ui.fragment.IntervieweeOverviewFragment
import de.sodis.monitoring.ui.fragment.MonitoringOverviewFragment
import de.sodis.monitoring.ui.fragment.RegistrationFragment
import de.sodis.monitoring.ui.fragment.TaskOverviewFragment
import de.sodis.monitoring.viewmodel.RootViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.continuable_list.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dashboard -> {
                replaceFragments(IntervieweeOverviewFragment(), "INTERVIEWEE_OVERVIEW")
                supportActionBar!!.title = "Dashboard"
            }
            R.id.monitoring -> {
                replaceFragments(MonitoringOverviewFragment(), "MONITORING_OVERVIEW")
                supportActionBar!!.title = "Monitoreo"
            }
            R.id.task -> {
                replaceFragments(TaskOverviewFragment(), "Task_OVERVIEW")
                supportActionBar!!.title = "Tasks"
            }

        }
        return true
    }


    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var rootViewModel: RootViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        setContentView(R.layout.activity_main)
        rootViewModel = this.run {
            ViewModelProviders.of(this).get(RootViewModel::class.java)
        }
        //setup bottom navigation bar interaction listener
        bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(this)


        replaceFragments(IntervieweeOverviewFragment(), "TAG_MONITORING_OVERVIEW")
        supportActionBar!!.title = "Dashboard"

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            //todo store token, store name?!
            supportActionBar!!.title = "Registration"
            this.replaceFragments(RegistrationFragment(), "TAG_REGISTRATION")
        }else{
            rootViewModel.requestData()
        }
    }


    override fun onBackPressed() {
        //only allow back press for convenience only on interviewee
        val surveyFragment = supportFragmentManager.findFragmentByTag("SURVEY_TAG")
        if (surveyFragment != null && surveyFragment.isVisible) {
            //super.onBackPressed()//todo
        }
    }


}

public fun MainActivity.replaceFragments(fragmentNew: Fragment, tag: String) {
    if (tag == "QUESTION_TAG" || tag == "TAG_REGISTRATION" ||tag == "SURVEY_TAG"
    ) {
        this.bottom_navigation.visibility = View.GONE//TODO navigation controler action based
    }
    else{
        this.bottom_navigation.visibility = View.VISIBLE
    }

    val transaction = supportFragmentManager.beginTransaction()
    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
    transaction.replace(R.id.fragment_container, fragmentNew, tag)
    transaction.addToBackStack(null)
    transaction.commit()
}

