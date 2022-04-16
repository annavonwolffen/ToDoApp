package com.annevonwolffen.domain

class TasksInteractorImpl(private val repo: TasksRepository) : TasksInteractor {
    override suspend fun getAllTasks(): Result<List<Task>> = repo.getAllTasks()

    override suspend fun addTask(task: Task): Result<Task> {
        val updateTime = getCurrentTime()
        return repo.addTask(task.copy(createdAt = updateTime, updatedAt = updateTime))
    }

    override suspend fun updateTask(task: Task): Result<Task> =
        repo.updateTask(task.copy(updatedAt = getCurrentTime()))

    override suspend fun deleteTask(task: Task): Result<Task> =
        repo.deleteTask(task.copy(updatedAt = getCurrentTime()))

    override suspend fun synchronizeTasks() {
        repo.synchronizeTasks()
    }

    private fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }
}