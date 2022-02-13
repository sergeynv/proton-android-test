package ch.protonmail.android.protonmailtest

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.time.LocalTime

interface ForecastService {

    @GET("api/forecast")
    fun forecast(): Call<List<DayForecast>>

    companion object {
        private const val ENDPOINT = "https://5c5c8ba58d018a0014aa1b24.mockapi.io/"

        private val localTimeDeserializer = JsonDeserializer { json, _, _ ->
            LocalTime.ofSecondOfDay(json!!.asJsonPrimitive.asLong)
        }

        private val gson by lazy {
            GsonBuilder()
                .registerTypeAdapter(LocalTime::class.java, localTimeDeserializer)
                .create()
        }

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        val instance: ForecastService by lazy {
            retrofit.create(ForecastService::class.java)
        }
    }
}