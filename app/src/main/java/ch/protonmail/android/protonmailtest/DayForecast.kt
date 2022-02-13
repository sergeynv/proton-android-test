package ch.protonmail.android.protonmailtest

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalTime

@Entity(tableName = "forecast")
data class DayForecast(
    @PrimaryKey @SerializedName("day") val dayIndex: Int,
    val description: String?,
    val sunrise: LocalTime,
    val sunset: LocalTime,
    @SerializedName("chance_rain") val rainChance: Float,
    val high: Int,
    val low: Int,
    val image: String?
) {
    @Ignore
    val rainChanceInPercent: Int = (rainChance * 100).toInt()
}