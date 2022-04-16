package com.annevonwolffen.data.remote

import com.annevonwolffen.data.database.TaskDbModel
import com.annevonwolffen.domain.Priority
import com.annevonwolffen.domain.Task
import com.google.gson.annotations.SerializedName
import java.util.Date

class TaskServerModel(
    val id: String,
    val text: String,
    val importance: String,
    val done: Boolean,
    val deadline: Long,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("updated_at") val updatedAt: Long
)

fun TaskServerModel.toDb(): TaskDbModel = TaskDbModel(
    id,
    text,
    if (deadline == 0L) null else Date(deadline * SECONDS_MULTIPLIER),
    done,
    Priority.values().first { it.serverName == importance },
    createdAt,
    updatedAt
)

fun TaskServerModel.toDomain(): Task = Task(
    id,
    text,
    if (deadline == 0L) null else Date(deadline * SECONDS_MULTIPLIER),
    done,
    Priority.values().first { it.serverName == importance },
    createdAt,
    updatedAt
)

const val SECONDS_MULTIPLIER = 1000L
