package com.hotaro.quranreader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkEntity::class, TodoEntity::class, RamadanDayEntity::class],
    version = 2,
    exportSchema = false
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun todoDao(): TodoDao
    abstract fun ramadanDao(): RamadanDao
}
