package com.annevonwolffen.todoapp.work.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationWorkManager(private val context: Context) {

    fun scheduleNotificationsForTasks() {
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                NOTIFICATION_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<NotificationWorker>(12, TimeUnit.HOURS)
                    .setInitialDelay(5, TimeUnit.MINUTES)
                    .build()
            )
    }

    companion object {
        const val NOTIFICATION_WORK = "ToDoApp_notification_work"
    }
}