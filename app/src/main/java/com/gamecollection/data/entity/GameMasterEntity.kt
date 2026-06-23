package com.gamecollection.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_master",
    indices = [Index(value = ["janCode"], unique = true)],
)
data class GameMasterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val platform: String? = null,
    val publisher: String? = null,
    val releaseYear: Int? = null,
    val janCode: String? = null,
    val isUserAdded: Boolean = false,
)
