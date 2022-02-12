package ch.protonmail.android.protonmailtest

import com.google.gson.annotations.SerializedName

data class DayForecast(
    val day: Int,
    val description: String?,
    val sunrise: Int,
    val sunset: Int,
    @SerializedName("chance_rain") val chanceRain: Float,
    val high: Int,
    val low: Int,
    val image: String?
)