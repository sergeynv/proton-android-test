package ch.protonmail.android.protonmailtest

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.HttpURLConnection
import java.net.URL

/**
 * Base class for displaying daily forecast.
 */
internal abstract class ForecastFragment : Fragment() {
    private val adapter by lazy { ForecastAdapter() }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_forecast, container, false)

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<RecyclerView>(R.id.recycler_view).let { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }

        fetchData()
    }

    fun fetchData() {
        if (dataPresentInLocalStorage()) {
            fetchDataFromLocalStorage()
        } else {
            fetchDataFromServer()
        }
    }

    fun fetchDataFromServer() {
        FetchDataFromServerTask().execute()
    }

    fun fetchDataFromLocalStorage(): Array<String>? {
        // TODO implement
        return null
    }

    fun dataPresentInLocalStorage(): Boolean = true

    class FetchDataFromServerTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String {
            val url = URL("https://5c5c8ba58d018a0014aa1b24.mockapi.io/api/forecast")
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connect()

            val responseCode: Int = httpURLConnection.responseCode

            var response: String = ""
            if (responseCode == 200) {
                response = httpURLConnection.responseMessage
            }
            return response
        }
    }
}