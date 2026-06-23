package com.gamecollection.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gamecollection.data.entity.CollectionItemEntity
import com.gamecollection.data.model.GameWithMaster
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionItemDao {
    @Transaction
    @Query("SELECT * FROM collection_item ORDER BY addedAt DESC")
    fun observeAllWithMaster(): Flow<List<GameWithMaster>>

    @Transaction
    @Query(
        """
        SELECT * FROM collection_item
        WHERE playStatus = 'BACKLOG' AND ownershipStatus = 'OWNING'
        ORDER BY addedAt DESC
        """,
    )
    fun observeBacklogWithMaster(): Flow<List<GameWithMaster>>

    @Transaction
    @Query("SELECT * FROM collection_item WHERE id = :id")
    fun observeWithMasterById(id: Long): Flow<GameWithMaster?>

    @Query("SELECT * FROM collection_item WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<CollectionItemEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM collection_item WHERE gameMasterId = :gameMasterId)")
    fun observeIsInCollection(gameMasterId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM collection_item WHERE gameMasterId = :gameMasterId)")
    suspend fun isInCollection(gameMasterId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: CollectionItemEntity): Long

    @Update
    suspend fun update(item: CollectionItemEntity)

    @Update
    suspend fun updateAll(items: List<CollectionItemEntity>)

    @Delete
    suspend fun delete(item: CollectionItemEntity)

    @Query("SELECT * FROM collection_item WHERE id = :id")
    suspend fun getById(id: Long): CollectionItemEntity?
}
