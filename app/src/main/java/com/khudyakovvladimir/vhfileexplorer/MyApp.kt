package com.khudyakovvladimir.vhfileexplorer

import android.app.Application
import android.content.Context
import com.khudyakovvladimir.vhfileexplorer.dependencyinjection.AppComponent
import com.khudyakovvladimir.vhfileexplorer.dependencyinjection.DaggerAppComponent

class MyApp: Application() {

    lateinit var appComponent: AppComponent
    private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }
}

val Context.appComponent: AppComponent
get() = when(this) {
    is MyApp -> appComponent
    else -> applicationContext.appComponent
}