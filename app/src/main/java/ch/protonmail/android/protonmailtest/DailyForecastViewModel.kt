package ch.protonmail.android.protonmailtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class DailyForecastViewModel : ViewModel() {
    private val allForecast: LiveData<List<DayForecast>?> by lazy { DailyForecastLiveData() }

    val upcoming: LiveData<List<DayForecast>> by lazy {
        Transformations.map(allForecast) { allForecast -> allForecast?.sortedBy { it.high } }
    }

    val hottest: LiveData<List<DayForecast>> by lazy {
        Transformations.map(allForecast) { allForecast ->
            allForecast?.filter { it.rainChance < 0.5 }?.sortedBy { it.high }
        }
    }
}