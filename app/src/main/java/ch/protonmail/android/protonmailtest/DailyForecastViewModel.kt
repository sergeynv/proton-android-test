package ch.protonmail.android.protonmailtest

import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * A ViewModel shared by the [MainActivity] and both its Fragments - [UpcomingFragment] and
 * [HottestFragment]
 */
class DailyForecastViewModel : ViewModel() {

    /** Check if there is any available data (either fetched or cached). */
    val isDataAvailable: LiveData<Boolean> by lazy {
        Transformations.map(DailyForecastRepository.getData()) { it.isNotEmpty() }
    }

    /** Check if there is a ongoing network request to fetch the forecast data. */
    val isRefreshing: LiveData<Boolean> = DailyForecastRepository.isFetching()

    /** Check if the last network request to fetch the forecast data has failed. */
    val isOffline: LiveData<Boolean> = DailyForecastRepository.isLastFetchFailed()

    /** Get all available forecast data, sorted by [day index][DayForecast.dayIndex]. */
    val upcoming: LiveData<List<DayForecast>> by lazy {
        Transformations.map(DailyForecastRepository.getData()) { allForecast ->
            allForecast.sortedBy { it.dayIndex }
        }
    }

    /**
     * Get forecast data for dats with less than 50% chance of precipitation, in descending order
     * of [the highest daily temperature][DayForecast.high]
     */
    val hottest: LiveData<List<DayForecast>> by lazy {
        Transformations.map(DailyForecastRepository.getData()) { allForecast ->
            allForecast.filter { it.rainChance < 0.5 }.sortedByDescending { it.high }
        }
    }

    /** * Make another attempt to fetch data. */
    fun fetch() = DailyForecastRepository.fetch()

    /** Clear local cache. */
    fun clearCache() = DailyForecastRepository.clearCache()
}

// Utility extension method for retrieving instance of DailyForecastViewModel.
fun ComponentActivity.getDailyForecastViewModel() =
    ViewModelProvider(this).get(DailyForecastViewModel::class.java)