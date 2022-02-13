package ch.protonmail.android.protonmailtest

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalTime

@Dao
interface ForecastLocalStorage {
    @Query("SELECT * FROM forecast")
    fun getAll(): List<DayForecast>

    @Insert
    fun store(forecasts: List<DayForecast>)

    @Query("DELETE FROM forecast")
    fun clear()

    companion object : ForecastLocalStorage {
        private val instance: ForecastLocalStorage by lazy {
            Room.databaseBuilder(ForecastApplication.instance, CacheDatabase::class.java, "cache")
                .build()
                .forecastLocalStorage()
        }

        override fun getAll(): List<DayForecast> = instance.getAll()
        override fun store(forecasts: List<DayForecast>) = instance.store(forecasts)
        override fun clear() = instance.clear()
    }
}

private class Converters {
    @TypeConverter
    fun localTimeToSecondOfDay(localTime: LocalTime): Int = localTime.toSecondOfDay()

    @TypeConverter
    fun localTimeFromSecondOfDay(secondOfDay: Int): LocalTime =
        LocalTime.ofSecondOfDay(secondOfDay.toLong())
}

@Database(entities = [DayForecast::class], version = 1)
@TypeConverters(Converters::class)
private abstract class CacheDatabase : RoomDatabase() {
    abstract fun forecastLocalStorage(): ForecastLocalStorage
}