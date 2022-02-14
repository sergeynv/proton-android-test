package ch.protonmail.android.protonmailtest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Shows all the details for a particular day.
 */
class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dayForecast = intent.getParcelableExtra<DayForecast>(EXTRA_DAY_FORECAST)!!
        title = getString(R.string.title_day_index, dayForecast.dayIndex)

        setContentView(R.layout.activity_details)
        //findViewById<Button>(R.id.download).setOnClickListener(downloadListener)
    }

    companion object {
        private const val EXTRA_DAY_FORECAST =
            "ch.protonmail.android.protonmailtest.EXTRA_DAY_FORECAST"

        fun buildIntent(context: Context, dayForecast: DayForecast) =
            Intent(context, DetailsActivity::class.java)
                .apply { putExtra(EXTRA_DAY_FORECAST, dayForecast) }
    }
}
