package com.gamecollection.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gamecollection.data.model.CollectionStatus

@Entity(
    tableName = "collection_item",
    foreignKeys = [
        ForeignKey(
            entity = GameMasterEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameMasterId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["gameMasterId"], unique = true)],
)
data class CollectionItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameMasterId: Long,
    val status: CollectionStatus = CollectionStatus.OWNED,
    val rating: Int? = null,
    val notes: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
)
