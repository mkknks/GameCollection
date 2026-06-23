package com.gamecollection.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gamecollection.data.entity.GameMasterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameMasterDao {
    @Query("SELECT * FROM game_master ORDER BY title ASC")
    fun observeAll(): Flow<List<GameMasterEntity>>

    @Query("SELECT * FROM game_master WHERE id = :id")
    fun observeById(id: Long): Flow<GameMasterEntity?>

    @Query("SELECT * FROM game_master WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchByTitle(query: String): Flow<List<GameMasterEntity>>

    @Query("SELECT * FROM game_master WHERE janCode = :janCode ORDER BY title ASC")
    suspend fun findByJanCode(janCode: String): List<GameMasterEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(gameMaster: GameMasterEntity): Long

    @Query("SELECT * FROM game_master WHERE id = :id")
    suspend fun getById(id: Long): GameMasterEntity?
}
