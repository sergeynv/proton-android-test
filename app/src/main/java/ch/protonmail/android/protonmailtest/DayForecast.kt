package ch.protonmail.android.protonmailtest

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalTime

@Entity(tableName = "forecast")
data class DayForecast(
    @PrimaryKey @SerializedName("day") val dayIndex: Int,
    val description: String,
    val sunrise: LocalTime,
    val sunset: LocalTime,
    @SerializedName("chance_rain") val rainChance: Float,
    val high: Int,
    val low: Int,
    val image: String
) : Parcelable {
    val rainChanceInPercent: Int
        get() = (rainChance * 100).toInt()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dayIndex)
        parcel.writeString(description)
        parcel.writeInt(sunrise.toSecondOfDay())
        parcel.writeInt(sunset.toSecondOfDay())
        parcel.writeFloat(rainChance)
        parcel.writeInt(high)
        parcel.writeInt(low)
        parcel.writeString(image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DayForecast> {
        override fun createFromParcel(parcel: Parcel) = DayForecast(
            parcel.readInt(),
            parcel.readString()!!,
            LocalTime.ofSecondOfDay(parcel.readInt().toLong()),
            LocalTime.ofSecondOfDay(parcel.readInt().toLong()),
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!)

        override fun newArray(size: Int): Array<DayForecast?> = arrayOfNulls(size)
    }
}