package ch.protonmail.android.protonmailtest

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ch.protonmail.android.protonmailtest.ForecastApplication.Companion.maybeShowDebugToast
import java.time.LocalTime
import java.util.concurrent.Executors

@Dao
interface ForecastLocalStorage {
    @Query("SELECT * FROM forecast")
    fun getAll(): LiveData<List<DayForecast>>

    @Insert
    fun store(forecasts: List<DayForecast>)

    @Query("DELETE FROM forecast")
    fun clear()

    companion object : ForecastLocalStorage {
        private val dao: ForecastLocalStorage by lazy {
            Room.databaseBuilder(ForecastApplication.instance, CacheDatabase::class.java, "cache")
                .build()
                .forecastLocalStorage()
        }
        private val executor by lazy { Executors.newSingleThreadExecutor() }

        override fun getAll() = dao.getAll()

        override fun store(forecasts: List<DayForecast>) = executor.execute {
            dao.store(forecasts)
            maybeShowDebugToast("Cache stored")
        }

        override fun clear() = executor.execute {
            dao.clear()
            maybeShowDebugToast("Cache cleared")
        }
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