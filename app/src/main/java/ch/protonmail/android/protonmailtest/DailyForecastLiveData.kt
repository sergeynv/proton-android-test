package ch.protonmail.android.protonmailtest

import android.util.Log
import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyForecastLiveData : LiveData<List<DayForecast>?>() {
    private var isLoading: Boolean = false

    override fun onActive() {
        if (value != null || isLoading) return

        isLoading = true
        ForecastService.instance.forecast().enqueue(callback)
    }

    override fun postValue(value: List<DayForecast>?) {
        isLoading = false
        super.postValue(value)
    }

    private val callback = object : Callback<List<DayForecast>> {
        override fun onResponse(
            call: Call<List<DayForecast>>,
            response: Response<List<DayForecast>>
        ) {
            Log.d(TAG, "onResponse() $response");
            postValue(if (response.isSuccessful) response.body() else null)
        }

        override fun onFailure(call: Call<List<DayForecast>>, t: Throwable) {
            Log.d(TAG, "onFailure()", t)
            postValue(null)
        }
    }

    companion object {
        private const val TAG = "DailyForecastLiveData"
    }
}