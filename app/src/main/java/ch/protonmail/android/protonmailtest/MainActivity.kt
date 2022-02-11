package ch.protonmail.android.protonmailtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById<ViewPager2>(R.id.view_pager).apply { adapter = pagerAdapter }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            onNavigationItemSelected(it.itemId)
        }
    }

    private fun onNavigationItemSelected(id: Int): Boolean {
        when (id) {
            R.id.navigation_item_upcoming -> viewPager.currentItem = POSITION_UPCOMING
            R.id.navigation_item_hottest -> viewPager.currentItem = POSITION_HOTTEST
            else -> return false
        }
        return true
    }

    private val pagerAdapter = object : FragmentStateAdapter(this) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment = when(position) {
            POSITION_UPCOMING -> UpcomingFragment()
            POSITION_HOTTEST -> HottestFragment()
            else -> throw IllegalArgumentException("Illegal position index $position")
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val POSITION_UPCOMING = 0;
        private const val POSITION_HOTTEST = 1;
    }
}
