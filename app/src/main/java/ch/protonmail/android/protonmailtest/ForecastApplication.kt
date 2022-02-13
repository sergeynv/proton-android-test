package ch.protonmail.android.protonmailtest

import android.app.Application

class ForecastApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ForecastApplication
            private set
    }
}