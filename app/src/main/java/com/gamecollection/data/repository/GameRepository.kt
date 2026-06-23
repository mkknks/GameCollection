package com.gamecollection.data.repository

import com.gamecollection.data.dao.CollectionItemDao
import com.gamecollection.data.dao.GameMasterDao
import com.gamecollection.data.entity.CollectionItemEntity
import com.gamecollection.data.entity.GameMasterEntity
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.model.JanLookupResult
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

    suspend fun lookupByJanCode(rawJanCode: String): JanLookupResult {
        val janCode = normalizeJanCode(rawJanCode)
            ?: return JanLookupResult.NotFound(rawJanCode)
        val masters = gameMasterDao.findByJanCode(janCode)
        return when {
            masters.isEmpty() -> JanLookupResult.NotFound(janCode)
            masters.size == 1 -> {
                val master = masters.first()
                if (collectionItemDao.isInCollection(master.id)) {
                    JanLookupResult.AlreadyRegistered(master)
                } else {
                    JanLookupResult.Found(master)
                }
            }
            else -> JanLookupResult.Multiple(masters)
        }
    }

    suspend fun addManualGame(
        title: String,
        platform: String?,
        publisher: String?,
        releaseYear: Int?,
        janCode: String?,
        ownershipStatus: OwnershipStatus,
        playStatus: PlayStatus,
        notes: String?,
    ): Long {
        val normalizedJan = janCode?.let { normalizeJanCode(it) }
        val gameMasterId = gameMasterDao.insert(
            GameMasterEntity(
                title = title.trim(),
                platform = platform?.trim()?.takeIf { it.isNotEmpty() },
                publisher = publisher?.trim()?.takeIf { it.isNotEmpty() },
                releaseYear = releaseYear,
                janCode = normalizedJan,
                isUserAdded = true,
            ),
        )
        return collectionItemDao.insert(
            CollectionItemEntity(
                gameMasterId = gameMasterId,
                ownershipStatus = ownershipStatus,
                playStatus = playStatus,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                visibility = Visibility.PRIVATE,
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
                visibility = Visibility.PRIVATE,
            ),
        )
    }

    suspend fun addFromMasterByBarcode(gameMasterId: Long): Long {
        return addFromMaster(
            gameMasterId = gameMasterId,
            ownershipStatus = OwnershipStatus.OWNING,
            playStatus = PlayStatus.NOT_PLAYED,
            notes = null,
        )
    }

    suspend fun getRegistrationStatus(gameMasterId: Long): JanLookupResult? {
        val master = gameMasterDao.getById(gameMasterId) ?: return null
        return if (collectionItemDao.isInCollection(gameMasterId)) {
            JanLookupResult.AlreadyRegistered(master)
        } else {
            JanLookupResult.Found(master)
        }
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

    companion object {
        fun normalizeJanCode(raw: String): String? {
            val digits = raw.filter { it.isDigit() }
            return when (digits.length) {
                13 -> digits
                12 -> "0$digits"
                else -> null
            }
        }
    }
}
