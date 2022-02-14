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
    fun insertAll(forecasts: List<DayForecast>)

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

        override fun insertAll(forecasts: List<DayForecast>) = executor.execute {
            // Actually this would need to be an atomic operation, otherwise we may lose cache,
            // if the process is killed or the app crashes after clear() call is completed, but
            // before insertAll() had a chance to fully run, or if insertAll() simply fails.
            // But for the test assignment this will have to do.
            dao.clear()
            dao.insertAll(forecasts)
            maybeShowDebugToast("Cache updated")
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