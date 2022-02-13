package ch.protonmail.android.protonmailtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class for displaying daily forecast.
 */
sealed class ForecastFragment : Fragment() {
    private val adapter by lazy { DailyForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)
        viewModel.dailyForecast.observe(/* LifecycleOwner*/ this, /* Observer */ adapter)
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
}

/**
 * Displays list of available daily forecast for the days with less than 50% chance of
 * precipitation in in descending order of average daily temperature.
 */
internal class HottestFragment : ForecastFragment()

/**
 * Displays list of all available daily forecast.
 */
internal class UpcomingFragment : ForecastFragment()