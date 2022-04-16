package com.annevonwolffen.data.database

import androidx.room.*

@Dao
interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tasks: List<TaskDbModel>)

    @Query("UPDATE TASKS SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM TASKS WHERE is_deleted = 0")
    suspend fun selectAll(): List<TaskDbModel>

    @Query("UPDATE TASKS SET is_deleted = 1 WHERE id NOT IN (:ids) AND is_dirty = 0")
    suspend fun updateDeleted(ids: List<String>)

    @Query("SELECT * FROM TASKS WHERE is_dirty = 1")
    suspend fun selectDirty(): List<TaskDbModel>

    @Query("UPDATE TASKS SET is_dirty = 0 where id IN (:ids)")
    suspend fun updateDirty(ids: List<String>)
}