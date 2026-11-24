package com.hotaro.mizan.data.model

data class ReleaseNote(
    val version: String,
    val title: String? = null,
    val date: String,
    val isLatest: Boolean = false,
    val tags: List<String>,
    val highlights: List<String>,
    val description: String? = null,
    val downloadUrl: String? = null,
    val htmlUrl: String? = null
)
