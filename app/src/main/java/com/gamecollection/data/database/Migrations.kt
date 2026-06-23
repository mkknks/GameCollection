package com.gamecollection.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS collection_item_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                gameMasterId INTEGER NOT NULL,
                ownershipStatus TEXT NOT NULL,
                playStatus TEXT NOT NULL,
                rating INTEGER,
                notes TEXT,
                purchasePrice INTEGER,
                purchaseStore TEXT,
                purchaseDate TEXT,
                purchaseCondition TEXT NOT NULL,
                purchaseMemo TEXT,
                visibility TEXT NOT NULL,
                addedAt INTEGER NOT NULL,
                FOREIGN KEY(gameMasterId) REFERENCES game_master(id) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO collection_item_new (
                id, gameMasterId, ownershipStatus, playStatus, rating, notes,
                purchasePrice, purchaseStore, purchaseDate, purchaseCondition,
                purchaseMemo, visibility, addedAt
            )
            SELECT
                id,
                gameMasterId,
                CASE status
                    WHEN 'WISHLIST' THEN 'WISHLIST'
                    ELSE 'OWNING'
                END,
                CASE status
                    WHEN 'PLAYING' THEN 'PLAYING'
                    WHEN 'COMPLETED' THEN 'COMPLETED'
                    ELSE 'NOT_PLAYED'
                END,
                CASE
                    WHEN rating IS NULL OR rating < 1 THEN NULL
                    WHEN rating > 10 THEN 10
                    ELSE rating
                END,
                notes,
                NULL,
                NULL,
                NULL,
                'UNKNOWN',
                NULL,
                'PRIVATE',
                addedAt
            FROM collection_item
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE collection_item")
        db.execSQL("ALTER TABLE collection_item_new RENAME TO collection_item")
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_collection_item_gameMasterId ON collection_item (gameMasterId)",
        )
    }
}
