package com.gamecollection.di

import android.content.Context
import com.gamecollection.data.database.GameCollectionDatabase
import com.gamecollection.data.repository.GameRepository
import com.gamecollection.data.session.ContinuousScanSessionHolder
import com.gamecollection.export.CsvExporter
import com.gamecollection.export.CsvImporter

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = GameCollectionDatabase.getInstance(appContext)

    val repository: GameRepository = GameRepository(
        gameMasterDao = database.gameMasterDao(),
        collectionItemDao = database.collectionItemDao(),
    )

    val continuousScanSessionHolder: ContinuousScanSessionHolder = ContinuousScanSessionHolder()

    val csvExporter: CsvExporter = CsvExporter(appContext)

    val csvImporter: CsvImporter = CsvImporter(appContext)
}
