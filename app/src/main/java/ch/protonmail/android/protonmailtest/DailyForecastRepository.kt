package ch.protonmail.android.protonmailtest

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ch.protonmail.android.protonmailtest.DailyForecastRepository.clearCache
import ch.protonmail.android.protonmailtest.DailyForecastRepository.fetch
import ch.protonmail.android.protonmailtest.DailyForecastRepository.getData
import ch.protonmail.android.protonmailtest.DailyForecastRepository.isFetching
import ch.protonmail.android.protonmailtest.DailyForecastRepository.isLastFetchFailed
import ch.protonmail.android.protonmailtest.ForecastApplication.Companion.maybeShowDebugToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @see [getData]
 * @see [isFetching]
 * @see [isLastFetchFailed]
 * @see [fetch]
 * @see [clearCache]
 */
object DailyForecastRepository {
    private const val TAG = "DailyForecastLiveData"

    // Doesn't need to be initialized lazily, since it really doesn't do anything interesting by
    // itself.
    private val fetchedData = MutableLiveData<List<DayForecast>?>()

    // LiveData of the locally-stored (cached) forecasts. We do not update this LiveData directly
    // here, just letting Room to do its job.
    private val cachedData: LiveData<List<DayForecast>> by lazy {
        // Make sure the List<LiveData> is never null here.
        Transformations.map(ForecastLocalStorage.getAll()) { it ?: emptyList() }
    }

    // LiveData that "combines" fetched data and the cached data, by always "prioritizing" the
    // fetched data when it's available (non-null).
    private val data by lazy {
        object : MediatorLiveData<List<DayForecast>>() {
            private var hasFetchedData = false
            private var hasBeenActive = false

            init {
                addSource(fetchedData) { data ->
                    // Update value, unless the new data is null.
                    if (data != null) {
                        hasFetchedData = true
                        postValue(data)
                    }
                }
                addSource(cachedData) { data ->
                    // Ignore updates to the cached data, unless there is no fetched data.
                    if (hasFetchedData.not()) postValue(data)
                }
                // Value here should never be null, so start with an empty list.
                value = emptyList()
            }

            override fun onActive() {
                super.onActive()

                if (hasBeenActive.not()) {
                    // fetch() when becoming "active" for the first time only
                    fetch()

                    hasBeenActive = true
                }
            }
        }
    }

    // Indicator of whether there is an "in-progress" network request to fetch the forecast.
    private val fetching = MutableLiveData(false)

    // Indicator of whether the last network request to fetch the forecast has failed.
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
        maybeShowDebugToast("fetch() started")

        if (fetching.value!!) {
            Log.d(TAG, "  > already in progress")
            return
        }
        fetching.postValue(true)

        ForecastRestService.forecast().enqueue(fetchingCallback)
    }

    /** Clears local cache. */
    fun clearCache() = ForecastLocalStorage.clear()

    private val fetchingCallback = object : Callback<List<DayForecast>> {
        override fun onResponse(
            call: Call<List<DayForecast>>,
            response: Response<List<DayForecast>>
        ) {
            Log.d(TAG, "onResponse() $response");
            maybeShowDebugToast("fetch() completed, code=${response.code()}")

            if (response.isSuccessful) {
                // Only post to the fetchedData LiveData, otherwise we'll keep the value that's
                // already in there and indicate that the last fetch has failed (by posting to the
                // lastFetchFailed LiveData).
                val data = response.body()!!
                fetchedData.postValue(response.body())

                // Cache the data we just fetched.
                ForecastLocalStorage.insertAll(data)
            }
            fetching.postValue(false)
            lastFetchFailed.postValue(response.isSuccessful.not())
        }

        override fun onFailure(call: Call<List<DayForecast>>, t: Throwable) {
            Log.d(TAG, "onFailure()", t)
            maybeShowDebugToast("fetch() failed, ${t.message}")

            // Do not post to the fetchedData LiveData: keeping the value that's already in there is
            // fine, we just need to indicate that the last fetch has failed (by posting to the
            // lastFetchFailed LiveData).
            fetching.postValue(false)
            lastFetchFailed.postValue(true)
        }
    }
}