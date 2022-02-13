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

    val isDataAvailable: LiveData<Boolean> by lazy {
        Transformations.map(DailyForecastRepository.getData()) { it.isNotEmpty() }
    }

    val isRefreshing: LiveData<Boolean> = DailyForecastRepository.isFetching()

    val isOffline: LiveData<Boolean> = DailyForecastRepository.isLastFetchFailed()

    val upcoming: LiveData<List<DayForecast>> by lazy {
        Transformations.map(DailyForecastRepository.getData()) { allForecast ->
            allForecast.sortedBy { it.high }
        }
    }

    val hottest: LiveData<List<DayForecast>> by lazy {
        Transformations.map(DailyForecastRepository.getData()) { allForecast ->
            allForecast.filter { it.rainChance < 0.5 }.sortedBy { it.high }
        }
    }

    fun fetch() = DailyForecastRepository.fetch()
}

fun ComponentActivity.getDailyForecastViewModel() =
    ViewModelProvider(this).get(DailyForecastViewModel::class.java)