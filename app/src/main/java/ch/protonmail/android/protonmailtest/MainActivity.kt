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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: DailyForecastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Update the title (in the action bar).
        setTitle(R.string.title_forecast)

        // Get (or Create) a ViewModel (that's the same ViewModel instance the fragments will use as
        // well).
        viewModel = getDailyForecastViewModel()

        // Init UI.
        setContentView(R.layout.activity_main)

        // Set up the TabLayout and the ViewPager and "connect" the two.
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
            .apply { adapter = pagerAdapter }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                TAB_INDEX_UPCOMING -> tab.setText(R.string.tab_title_upcoming)
                TAB_INDEX_HOTTEST -> tab.setText(R.string.tab_title_hottest)
            }
        }.attach()

        val offlineLabel = findViewById<View>(R.id.label_offline)
        val noDataLabel = findViewById<View>(R.id.label_no_data)

        val srl = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
            .apply { setOnRefreshListener { viewModel.fetch() } }

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

    private val pagerAdapter = object : FragmentStateAdapter(this) {
        override fun getItemCount() = TABS_COUNT

        override fun createFragment(position: Int): Fragment = when (position) {
            TAB_INDEX_UPCOMING -> UpcomingFragment()
            TAB_INDEX_HOTTEST -> HottestFragment()
            else -> throw IllegalArgumentException("Illegal position index $position")
        }
    }

    companion object {
        private const val TABS_COUNT = 2
        private const val TAB_INDEX_UPCOMING = 0
        private const val TAB_INDEX_HOTTEST = 1
    }
}
