package ch.protonmail.android.protonmailtest

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

private val DayForecast.requestCreator
    get() = Picasso.get().load(imageUrl).stableKey("$imageUrl/day-$dayIndex")

fun DayForecast.loadImageInto(imageView: ImageView, callbackAction: (Boolean) -> Unit) {
    requestCreator.into(imageView, PicassoCallback(callbackAction))
}

private class PicassoCallback(val callbackAction: (Boolean) -> Unit) : Callback {
    override fun onSuccess() = callbackAction(true)
    override fun onError(e: Exception?) = callbackAction(false)
}