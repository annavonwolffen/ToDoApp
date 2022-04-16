package com.annevonwolffen.data

import com.annevonwolffen.data.database.TaskDbModel
import com.annevonwolffen.data.remote.SECONDS_MULTIPLIER
import com.annevonwolffen.data.remote.TaskServerModel
import com.annevonwolffen.domain.Task

fun TaskDbModel.toDomain(): Task =
    Task(id, title, deadline, isDone, priority, createdAt, updatedAt)

fun TaskDbModel.toServer(): TaskServerModel =
    TaskServerModel(
        id,
        title,
        priority.serverName,
        isDone,
        deadline?.time ?: 0,
        createdAt,
        updatedAt
    )

fun Task.toDb(): TaskDbModel =
    TaskDbModel(id, title, deadline, isDone, priority, createdAt, updatedAt)

fun Task.toServer(): TaskServerModel = TaskServerModel(
    id,
    title,
    priority.serverName,
    isDone,
    deadline?.let { it.time / SECONDS_MULTIPLIER } ?: 0,
    createdAt,
    updatedAt
)