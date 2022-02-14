package ch.protonmail.android.protonmailtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class for Fragments that display daily forecast.
 */
internal sealed class BaseDailyForecastFragment : Fragment() {
    protected lateinit var viewModel: DailyForecastViewModel
    private val adapter by lazy { DailyForecastAdapter { onItemClicked(it) } }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Let all the DailyForecastFragments hosted by the same Activity share the ViewModel (which
        // makes sense since we have a single REST API call which returns all of the data, and the
        // filtering and sorting is done locally)
        viewModel = ViewModelProvider(requireActivity()).get(DailyForecastViewModel::class.java)

        // Now that ViewModel is ready, we can get and subscribe to the LiveData.
        getForecastLiveData().observe(/* LifecycleOwner*/ this, /* Observer */ adapter)
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_forecast, container, false)

    final override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = view.findViewById<RecyclerView>(R.id.recycler_view).let { recyclerView ->
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    protected abstract fun getForecastLiveData(): LiveData<List<DayForecast>>

    // Launch DetailsActivity.
    private fun onItemClicked(dayForecast: DayForecast) =
        DetailsActivity.buildIntent(requireContext(), dayForecast)
            .also { startActivity(it) }
}

/**
 * Displays list of all available daily forecast.
 */
internal class UpcomingFragment : BaseDailyForecastFragment() {
    override fun getForecastLiveData() = viewModel.upcoming
}


/**
 * Displays list of available daily forecast for the days with less than 50% chance of
 * precipitation in in descending order of average daily temperature.
 */
internal class HottestFragment : BaseDailyForecastFragment() {
    override fun getForecastLiveData() = viewModel.hottest
}
