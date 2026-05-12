package com.hotaro.quranreader.data.model

data class Todo(
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
