package ch.protonmail.android.protonmailtest

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: DailyForecastViewModel
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get (or Create) a ViewModel (that's the same ViewModel instance the fragments will use as
        // well).
        viewModel = getDailyForecastViewModel()

        // Init UI.
        setContentView(R.layout.activity_main)
        viewPager = findViewById<ViewPager2>(R.id.view_pager)
            .apply { adapter = pagerAdapter }

        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnItemSelectedListener { onNavigationItemSelected(it.itemId) }

        val srl = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        srl.setOnRefreshListener { viewModel.fetch() }

        val offlineLabel = findViewById<View>(R.id.label_offline)
        val noDataLabel = findViewById<View>(R.id.label_no_data)

        // Now subscribe to the LiveData-s (exposed by the ViewModel) we are interested in on the
        // Activity scope.
        viewModel.isRefreshing.observe(/* LifecycleOwner */ this) { fetching ->
            srl.isRefreshing = fetching
        }
        viewModel.isOffline.observe(/* LifecycleOwner */ this) { isOffline ->
            offlineLabel.visibility = if (isOffline) View.VISIBLE else View.GONE
        }
        viewModel.isDataAvailable.observe(/* LifecycleOwner */ this) { dataAvailable ->
            if (dataAvailable) {
                viewPager.visibility = View.VISIBLE
                noDataLabel.visibility = View.GONE
            } else {
                // Let's not remove the ViewPager from the hierarchy completely (using GONE), but
                // just "hide" it (the adapter may not like the former, which is not something,
                // which needs to be investigated, which takes more time, than a test assignment
                // should take)
                viewPager.visibility = View.INVISIBLE
                noDataLabel.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> viewModel.fetch()
            R.id.menu_clear_cache -> viewModel.clearCache()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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

        override fun createFragment(position: Int): Fragment = when (position) {
            POSITION_UPCOMING -> UpcomingFragment()
            POSITION_HOTTEST -> HottestFragment()
            else -> throw IllegalArgumentException("Illegal position index $position")
        }
    }

    companion object {
        private const val POSITION_UPCOMING = 0;
        private const val POSITION_HOTTEST = 1;
    }
}
