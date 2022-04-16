package com.annevonwolffen.todoapp

import com.annevonwolffen.todoapp.model.TaskPresentationModel

interface TaskItemActionListener {
    fun onDoneTask(task: TaskPresentationModel)
    fun onDeleteTask(task: TaskPresentationModel)
}