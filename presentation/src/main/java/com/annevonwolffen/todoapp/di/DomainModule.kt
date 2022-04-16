package com.annevonwolffen.todoapp.di

import android.content.Context
import com.annevonwolffen.data.SharedPrefSettingsStorage
import com.annevonwolffen.data.database.TasksDatabase
import com.annevonwolffen.data.remote.TasksService
import com.annevonwolffen.data.repository.TasksRepositoryImpl
import com.annevonwolffen.domain.TasksInteractor
import com.annevonwolffen.domain.TasksInteractorImpl
import com.annevonwolffen.domain.settings.SettingsInteractor
import com.annevonwolffen.domain.settings.SettingsInteractorImpl
import dagger.Module
import dagger.Provides

@Module
object DomainModule {

    @AppScope
    @Provides
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(SharedPrefSettingsStorage(context))
    }

    @AppScope
    @Provides
    fun provideTasksInteractor(
        tasksDatabase: TasksDatabase,
        tasksService: TasksService
    ): TasksInteractor {
        val repo = TasksRepositoryImpl(tasksDatabase.tasksDao(), tasksService)
        return TasksInteractorImpl(repo)
    }

}