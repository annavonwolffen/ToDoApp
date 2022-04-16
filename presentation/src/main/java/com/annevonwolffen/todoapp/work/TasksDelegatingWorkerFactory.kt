package com.annevonwolffen.todoapp.work

import androidx.work.DelegatingWorkerFactory
import com.annevonwolffen.domain.TasksInteractor
import com.annevonwolffen.todoapp.di.AppScope
import javax.inject.Inject

@AppScope
class TasksDelegatingWorkerFactory @Inject constructor(
    interactor: TasksInteractor
) : DelegatingWorkerFactory() {
    init {
        addFactory(TasksWorkerFactory(interactor))
    }
}