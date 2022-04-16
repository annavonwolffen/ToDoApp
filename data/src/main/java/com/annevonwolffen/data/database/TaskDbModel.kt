package com.annevonwolffen.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.annevonwolffen.domain.Priority
import java.util.Date

@Entity(tableName = "TASKS")
data class TaskDbModel(
    @PrimaryKey val id: String,
    val title: String,
    val deadline: Date? = null,
    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,
    val priority: Priority = Priority.UNDEFINED,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0,
    @ColumnInfo(name = "is_dirty")
    val isDirty: Int = 0
)