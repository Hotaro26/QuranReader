package com.hotaro.mizan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hotaro.mizan.data.model.Todo

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

fun TodoEntity.toDomain() = Todo(
    id = id,
    title = title,
    isCompleted = isCompleted,
    timestamp = timestamp
)

fun Todo.toEntity() = TodoEntity(
    id = id,
    title = title,
    isCompleted = isCompleted,
    timestamp = timestamp
)
