package com.gamecollection.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.PurchaseCondition
import com.gamecollection.data.model.Visibility

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
    val ownershipStatus: OwnershipStatus = OwnershipStatus.OWNING,
    val playStatus: PlayStatus = PlayStatus.NOT_PLAYED,
    val rating: Int? = null,
    val notes: String? = null,
    val purchasePrice: Int? = null,
    val purchaseStore: String? = null,
    val purchaseDate: String? = null,
    val purchaseCondition: PurchaseCondition = PurchaseCondition.UNKNOWN,
    val purchaseMemo: String? = null,
    val visibility: Visibility = Visibility.PRIVATE,
    val addedAt: Long = System.currentTimeMillis(),
)
