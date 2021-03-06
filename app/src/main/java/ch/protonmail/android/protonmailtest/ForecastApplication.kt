package ch.protonmail.android.protonmailtest

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class ForecastApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        // Would love to disable this, but the mockapi.io we are using here, is just sooo
        // unreliable, so "debug Toasts" come in very handy...
        public var DEBUG_SHOW_TOAST = true
        private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

        lateinit var instance: ForecastApplication
            private set

        /** Show "debug" Toast, if this is allowed. */
        fun maybeShowDebugToast(text: String) {
            if (DEBUG_SHOW_TOAST.not()) return

            val toastText = "[DEBUG] $text"
            // If we are not on the MainThread, we'll need to "post" it there, otherwise - simply
            // run.
            if (Looper.myLooper() == Looper.getMainLooper()) {
                showToast(toastText)
            } else {
                mainHandler.post { showToast(toastText) }
            }
        }

        private fun showToast(text: String) =
            Toast.makeText(instance, text, Toast.LENGTH_SHORT).show()
    }
}