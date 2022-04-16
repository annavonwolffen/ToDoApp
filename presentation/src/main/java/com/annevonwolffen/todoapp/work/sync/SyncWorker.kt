package com.annevonwolffen.todoapp.work.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.annevonwolffen.domain.TasksInteractor

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val tasksInteractor: TasksInteractor
) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        tasksInteractor.synchronizeTasks()
        return Result.success()
    }
}
