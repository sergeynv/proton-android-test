package ch.protonmail.android.protonmailtest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val pager = findViewById<ViewPager>(R.id.pager)
//        val adapter = TabsAdapter(this, supportFragmentManager)
//        pager.adapter = adapter

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            onNavigationItemSelected(it.itemId)
        }
    }

    private fun onNavigationItemSelected(id: Int): Boolean {
        when (id) {
            R.id.navigation_item_upcoming -> Log.d(TAG, """Selected "Upcoming"""")
            R.id.navigation_item_hottest -> Log.d(TAG, """Selected "Hottest"""")
            else -> return false
        }
        return true
    }

    private fun initTabs() {
        //TODO
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
