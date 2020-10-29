package de.sodis.monitoring

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import de.sodis.monitoring.repository.worker.DownloadWorker
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.RootViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private val rootViewModel: RootViewModel by lazy {
        this.run {
            ViewModelProviders.of(this, MyViewModelFactory(application, emptyList()))
                .get(RootViewModel::class.java)
        }!!
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dashboard -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.intervieweeOverviewFragment)
                supportActionBar!!.title = getString(R.string.tab_village_overview)
            }
            R.id.monitoring -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.monitoringOverviewFragment)
                supportActionBar!!.title = getString(R.string.tab_monitoring)
            }
            R.id.monitoring_history -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.monitoringHistoryFragment)
                supportActionBar!!.title = getString(R.string.tab_history)
            }
            R.id.tasks -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.todoFragment)
                supportActionBar!!.title = "Tasks"
            }
        }
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        setContentView(R.layout.activity_main)
        //setup bottom navigation bar interaction listener
        bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(this)

        findNavController(R.id.nav_host_fragment).navigate(R.id.intervieweeOverviewFragment)
        supportActionBar!!.title = "Dashboard"

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


    }


    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            //todo store token, store name?!
            supportActionBar!!.title = "Registration"
            this.hide_bottom_navigation()
            findNavController(R.id.nav_host_fragment).navigate(R.id.registrationOverviewwFragment)
        }
        rootViewModel.requestData()
        rootViewModel.workInfoByIdLiveData.observe(this, Observer {
            if (it != null) {
                val progress = it.progress
                val value = progress.getInt(DownloadWorker.Progress, 0)
                print("progress:$value")
                showProgressBar(value)
                if (value == 100) {
                    //register user on Sodis API
                    Snackbar.make(
                        findViewById(R.id.nav_host_fragment),
                        getString(R.string.registration_successfull),
                        Snackbar.LENGTH_LONG
                    ).show()
                    this.hideProgressBar()
                }
            }
        })
    }


    override fun onBackPressed() {
        //only allow back press for convenience only on interviewee
        val currFragmentId = findNavController(R.id.nav_host_fragment).currentDestination!!.id
        if (currFragmentId == R.id.intervieweeDetailFragment || currFragmentId == R.id.surveyFragment) {
            super.onBackPressed()
        }
        if (currFragmentId == R.id.surveyFragment) {
            this.show_bottom_navigation()
        }
    }


}


public fun MainActivity.hideProgressBar() {
    this.progress_bar.visibility = View.GONE
}

public fun MainActivity.showProgressBar(value: Int) {
    this.progress_bar.visibility = View.VISIBLE
    this.progress_bar.progress = value
}

public fun MainActivity.hide_bottom_navigation() {
    this.bottom_navigation.visibility = View.GONE
}

public fun MainActivity.show_bottom_navigation() {
    this.bottom_navigation.visibility = View.VISIBLE
}

