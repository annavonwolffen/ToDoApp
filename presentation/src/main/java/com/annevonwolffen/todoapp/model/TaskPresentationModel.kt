package com.annevonwolffen.todoapp.model

import android.os.Parcelable
import com.annevonwolffen.domain.Priority
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class TaskPresentationModel(
    val id: String? = null,
    val title: String,
    val deadline: Date? = null,
    val isDone: Boolean = false,
    val priority: Priority = Priority.UNDEFINED,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) : Parcelable