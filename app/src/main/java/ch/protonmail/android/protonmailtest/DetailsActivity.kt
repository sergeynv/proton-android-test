package ch.protonmail.android.protonmailtest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

/**
 * Shows all the details for a particular day.
 */
class DetailsActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var imageLabel: TextView
    private lateinit var btnDownload: View

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_details)

        imageView = findViewById(R.id.image_view)
        imageLabel = findViewById(R.id.label_image)
        btnDownload = findViewById(R.id.btn_download)

        with(intent.getParcelableExtra<DayForecast>(EXTRA_DAY_FORECAST)!!) {
            title = getString(R.string.title_day_index, dayIndex)

            findViewById<TextView>(R.id.description).text = description
            findViewById<TextView>(R.id.precipitation).text = "$rainChanceInPercent%"
            findViewById<TextView>(R.id.temp).text = "$low / $high"
            findViewById<TextView>(R.id.sun).text = "$sunrise / $sunset"

            btnDownload.setOnClickListener { loadImage(imageUrl) }
        }
    }

    private fun loadImage(url: String) = Picasso.get().load(url).into(imageView)

    companion object {
        private const val EXTRA_DAY_FORECAST =
            "ch.protonmail.android.protonmailtest.EXTRA_DAY_FORECAST"

        fun buildIntent(context: Context, dayForecast: DayForecast) =
            Intent(context, DetailsActivity::class.java)
                .apply { putExtra(EXTRA_DAY_FORECAST, dayForecast) }
    }
}
