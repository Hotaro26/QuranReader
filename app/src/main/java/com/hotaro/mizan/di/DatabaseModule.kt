package com.hotaro.mizan.di

import android.content.Context
import androidx.room.Room
import com.hotaro.mizan.data.local.BookmarkDao
import com.hotaro.mizan.data.local.PreferenceManager
import com.hotaro.mizan.data.local.QuranDatabase
import com.hotaro.mizan.data.local.TodoDao
import com.hotaro.mizan.data.local.RamadanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext context: Context): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "quran_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideBookmarkDao(database: QuranDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    fun provideTodoDao(database: QuranDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    fun provideRamadanDao(database: QuranDatabase): RamadanDao {
        return database.ramadanDao()
    }

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }
}
