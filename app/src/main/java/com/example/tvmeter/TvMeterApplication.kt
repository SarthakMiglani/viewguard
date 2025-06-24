package com.example.tvmeter

import android.app.Application
import com.example.tvmeter.di.AppContainer

class TvMeterApplication : Application() {

    val appContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
    }
}