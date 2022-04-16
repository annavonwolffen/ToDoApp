package com.annevonwolffen.todoapp.work.sync

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class SyncWorkManager(private val context: Context) {
    fun scheduleSync() {
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                SYNC_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<SyncWorker>(8, TimeUnit.HOURS)
                    .setInitialDelay(5, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()
            )
    }

    private companion object {
        const val SYNC_WORK = "ToDoApp_sync_work"
    }
}