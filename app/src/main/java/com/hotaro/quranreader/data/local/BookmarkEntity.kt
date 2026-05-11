package com.hotaro.quranreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hotaro.quranreader.data.model.Bookmark

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val text: String,
    val timestamp: Long
)

fun BookmarkEntity.toDomain() = Bookmark(
    id = id,
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    surahName = surahName,
    text = text,
    timestamp = timestamp
)

fun Bookmark.toEntity() = BookmarkEntity(
    id = id,
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    surahName = surahName,
    text = text,
    timestamp = timestamp
)
