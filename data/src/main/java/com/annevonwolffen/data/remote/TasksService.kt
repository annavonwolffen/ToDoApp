package com.annevonwolffen.data.remote

import retrofit2.Response
import retrofit2.http.*

interface TasksService {
    @GET("/tasks/")
    suspend fun getAllTasks(): Response<List<TaskServerModel>>

    @POST("/tasks/")
    suspend fun addTask(@Body task: TaskServerModel): Response<TaskServerModel>

    @PUT("/tasks/{task_id}")
    suspend fun updateTask(
        @Path("task_id") id: String,
        @Body task: TaskServerModel
    ): Response<TaskServerModel>

    @DELETE("/tasks/{task_id}")
    suspend fun deleteTask(@Path("task_id") id: String): Response<TaskServerModel>

    @PUT("/tasks/")
    suspend fun synchronizeTasks(@Body synchronizeRequest: SynchronizeRequest): Response<List<TaskServerModel>>
}