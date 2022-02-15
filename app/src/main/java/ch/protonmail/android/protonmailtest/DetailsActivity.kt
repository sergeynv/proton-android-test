package ch.protonmail.android.protonmailtest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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

            btnDownload.setOnClickListener { loadImage(dayForecast = this) }

            // Also try loading form cache right now.
            loadImage(dayForecast = this, fromCacheOnly = true)
        }
    }

    override fun onDestroy() {
        // Need to cancel the image load request, so that we do not leak the ImageView, the
        // Callback and the whole Activity along with them.
        // (Actually would be great to move this to a ViewModel, and keep the request going if this
        // is simply a configuration change, but that feels out of scope for a test assignment.)
        cancelLoadImage(imageView)

        super.onDestroy()
    }

    private fun loadImage(dayForecast: DayForecast, fromCacheOnly: Boolean = false) {
        // Only show "Downloading..." label if this is a network request.
        if (!fromCacheOnly) imageLabel.setText(R.string.label_image_downloading)
        // Disable the download button while loading.
        btnDownload.isEnabled = false

        dayForecast.loadImageInto(fromCacheOnly, imageView) { success: Boolean ->
            if (success) {
                btnDownload.visibility = GONE
                imageLabel.visibility = GONE
            } else {
                // Only show "Failed" label if this was a network request.
                if (!fromCacheOnly) imageLabel.setText(R.string.label_image_download_failed)

                btnDownload.isEnabled = true
            }
        }
    }

    companion object {
        private const val EXTRA_DAY_FORECAST =
            "ch.protonmail.android.protonmailtest.EXTRA_DAY_FORECAST"

        fun buildIntent(context: Context, dayForecast: DayForecast) =
            Intent(context, DetailsActivity::class.java)
                .apply { putExtra(EXTRA_DAY_FORECAST, dayForecast) }
    }
}
