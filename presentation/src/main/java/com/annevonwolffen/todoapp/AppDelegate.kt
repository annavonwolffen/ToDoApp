package com.annevonwolffen.todoapp

import android.app.Application
import androidx.work.Configuration
import com.annevonwolffen.todoapp.di.AppComponent
import com.annevonwolffen.todoapp.di.DaggerAppComponent
import javax.inject.Inject

class AppDelegate : Application(), Configuration.Provider {

    @Inject
    lateinit var workerConfiguration: Configuration

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)
        appComponent.inject(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return workerConfiguration
    }
}