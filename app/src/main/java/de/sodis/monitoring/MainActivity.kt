package de.sodis.monitoring

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.sodis.monitoring.ui.fragment.MonitoringOverviewFragment
import de.sodis.monitoring.viewmodel.RootViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var rootViewModel: RootViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        setContentView(R.layout.activity_main)
        rootViewModel = this.run {
            ViewModelProviders.of(this).get(RootViewModel::class.java)
        }

        replaceFragments(MonitoringOverviewFragment(), "TAG_MONITORING_OVERVIEW")
    }

    override fun onBackPressed() {
        //only allow back press for convenience only on interviewee
        val surveyFragment= supportFragmentManager.findFragmentByTag("SURVEY_TAG")
        if (surveyFragment != null && surveyFragment.isVisible) {
            //super.onBackPressed()//todo
        }
    }
}

fun MainActivity.replaceFragments(fragmentNew: Fragment, tag: String) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
    transaction.replace(R.id.fragment_container, fragmentNew, tag)
    transaction.addToBackStack(null)
    transaction.commit()
}