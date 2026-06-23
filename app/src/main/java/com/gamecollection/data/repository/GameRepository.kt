package com.gamecollection.data.repository

import com.gamecollection.data.dao.CollectionItemDao
import com.gamecollection.data.dao.GameMasterDao
import com.gamecollection.data.entity.CollectionItemEntity
import com.gamecollection.data.entity.GameMasterEntity
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.Visibility
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val gameMasterDao: GameMasterDao,
    private val collectionItemDao: CollectionItemDao,
) {
    fun observeCollection(): Flow<List<GameWithMaster>> =
        collectionItemDao.observeAllWithMaster()

    fun observeBacklog(): Flow<List<GameWithMaster>> =
        collectionItemDao.observeBacklogWithMaster()

    fun observeCollectionItem(collectionItemId: Long): Flow<GameWithMaster?> =
        collectionItemDao.observeWithMasterById(collectionItemId)

    fun searchMasterByTitle(query: String): Flow<List<GameMasterEntity>> =
        gameMasterDao.searchByTitle(query.trim())

    fun observeIsInCollection(gameMasterId: Long): Flow<Boolean> =
        collectionItemDao.observeIsInCollection(gameMasterId)

    suspend fun addManualGame(
        title: String,
        platform: String?,
        publisher: String?,
        releaseYear: Int?,
        ownershipStatus: OwnershipStatus,
        playStatus: PlayStatus,
        notes: String?,
    ): Long {
        val gameMasterId = gameMasterDao.insert(
            GameMasterEntity(
                title = title.trim(),
                platform = platform?.trim()?.takeIf { it.isNotEmpty() },
                publisher = publisher?.trim()?.takeIf { it.isNotEmpty() },
                releaseYear = releaseYear,
                isUserAdded = true,
            ),
        )
        return collectionItemDao.insert(
            CollectionItemEntity(
                gameMasterId = gameMasterId,
                ownershipStatus = ownershipStatus,
                playStatus = playStatus,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
            ),
        )
    }

    suspend fun addFromMaster(
        gameMasterId: Long,
        ownershipStatus: OwnershipStatus,
        playStatus: PlayStatus,
        notes: String?,
    ): Long {
        if (collectionItemDao.isInCollection(gameMasterId)) {
            throw IllegalStateException("このゲームは既にコレクションに登録されています")
        }
        return collectionItemDao.insert(
            CollectionItemEntity(
                gameMasterId = gameMasterId,
                ownershipStatus = ownershipStatus,
                playStatus = playStatus,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
            ),
        )
    }

    suspend fun updateCollectionItem(item: CollectionItemEntity) {
        collectionItemDao.update(item)
    }

    suspend fun bulkUpdateCollectionItems(
        collectionItemIds: Set<Long>,
        params: BulkUpdateParams,
    ) {
        if (collectionItemIds.isEmpty()) return
        val items = collectionItemDao.getByIds(collectionItemIds.toList())
        val updated = items.map { item ->
            item.copy(
                ownershipStatus = params.ownershipStatus,
                playStatus = params.playStatus,
                visibility = params.visibility,
            )
        }
        collectionItemDao.updateAll(updated)
    }

    suspend fun deleteCollectionItem(item: CollectionItemEntity) {
        collectionItemDao.delete(item)
    }
}
