package de.sodis.monitoring

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.sodis.monitoring.ui.fragment.MonitoringOverviewFragment
import de.sodis.monitoring.viewmodel.RootViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var rootViewModel: RootViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootViewModel = this.run {
            ViewModelProviders.of(this).get(RootViewModel::class.java)
        }
        replaceFragments(MonitoringOverviewFragment())
    }
}

fun MainActivity.replaceFragments(fragmentNew: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
    transaction.replace(R.id.fragment_container, fragmentNew)
    transaction.addToBackStack(null)
    transaction.commit()
}