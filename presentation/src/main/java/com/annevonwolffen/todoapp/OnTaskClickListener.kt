package com.annevonwolffen.todoapp

import com.annevonwolffen.todoapp.model.TaskPresentationModel

interface OnTaskClickListener {
    fun onClickTask(task: TaskPresentationModel)
}