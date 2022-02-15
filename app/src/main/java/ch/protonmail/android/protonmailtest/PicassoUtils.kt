package ch.protonmail.android.protonmailtest

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy.OFFLINE
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator

fun DayForecast.loadImageInto(
    fromCacheOnly: Boolean,
    imageView: ImageView,
    callbackAction: ((Boolean) -> Unit)? = null
) = requestCreator.apply { if (fromCacheOnly) networkPolicy(OFFLINE) }
    .into(imageView, callbackAction)

fun DayForecast.loadImageFromCacheInto(imageView: ImageView) =
    loadImageInto(fromCacheOnly = true, imageView = imageView)

fun cancelLoadImage(imageView: ImageView) = Picasso.get().cancelRequest(imageView)

// All the DayForecast objects returned by REST service (mockapi.io) seem to have image (precisely:
// https://picsum.photos/640/480). While the picsum.photos service responds with a different picture
// to every response, the regular caching - using the URL as the cache key - won't work here: we'll
// load the image for on  the days once, it will get cached and will get re-used for all the other
// days as well. To work around this we'll manually specify a different Picasso's stableKey (
// basically a cache key) for every day, which will be a combination of the url and the day index.
// E.g. "https://picsum.photos/640/480:day-2".
private val DayForecast.requestCreator
    get() = Picasso.get().load(imageUrl).stableKey("$imageUrl:day-$dayIndex")

// Making the Picasso API more Kotlin-friendly (this into() method takes callback in form of a
// lambda, so the callers would be able use "trailing lambda" syntax when invoking the API).
private fun RequestCreator.into(imageView: ImageView, callbackAction: ((Boolean) -> Unit)?) =
    into(imageView, if (callbackAction == null) null else object : Callback {
        override fun onSuccess() = callbackAction(true)
        override fun onError(e: Exception?) = callbackAction(false)
    })