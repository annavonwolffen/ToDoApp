package com.annevonwolffen.todoapp.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.annevonwolffen.domain.TasksInteractor
import com.annevonwolffen.todoapp.work.notification.NotificationWorker
import com.annevonwolffen.todoapp.work.sync.SyncWorker

class TasksWorkerFactory(private val tasksInteractor: TasksInteractor) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> SyncWorker(
                appContext,
                workerParameters,
                tasksInteractor
            )
            NotificationWorker::class.java.name -> NotificationWorker(
                appContext,
                workerParameters,
                tasksInteractor
            )
            else -> null
        }
    }
}