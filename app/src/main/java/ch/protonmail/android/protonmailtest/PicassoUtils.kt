package ch.protonmail.android.protonmailtest

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy.OFFLINE
import com.squareup.picasso.Picasso

private val DayForecast.requestCreator
    get() = Picasso.get().load(imageUrl).stableKey("$imageUrl/day-$dayIndex")

fun DayForecast.loadImageInto(imageView: ImageView, callbackAction: (Boolean) -> Unit) {
    requestCreator.into(imageView, object : Callback {
        override fun onSuccess() = callbackAction(true)
        override fun onError(e: Exception?) = callbackAction(false)
    })
}

fun DayForecast.loadImageFromCacheInto(imageView: ImageView, callbackAction: () -> Unit) {
    requestCreator.networkPolicy(OFFLINE).into(imageView, object : Callback {
        override fun onSuccess() = callbackAction()
        override fun onError(e: Exception?) { }
    })
}