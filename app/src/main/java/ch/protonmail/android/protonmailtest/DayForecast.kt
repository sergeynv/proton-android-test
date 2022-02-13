package ch.protonmail.android.protonmailtest

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class DayForecast(
    @SerializedName("day") val dayIndex: Int,
    val description: String?,
    val sunrise: LocalTime,
    val sunset: LocalTime,
    @SerializedName("chance_rain") val rainChance: Float,
    val high: Int,
    val low: Int,
    val image: String?
) {
    val rainChanceInPercent: Int = (rainChance * 100).toInt()
}