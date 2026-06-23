package com.gamecollection.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gamecollection.data.dao.CollectionItemDao
import com.gamecollection.data.dao.GameMasterDao
import com.gamecollection.data.entity.CollectionItemEntity
import com.gamecollection.data.entity.GameMasterEntity

@Database(
    entities = [GameMasterEntity::class, CollectionItemEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class GameCollectionDatabase : RoomDatabase() {
    abstract fun gameMasterDao(): GameMasterDao
    abstract fun collectionItemDao(): CollectionItemDao

    companion object {
        @Volatile
        private var instance: GameCollectionDatabase? = null

        fun getInstance(context: Context): GameCollectionDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context.applicationContext).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): GameCollectionDatabase {
            return Room.databaseBuilder(
                context,
                GameCollectionDatabase::class.java,
                "game_collection.db",
            )
                .addCallback(PrepopulateCallback())
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }

    private class PrepopulateCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val prepopulatedGames = listOf(
                arrayOf<Any?>("The Legend of Zelda: Breath of the Wild", "Nintendo Switch", "Nintendo", 2017),
                arrayOf<Any?>("Elden Ring", "Multi", "FromSoftware", 2022),
                arrayOf<Any?>("Final Fantasy VII", "PlayStation", "Square Enix", 1997),
                arrayOf<Any?>("Super Mario Odyssey", "Nintendo Switch", "Nintendo", 2017),
                arrayOf<Any?>("Persona 5 Royal", "Multi", "Atlus", 2020),
                arrayOf<Any?>("Hollow Knight", "Multi", "Team Cherry", 2017),
                arrayOf<Any?>("Monster Hunter: World", "Multi", "Capcom", 2018),
                arrayOf<Any?>("Stardew Valley", "Multi", "ConcernedApe", 2016),
            )

            prepopulatedGames.forEach { game ->
                val title = game[0] as String
                val platform = game[1] as String
                val publisher = game[2] as String
                val releaseYear = game[3] as Int
                db.execSQL(
                    """
                    INSERT INTO game_master (title, platform, publisher, releaseYear, isUserAdded)
                    VALUES (?, ?, ?, ?, 0)
                    """.trimIndent(),
                    arrayOf(title, platform, publisher, releaseYear as Any),
                )
            }
        }
    }
}
