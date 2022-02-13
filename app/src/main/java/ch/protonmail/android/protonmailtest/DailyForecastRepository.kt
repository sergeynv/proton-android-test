package ch.protonmail.android.protonmailtest

import android.util.Log
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DailyForecastRepository {
    private const val TAG = "DailyForecastLiveData"
    // Would love to disable this, but the mockapi.io we are using here, is just sooo unreliable...
    private const val DEBUG_SHOW_TOAST = true

    private val fetchedData = object : MutableLiveData<List<DayForecast>?>(null) {
        // (Re-)fetch data from when when new becomes active
        override fun onActive() = fetch()
    }

    private val cachedData = MutableLiveData<List<DayForecast>>(emptyList())

    // LiveData that "combines" fetched data and the cached data, by always "prioritizing" the
    // fetched data when it's available (non-null).
    private val data by lazy {
        object : MediatorLiveData<List<DayForecast>>() {
            init {
                // Use updated fetched data, unless it's null, then use cached data.
                addSource(fetchedData) { data -> postValue(data ?: cachedData.value) }
                // Ignore updates to the cached data, unless fetched data is null.
                addSource(cachedData) { data -> if (fetchedData.value == null) postValue(data)}
            }
        }
    }

    private val fetching = MutableLiveData(false)

    private val lastFetchFailed = MutableLiveData(false)

    fun getData(): LiveData<List<DayForecast>> = data

    fun isFetching(): LiveData<Boolean> = fetching

    fun isLastFetchFailed(): LiveData<Boolean> = lastFetchFailed

    // Restricting this so that it should only be called from the Main Thread (which is kindof
    // cheating slightly, normally this the proper should be implemented here, but that feels out of
    // scope for a test assignment).
    @MainThread
    fun fetch() {
        Log.d(TAG, "fetch()")

        if (fetching.value!!) {
            Log.d(TAG, "  > already in progress")
            return
        }
        fetching.postValue(true)

        ForecastRestService.forecast().enqueue(fetchingCallback)

        maybeShowDebugToast("fetch() started")
    }

    private fun maybeShowDebugToast(text: String) {
        if (!DEBUG_SHOW_TOAST) return
        Toast.makeText(ForecastApplication.instance, text, Toast.LENGTH_SHORT).show()
    }

    private val fetchingCallback = object : Callback<List<DayForecast>> {
        override fun onResponse(
            call: Call<List<DayForecast>>,
            response: Response<List<DayForecast>>
        ) {
            Log.d(TAG, "onResponse() $response");

            data.postValue(if (response.isSuccessful) response.body() else emptyList())
            fetching.postValue(false)
            lastFetchFailed.postValue(false)

            maybeShowDebugToast("fetch() completed, code=${response.code()}")
        }

        override fun onFailure(call: Call<List<DayForecast>>, t: Throwable) {
            Log.d(TAG, "onFailure()", t)

            data.postValue(emptyList())
            fetching.postValue(false)
            lastFetchFailed.postValue(true)

            maybeShowDebugToast("fetch() failed, ${t.message}")
        }
    }
}