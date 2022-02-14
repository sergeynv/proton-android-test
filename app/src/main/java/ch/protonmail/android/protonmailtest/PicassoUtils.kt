package ch.protonmail.android.protonmailtest

import android.widget.ImageView
import com.squareup.picasso.Picasso

private val DayForecast.requestCreator
    get() = Picasso.get().load(imageUrl).stableKey("$imageUrl/day-$dayIndex")

fun DayForecast.loadImageInto(imageView: ImageView) = requestCreator.into(imageView)