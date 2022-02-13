package ch.protonmail.android.protonmailtest

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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

    val isRefreshing: LiveData<Boolean> = MutableLiveData(false)
    val isOffline: LiveData<Boolean> = MutableLiveData(false)

    fun fetch() {
        Log.d(TAG, "fetch()")
    }

    companion object {
        private const val TAG = "DailyForecastViewModel"
    }
}

fun ComponentActivity.getDailyForecastViewModel() =
    ViewModelProvider(this).get(DailyForecastViewModel::class.java)