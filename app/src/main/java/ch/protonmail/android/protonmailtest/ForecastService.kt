package ch.protonmail.android.protonmailtest

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ForecastService {

    @GET("api/forecast")
    fun forecast(): Call<List<String>>

    companion object {
        val instance: ForecastService by lazy {
            Retrofit.Builder()
                .baseUrl("https://5c5c8ba58d018a0014aa1b24.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ForecastService::class.java)
        }
    }
}