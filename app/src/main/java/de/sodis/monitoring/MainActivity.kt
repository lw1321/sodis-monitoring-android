package de.sodis.monitoring

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import de.sodis.monitoring.viewmodel.RootViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dashboard -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.intervieweeOverviewFragment)
                supportActionBar!!.title = "Dashboard"
            }
            R.id.monitoring -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.monitoringOverviewFragment)
                supportActionBar!!.title = "Monitoreo"
            }
            R.id.task -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.taskOverviewFragment)
                supportActionBar!!.title = "Tasks"
            }
            R.id.monitoring_history-> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.monitoringHistoryFragment)
                supportActionBar!!.title = "History"
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
          }else{
            rootViewModel.requestData()
        }
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

public fun MainActivity.hide_bottom_navigation() {
    this.bottom_navigation.visibility = View.GONE
}

public fun MainActivity.show_bottom_navigation() {
    this.bottom_navigation.visibility = View.VISIBLE
}

