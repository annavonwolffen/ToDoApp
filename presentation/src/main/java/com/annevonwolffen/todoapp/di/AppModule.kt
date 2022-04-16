package com.annevonwolffen.todoapp.di

import android.content.Context
import androidx.room.Room
import androidx.work.Configuration
import com.annevonwolffen.data.database.TasksDatabase
import com.annevonwolffen.todoapp.utils.CoroutineDispatchers
import com.annevonwolffen.todoapp.utils.CoroutineDispatchersImpl
import com.annevonwolffen.todoapp.work.TasksDelegatingWorkerFactory
import com.annevonwolffen.todoapp.work.notification.NotificationWorkManager
import com.annevonwolffen.todoapp.work.sync.SyncWorkManager
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface AppModule {

    @AppScope
    @Binds
    fun bindCoroutinesDispatcher(coroutineDispatchers: CoroutineDispatchersImpl): CoroutineDispatchers

    companion object {
        @AppScope
        @Provides
        fun provideNotificationHelper(context: Context): NotificationWorkManager {
            return NotificationWorkManager(context)
        }

        @AppScope
        @Provides
        fun provideSyncWorkManager(context: Context): SyncWorkManager {
            return SyncWorkManager(context)
        }

        @AppScope
        @Provides
        fun provideWorkManagerConfiguration(
            workerFactory: TasksDelegatingWorkerFactory
        ): Configuration {
            return Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .setWorkerFactory(workerFactory)
                .build()
        }

        @AppScope
        @Provides
        fun provideDatabase(context: Context): TasksDatabase {
            return Room.databaseBuilder(
                context,
                TasksDatabase::class.java,
                "Tasks.db"
            ).build()
        }
    }
}