package com.annevonwolffen.todoapp

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.annevonwolffen.domain.Priority
import com.annevonwolffen.domain.Result
import com.annevonwolffen.domain.Task
import com.annevonwolffen.domain.TasksInteractor
import com.annevonwolffen.domain.settings.SettingsInteractor
import com.annevonwolffen.todoapp.model.TaskPresentationModel
import com.annevonwolffen.todoapp.model.mapFromDomain
import com.annevonwolffen.todoapp.model.mapToDomain
import com.annevonwolffen.todoapp.utils.CoroutineDispatchers
import com.annevonwolffen.todoapp.utils.MainCoroutineScopeRule
import io.mockk.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * тесты для [TasksViewModel]
 */
class TasksViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    private val settingsInteractor: SettingsInteractor = mockk(relaxed = true)
    private val tasksInteractor: TasksInteractor = mockk()
    private val coroutineDispatchers: CoroutineDispatchers = mockk {
        every { ioDispatcher } returns TestCoroutineDispatcher()
    }
    private val tasksObserver: Observer<List<TaskPresentationModel>> = mockk(relaxed = true)
    private val isDoneTasksShownObserver: Observer<Boolean> = mockk(relaxed = true)
    private val doneTasksCountObserver: Observer<Int> = mockk(relaxed = true)
    private val isLoadingCountObserver: Observer<Boolean> = mockk(relaxed = true)
    private lateinit var viewModel: TasksViewModel

    @Before
    fun setUp() {
        viewModel = TasksViewModel(settingsInteractor, tasksInteractor, coroutineDispatchers)
        viewModel.tasks.observeForever(tasksObserver)
        viewModel.isDoneTasksShown.observeForever(isDoneTasksShownObserver)
        viewModel.doneTasksCount.observeForever(doneTasksCountObserver)
        viewModel.isLoading.observeForever(isLoadingCountObserver)
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        mockkStatic("com.annevonwolffen.todoapp.model.MapperKt")
    }

    @After
    fun tearDown() {
        viewModel.tasks.removeObserver(tasksObserver)
        viewModel.isDoneTasksShown.removeObserver(isDoneTasksShownObserver)
        viewModel.doneTasksCount.removeObserver(doneTasksCountObserver)
        viewModel.isLoading.removeObserver(isLoadingCountObserver)
        unmockkStatic(Log::class)
        unmockkStatic("com.annevonwolffen.todoapp.model.MapperKt")
    }

    @Test
    fun testLoadTasksError() {
        coEvery { tasksInteractor.getAllTasks() } returns Result.Error("ERROR")
        viewModel.loadTasks()
        verify { isLoadingCountObserver.onChanged(true) }
        verify { isLoadingCountObserver.onChanged(false) }
        verify { Log.e(any(), "Exception occurred: ERROR") }
    }

    @Test
    fun testLoadTasks() {
        val taskPresentationModel1: TaskPresentationModel = mockk(relaxed = true) {
            every { deadline } returns Date(1626036062L)
        }
        val task1: Task = mockk(relaxed = true) {
            every { mapFromDomain() } returns taskPresentationModel1
        }
        val taskPresentationModel2: TaskPresentationModel = mockk(relaxed = true) {
            every { deadline } returns Date(1636036062L)
        }
        val task2: Task = mockk(relaxed = true) {
            every { mapFromDomain() } returns taskPresentationModel2
        }
        val taskPresentationModel3: TaskPresentationModel = mockk(relaxed = true) {
            every { deadline } returns Date(1636036062L)
            every { priority } returns Priority.HIGH
        }
        val task3: Task = mockk(relaxed = true) {
            every { mapFromDomain() } returns taskPresentationModel3
        }
        coEvery { tasksInteractor.getAllTasks() } returns Result.Success(
            listOf(
                task1,
                task2,
                task3
            )
        )
        viewModel.loadTasks()
        verify { isLoadingCountObserver.onChanged(true) }
        coVerify { tasksInteractor.getAllTasks() }
        verify { isLoadingCountObserver.onChanged(false) }
        verify {
            tasksObserver.onChanged(
                listOf(
                    taskPresentationModel1,
                    taskPresentationModel3,
                    taskPresentationModel2
                )
            )
        }
    }

    @Test
    fun testLoadTasks_whenDoneTasksOff() {
        every { settingsInteractor.doneTasksVisibility() } returns false
        val taskPresentationModel1: TaskPresentationModel = mockk(relaxed = true) {
            every { isDone } returns true
        }
        val task1: Task = mockk(relaxed = true) {
            every { mapFromDomain() } returns taskPresentationModel1
        }
        coEvery { tasksInteractor.getAllTasks() } returns Result.Success(listOf(task1))

        viewModel.loadTasks()
        verify { isLoadingCountObserver.onChanged(true) }
        coVerify { tasksInteractor.getAllTasks() }
        verify { isLoadingCountObserver.onChanged(false) }
        verify { tasksObserver.onChanged(emptyList()) }
    }

    @Test
    fun testAddTask() {
        val taskDomain: Task = mockk(relaxed = true)
        val taskPresentationModel: TaskPresentationModel = mockk(relaxed = true) {
            every { id } returns null
            every { mapToDomain() } returns taskDomain
            every { copy(any()) } returns this
        }
        every { taskDomain.mapFromDomain() } returns taskPresentationModel
        coEvery { tasksInteractor.addTask(taskDomain) } returns Result.Success(taskDomain)
        viewModel.saveTask(taskPresentationModel)
        verify { tasksObserver.onChanged(listOf(taskPresentationModel)) }
        coVerify { tasksInteractor.addTask(taskDomain) }
        verify { tasksObserver.onChanged(listOf(taskPresentationModel)) }
    }

    @Test
    fun testUpdateTask() {
        val taskDomain: Task = mockk(relaxed = true)
        val taskPresentationModel: TaskPresentationModel = mockk(relaxed = true) {
            every { id } returns RANDOM_ID
            every { mapToDomain() } returns taskDomain
            every { copy(any()) } returns this
        }
        every { taskDomain.mapFromDomain() } returns taskPresentationModel
        coEvery { tasksInteractor.updateTask(taskDomain) } returns Result.Success(taskDomain)
        viewModel.saveTask(taskPresentationModel)
        verify { tasksObserver.onChanged(listOf(taskPresentationModel)) }
        coVerify { tasksInteractor.updateTask(taskDomain) }
        verify { tasksObserver.onChanged(listOf(taskPresentationModel)) }
    }

    @Test
    fun testOnDoneTasksToggleClick_whenDoneTasksToggleOn() {
        every { settingsInteractor.doneTasksVisibility() } returns true
        viewModel.onDoneTasksToggleClick()
        verify { settingsInteractor.setDoneTasksVisibility(false) }
        verify { isDoneTasksShownObserver.onChanged(false) }
    }

    @Test
    fun testOnDoneTasksToggleClick_whenDoneTasksToggleOff() {
        every { settingsInteractor.doneTasksVisibility() } returns false
        viewModel.onDoneTasksToggleClick()
        verify { settingsInteractor.setDoneTasksVisibility(true) }
        verify { isDoneTasksShownObserver.onChanged(true) }
    }

    @Test
    fun testOnDoneTask() {
        val task = TaskPresentationModel(RANDOM_ID, "title", null, false, Priority.UNDEFINED, 1, 1)
        coEvery { tasksInteractor.getAllTasks() } returns Result.Success(listOf(task.mapToDomain()))
        coEvery { tasksInteractor.updateTask(any()) } returns Result.Success(task.mapToDomain())
        viewModel.loadTasks()
        viewModel.onDoneTasksToggleClick()
        viewModel.onDoneTask(task)
        verify { tasksObserver.onChanged(listOf(task.copy(isDone = true))) }
        verify { tasksObserver.onChanged(listOf(task)) }

    }

    @Test
    fun testOnDeleteTask() {
        val task = TaskPresentationModel(RANDOM_ID, "title", null, false, Priority.UNDEFINED, 1, 1)
        coEvery { tasksInteractor.getAllTasks() } returns Result.Success(listOf(task.mapToDomain()))
        coEvery { tasksInteractor.deleteTask(any()) } returns Result.Success(task.mapToDomain())
        viewModel.loadTasks()
        viewModel.onDeleteTask(task)
        verify { tasksObserver.onChanged(emptyList()) }
    }

    private companion object {
        const val RANDOM_ID = "t1"
    }
}