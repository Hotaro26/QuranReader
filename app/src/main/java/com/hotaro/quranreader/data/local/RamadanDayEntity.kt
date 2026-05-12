package com.hotaro.quranreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hotaro.quranreader.data.model.RamadanDay

@Entity(tableName = "ramadan_tracker")
data class RamadanDayEntity(
    @PrimaryKey
    val day: Int, // 1 to 30
    val fasted: Boolean = false,
    val prayedTaraweeh: Boolean = false,
    val quranRead: Boolean = false
)

fun RamadanDayEntity.toDomain() = RamadanDay(
    day = day,
    fasted = fasted,
    prayedTaraweeh = prayedTaraweeh,
    quranRead = quranRead
)

fun RamadanDay.toEntity() = RamadanDayEntity(
    day = day,
    fasted = fasted,
    prayedTaraweeh = prayedTaraweeh,
    quranRead = quranRead
)
