package ch.protonmail.android.protonmailtest

import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DailyForecastViewModel : ViewModel() {
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

    val isRefreshing: LiveData<Boolean> = DailyForecastRepository.isFetching()

    val isOffline: LiveData<Boolean> = DailyForecastRepository.isLastFetchFailed()

    fun fetch() = DailyForecastRepository.fetch()
}

fun ComponentActivity.getDailyForecastViewModel() =
    ViewModelProvider(this).get(DailyForecastViewModel::class.java)