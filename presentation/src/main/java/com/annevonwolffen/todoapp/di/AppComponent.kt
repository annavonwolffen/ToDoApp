package com.annevonwolffen.todoapp.di

import android.content.Context
import com.annevonwolffen.todoapp.AppDelegate
import com.annevonwolffen.todoapp.TasksActivity
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [AppModule::class, ViewModelModule::class, DomainModule::class, NetworkModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(appDelegate: AppDelegate)
    fun inject(tasksActivity: TasksActivity)
}