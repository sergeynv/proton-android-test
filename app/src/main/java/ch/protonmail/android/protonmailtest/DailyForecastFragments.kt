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
 * Base class for displaying daily forecast.
 */
internal sealed class BaseDailyForecastFragment : Fragment() {
    protected lateinit var viewModel: ForecastViewModel
    private val adapter by lazy { DailyForecastAdapter() }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel.
        viewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)
        // Now that ViewModel is ready, we can get and subscribe to the LiveData.
        getForecastLiveData().observe(/* LifecycleOwner*/ this, /* Observer */ adapter)
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_forecast, container, false)

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<RecyclerView>(R.id.recycler_view).let { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    abstract fun getForecastLiveData(): LiveData<List<DayForecast>>
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
