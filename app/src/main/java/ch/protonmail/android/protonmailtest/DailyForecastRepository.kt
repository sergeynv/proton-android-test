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
 * The class responsible for loading, caching and managing forecast data, which provides APIs to
 * retrieve the data itself as well as information about its state (eg. [isLastFetchFailed]) to the
 * UI-layer ([DailyForecastViewModel]).
 *
 * @see [getData]
 * @see [isFetching]
 * @see [isLastFetchFailed]
 * @see [fetch]
 * @see [clearCache]
 */
object DailyForecastRepository {
    private const val TAG = "DailyForecastLiveData"

    /**
     * Contains the last successfully fetched (over the network) data.
     *
     * This [LiveData] is not accessible outside of the [DailyForecastRepository] directly, but is
     * a "part of" [data LiveData][data] which could available to clients of the
     * [DailyForecastRepository] via [getData].
     * @see getData
     *
     * Note: it doesn't need to be initialized lazily, since it really doesn't do anything
     * interesting by itself.
     */
    private val fetchedData = MutableLiveData<List<DayForecast>?>()


    /**
     * Contains the locally stored (cached) forecasts. [DailyForecastRepository] does not make
     * changes to this [LiveData] directly, all the changes "flow" through
     * [Room][androidx.room.Room].
     * @see ForecastLocalStorage
     *
     * This [LiveData] is not accessible outside of the [DailyForecastRepository] directly, but is
     * a "part of" [data LiveData][data] which could available to clients of the
     * [DailyForecastRepository] via [getData].
     * @see getData
     */
    private val cachedData: LiveData<List<DayForecast>> by lazy {
        // Make sure the List<LiveData> is never null here.
        Transformations.map(ForecastLocalStorage.getAll()) { it ?: emptyList() }
    }


    /**
     * Combines [fetched][fetchedData] and [cached][cachedData] data into a single [LiveData],
     * exposed to the clients of [DailyForecastRepository] via [getData]
     * @see getData
     *
     * Note: we always prioritize the [fetched data][fetchedData] when is is available (non-null).
     */
    private val data by lazy {
        object : MediatorLiveData<List<DayForecast>>() {
            private var hasFetchedData = false
            private var hasBeenActive = false

            init {
                // Add fetched data
                addSource(fetchedData) { data ->
                    // Update value, unless the new data is null.
                    if (data != null) {
                        hasFetchedData = true
                        postValue(data)
                    }
                }

                // Add cached data.
                addSource(cachedData) { data ->
                    // Ignore updates to the cached data, unless there is no fetched data.
                    if (hasFetchedData.not()) postValue(data)
                }

                // Value here SHOULD NEVER be null, so start with an empty list.
                value = emptyList()
            }

            override fun onActive() {
                super.onActive()

                if (!hasBeenActive) {
                    // fetch() when becoming "active" for the first time only.
                    fetch()

                    hasBeenActive = true
                }
            }
        }
    }

    /**
     * Indicates whether there is an "in-progress" network request to fetch the forecast.
     * Available outside via [isFetching].
     * @see isFetching
     */
    private val fetching = MutableLiveData(false)

    /**
     * Indicates whether the last network request to fetch the forecast has failed.
     * Available outside via [isLastFetchFailed].
     * @see isLastFetchFailed
     */
    private val lastFetchFailed = MutableLiveData(false)

    /**
     * Get the available forecast data.
     * @return [LiveData] that contains forecast data.
     */
    fun getData(): LiveData<List<DayForecast>> = data

    /**
     * @return [LiveData] that indicates whether there is an "in-progress" network request to fetch
     * the forecast.
     */
    fun isFetching(): LiveData<Boolean> = fetching

    /**
     * @return [LiveData] that indicates whether the last network request to fetch the forecast has
     * failed.
     */
    fun isLastFetchFailed(): LiveData<Boolean> = lastFetchFailed

    /**
     * Make another attempt to fetch data.
     *
     * WARNING: this method should only be invoked on the MainThread.
     * (in the real application this should rather be made thread-safe, but for the test assignments
     * "restricting" this to the MainThread seems reasonable)
     */
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

    /** Clear local cache. */
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