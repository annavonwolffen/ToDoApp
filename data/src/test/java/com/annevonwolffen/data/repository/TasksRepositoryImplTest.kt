package com.annevonwolffen.data.repository

import android.util.Log
import com.annevonwolffen.data.database.TaskDbModel
import com.annevonwolffen.data.database.TasksDao
import com.annevonwolffen.data.remote.TaskServerModel
import com.annevonwolffen.data.remote.TasksService
import com.annevonwolffen.data.remote.toDb
import com.annevonwolffen.data.toDb
import com.annevonwolffen.data.toServer
import com.annevonwolffen.domain.Priority
import com.annevonwolffen.domain.Result
import com.annevonwolffen.domain.Task
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class TasksRepositoryImplTest {

    private val tasksService: TasksService = mockk()
    private val tasksDao: TasksDao = mockk(relaxed = true)
    private val task: Task = mockk(relaxed = true) {
        every { id } returns ID_DEFAULT
        every { title } returns "пересмотреть лекцию по корутинам"
        every { deadline } returns null
        every { isDone } returns false
        every { priority } returns Priority.UNDEFINED
        every { createdAt } returns CREATE_TIME_DEFAULT
        every { updatedAt } returns CREATE_TIME_DEFAULT
    }
    private val taskDb = TaskDbModel(
        ID_DEFAULT,
        "пересмотреть лекцию по корутинам",
        null,
        false,
        Priority.UNDEFINED,
        CREATE_TIME_DEFAULT,
        CREATE_TIME_DEFAULT
    )

    private val taskServer = TaskServerModel(
        ID_DEFAULT,
        "пересмотреть лекцию по корутинам",
        "basic",
        false,
        0,
        CREATE_TIME_DEFAULT,
        CREATE_TIME_DEFAULT
    )

    private val repo = TasksRepositoryImpl(tasksDao, tasksService)


    @Before
    fun setUp() {
        mockkStatic("com.annevonwolffen.data.ModelMappersKt")
        every { task.toServer() } returns taskServer
        every { task.toDb() } returns taskDb
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkStatic("com.annevonwolffen.data.ModelMappersKt")
    }

    @Test
    fun testGetAllTasks_whenDBNotEmpty() {
        coEvery { tasksDao.selectAll() } returns listOf(taskDb, taskDb)
        val result = runBlocking { repo.getAllTasks() }
        coVerify { tasksService wasNot called }
        assertThat(result).isEqualTo(Result.Success(listOf(task, task)))
    }

    @Test
    fun testGetAllTasks_whenDBEmpty() {
        coEvery { tasksDao.selectAll() } returns emptyList()
        coEvery { tasksService.getAllTasks() } returns Response.success(listOf(taskServer))
        coEvery { tasksDao.selectDirty() } returns emptyList()
        runBlocking { repo.getAllTasks() }
        coVerifySequence {
            tasksDao.selectAll()
            tasksService.getAllTasks()
            tasksDao.selectDirty()
            tasksDao.selectAll()
            tasksDao.insert(listOf(taskServer.toDb()))
            tasksDao.updateDeleted(listOf(taskServer.id))
            tasksDao.selectAll()
        }
    }

    @Test
    fun testAddTask() {
        coEvery { tasksService.addTask(taskServer) } returns Response.success(taskServer)

        val response = runBlocking { repo.addTask(task) }

        coVerify { tasksDao.insert(taskDb) }
        assertThat(response).isEqualTo(Result.Success(task))
    }

    @Test
    fun testAddTask_whenServerError() {
        coEvery { tasksService.addTask(taskServer) } returns Response.error(
            500,
            mockk(relaxed = true)
        )

        val response = runBlocking { repo.addTask(task) }

        coVerify { tasksDao.insert(taskDb.copy(isDirty = 1)) }
        assertThat(response).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun testUpdateTask() {
        coEvery { tasksService.updateTask(taskServer.id, taskServer) } returns Response.success(
            taskServer
        )

        val response = runBlocking { repo.updateTask(task) }

        coVerify { tasksDao.insert(taskDb) }
        assertThat(response).isEqualTo(Result.Success(task))
    }

    @Test
    fun testUpdateTask_whenServerError() {
        coEvery { tasksService.updateTask(taskServer.id, taskServer) } returns Response.error(
            500,
            mockk(relaxed = true)
        )

        val response = runBlocking { repo.updateTask(task) }

        coVerify { tasksDao.insert(taskDb.copy(isDirty = 1)) }
        assertThat(response).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun testDeleteTask() {
        coEvery { tasksService.deleteTask(taskServer.id) } returns Response.success(taskServer)

        val response = runBlocking { repo.deleteTask(task) }

        coVerify { tasksDao.insert(taskDb.copy(isDeleted = 1)) }
        assertThat(response).isEqualTo(Result.Success(task))
    }

    @Test
    fun testDeleteTask_whenServerError() {
        coEvery { tasksService.deleteTask(taskServer.id) } returns Response.error(
            500,
            mockk(relaxed = true)
        )

        val response = runBlocking { repo.deleteTask(task) }

        coVerify { tasksDao.insert(taskDb.copy(isDeleted = 1, isDirty = 1)) }
        assertThat(response).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun testSynchronizeTasks_whenNoDirty() {
        coEvery { tasksService.getAllTasks() } returns Response.success(
            listOf(
                taskServer,
                taskServer
            )
        )
        coEvery { tasksDao.selectDirty() } returns emptyList()
        coEvery { tasksDao.selectAll() } returns emptyList()
        runBlocking { repo.synchronizeTasks() }
        coVerify { tasksDao.insert(listOf(taskServer.toDb(), taskServer.toDb())) }
        coVerify { tasksDao.updateDeleted(listOf(taskServer.id, taskServer.id)) }
    }

    @Test
    fun testSynchronizeTasks_whenDirty() {
        coEvery { tasksService.getAllTasks() } returns Response.success(listOf(taskServer))
        coEvery { tasksService.synchronizeTasks(any()) } returns Response.success(listOf(mockk()))
        val taskDb1: TaskDbModel = mockk(relaxed = true)
        every { taskDb1.id } returns ID_ALTER
        val taskDb2: TaskDbModel = mockk(relaxed = true)
        every { taskDb2.id } returns ID_DEFAULT
        every { taskDb2.updatedAt } returns CREATE_TIME_ALTER
        every { taskDb2.isDeleted } returns 1
        coEvery { tasksDao.selectDirty() } returns listOf(taskDb1, taskDb2)
        runBlocking { repo.synchronizeTasks() }
        coVerify {
            tasksService.synchronizeTasks(any())
        }
        coVerify { tasksDao.updateDirty(listOf(ID_ALTER, ID_DEFAULT)) }
    }

    private companion object {
        const val ID_DEFAULT = "f1c35a40-0f5b-4691-af9b-cf81c6317c04"
        const val ID_ALTER = "t12"
        const val CREATE_TIME_DEFAULT = 1626036062L
        const val CREATE_TIME_ALTER = 1636036062L
    }
}