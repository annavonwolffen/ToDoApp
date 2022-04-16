package com.annevonwolffen.data.repository

import android.util.Log
import com.annevonwolffen.data.database.TasksDao
import com.annevonwolffen.data.remote.SynchronizeRequest
import com.annevonwolffen.data.remote.TasksService
import com.annevonwolffen.data.remote.toDb
import com.annevonwolffen.data.remote.toDomain
import com.annevonwolffen.data.toDb
import com.annevonwolffen.data.toDomain
import com.annevonwolffen.data.toServer
import com.annevonwolffen.domain.Result
import com.annevonwolffen.domain.Task
import com.annevonwolffen.domain.TasksRepository
import retrofit2.Response

class TasksRepositoryImpl(
    private val tasksDao: TasksDao,
    private val tasksService: TasksService
) : TasksRepository {
    override suspend fun getAllTasks(): Result<List<Task>> {
        if (tasksDao.selectAll().isEmpty()) {
            synchronize()
        }
        return Result.Success(tasksDao.selectAll().map { it.toDomain() })
    }

    override suspend fun addTask(task: Task): Result<Task> {
        return handleRequest(
            { tasksService.addTask(task.toServer()) },
            {
                tasksDao.insert(it.toDb())
                it.toDomain()
            },
            { tasksDao.insert(task.toDb().copy(isDirty = 1)) }
        )
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return handleRequest(
            { tasksService.updateTask(task.id, task.toServer()) },
            {
                tasksDao.insert(it.toDb())
                it.toDomain()
            },
            { tasksDao.insert(task.toDb().copy(isDirty = 1)) })
    }

    override suspend fun deleteTask(task: Task): Result<Task> {
        return handleRequest(
            { tasksService.deleteTask(task.id) },
            {
                tasksDao.insert(it.toDb().copy(isDeleted = 1))
                it.toDomain()
            },
            { tasksDao.insert(task.toDb().copy(isDeleted = 1, isDirty = 1)) })
    }

    override suspend fun synchronizeTasks() {
        synchronize()
    }

    /**
     * Синхронизировать данные в базе и на сервере
     */
    private suspend fun synchronize() {
        updateServerData()
    }

    /**
     * Обновить данные на сервере (переотправить таски из базы, по которым запрос на сервер ранее сфейлился),
     * после чего обновить данные в базе.
     */
    private suspend fun updateServerData() {
        handleRequest(
            request = { tasksService.getAllTasks() },
            onSuccess = { serverData ->
                tasksDao.selectDirty().filter { dbTask ->
                    // берем dirty данные в базе, которых нет на сервере или у которых дата редактирования больше соответствующей на сервере
                    dbTask.id !in serverData.map { it.id } || dbTask.updatedAt > serverData.find { it.id == dbTask.id }?.updatedAt ?: 0
                }.groupBy { it.isDeleted }.let { items ->
                    if (items.isNotEmpty()) {
                        val request = SynchronizeRequest(
                            items[1]?.map { it.id } ?: emptyList(),
                            items[0]?.map { it.toServer() } ?: emptyList()
                        )
                        handleRequest(
                            { tasksService.synchronizeTasks(request) },
                            { tasksDao.updateDirty(items.values.flatten().map { it.id }) }
                        )
                    }
                }
                val dbData = tasksDao.selectAll()
                tasksDao.insert(serverData.filter { serverTask ->
                    serverTask.id !in dbData.map { it.id }
                            || serverTask.updatedAt > dbData.find { it.id == serverTask.id }?.updatedAt ?: 0
                }.map { it.toDb() })
                // Обновляем удаленные на сервере, но не удаленные в базе
                tasksDao.updateDeleted(serverData.map { it.id })
            } // onSuccess
        )
    }

    private suspend fun <T, R> handleRequest(
        request: suspend () -> Response<T>,
        onSuccess: suspend (T) -> R,
        onError: suspend () -> Unit = {}
    ): Result<R> {
        return try {
            val response = request.invoke()
            if (response.isSuccessful) {
                response.body()?.let {
                    val result = onSuccess.invoke(it)
                    Result.Success(result)
                } ?: Result.Error("$TAG: request returned empty body")
            } else {
                onError.invoke()
                Log.e(TAG, logErrorMessage("${response.code()} ${response.message()}"))
                Result.Error(logErrorMessage("${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            onError.invoke()
            Log.e(TAG, e.localizedMessage ?: e.toString())
            Result.Error(e.localizedMessage ?: e.toString())
        }
    }

    private fun logErrorMessage(errorMessage: String): String {
        return "Error occurred: $errorMessage"
    }

    private companion object {
        const val TAG = "TasksRepository"
    }
}