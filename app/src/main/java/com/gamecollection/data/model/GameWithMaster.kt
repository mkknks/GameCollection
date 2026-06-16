package com.gamecollection.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.gamecollection.data.entity.CollectionItemEntity
import com.gamecollection.data.entity.GameMasterEntity

data class GameWithMaster(
    @Embedded
    val collectionItem: CollectionItemEntity,
    @Relation(
        parentColumn = "gameMasterId",
        entityColumn = "id",
    )
    val gameMaster: GameMasterEntity,
)
