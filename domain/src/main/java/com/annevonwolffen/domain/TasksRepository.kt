package com.annevonwolffen.domain

interface TasksRepository {
    suspend fun getAllTasks(): Result<List<Task>>
    suspend fun addTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun deleteTask(task: Task): Result<Task>
    suspend fun synchronizeTasks()
}