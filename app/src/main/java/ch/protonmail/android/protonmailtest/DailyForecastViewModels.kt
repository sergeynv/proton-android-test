package ch.protonmail.android.protonmailtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ForecastViewModel : ViewModel() {
    val dailyForecast: LiveData<List<DayForecast>> = DailyForecastLiveData()
}