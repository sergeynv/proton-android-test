package ch.protonmail.android.protonmailtest

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DailyForecastRepository {
    private const val TAG = "DailyForecastLiveData"

    private val data by lazy {
        object : MutableLiveData<List<DayForecast>>(emptyList()) {
            // (Re-)fetch data from when when new becomes active
            override fun onActive() = fetch()
        }
    }

    private val fetching = MutableLiveData(false)

    private val lastFetchFailed = MutableLiveData(false)

    fun getData(): LiveData<List<DayForecast>> = data

    fun isFetching(): LiveData<Boolean> = fetching

    fun isLastFetchFailed(): LiveData<Boolean> = lastFetchFailed

    // @MainThread is slightly cheating...
    // Will come back to implement proper thread-safety if have time.
    @MainThread
    fun fetch() {
        Log.d(TAG, "fetch()")

        if (fetching.value!!) {
            Log.d(TAG, "  > already in progress")
            return
        }
        fetching.postValue(true)

        ForecastRestService.forecast().enqueue(callback)
    }

    private val callback = object : Callback<List<DayForecast>> {
        override fun onResponse(
            call: Call<List<DayForecast>>,
            response: Response<List<DayForecast>>
        ) {
            Log.d(TAG, "onResponse() $response");

            data.postValue(if (response.isSuccessful) response.body() else emptyList())
            fetching.postValue(false)
            lastFetchFailed.postValue(false)
        }

        override fun onFailure(call: Call<List<DayForecast>>, t: Throwable) {
            Log.d(TAG, "onFailure()", t)

            data.postValue(emptyList())
            fetching.postValue(false)
            lastFetchFailed.postValue(true)
        }
    }
}