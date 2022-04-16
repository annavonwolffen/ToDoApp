package com.annevonwolffen.todoapp.model

import com.annevonwolffen.domain.Task

fun TaskPresentationModel.mapToDomain(): Task = Task(id ?: "", title, deadline, isDone, priority, createdAt, updatedAt)
fun Task.mapFromDomain(): TaskPresentationModel = TaskPresentationModel(id, title, deadline, isDone, priority, createdAt, updatedAt)