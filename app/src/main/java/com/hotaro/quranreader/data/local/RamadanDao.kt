package com.hotaro.quranreader.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RamadanDao {
    @Query("SELECT * FROM ramadan_tracker ORDER BY day ASC")
    fun getAllDays(): Flow<List<RamadanDayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: RamadanDayEntity)

    @Query("SELECT * FROM ramadan_tracker WHERE day = :day")
    suspend fun getDay(day: Int): RamadanDayEntity?
    
    @Query("UPDATE ramadan_tracker SET fasted = :fasted WHERE day = :day")
    suspend fun updateFasted(day: Int, fasted: Boolean)

    @Query("UPDATE ramadan_tracker SET prayedTaraweeh = :prayed WHERE day = :day")
    suspend fun updateTaraweeh(day: Int, prayed: Boolean)

    @Query("UPDATE ramadan_tracker SET quranRead = :read WHERE day = :day")
    suspend fun updateQuranRead(day: Int, read: Boolean)
}
