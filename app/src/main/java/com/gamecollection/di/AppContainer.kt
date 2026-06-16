package com.gamecollection.di

import android.content.Context
import com.gamecollection.data.database.GameCollectionDatabase
import com.gamecollection.data.repository.GameRepository

class AppContainer(context: Context) {
    private val database = GameCollectionDatabase.getInstance(context)

    val repository: GameRepository = GameRepository(
        gameMasterDao = database.gameMasterDao(),
        collectionItemDao = database.collectionItemDao(),
    )
}
