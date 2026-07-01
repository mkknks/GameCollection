package com.gamecollection.data.csvimport

import com.gamecollection.data.entity.CollectionItemEntity
import com.gamecollection.export.CsvImportRow

enum class CsvImportConflictPolicy {
    SKIP,
}

sealed class CsvImportRowOutcome {
    data object Imported : CsvImportRowOutcome()
    data object Skipped : CsvImportRowOutcome()
    data class Error(val message: String) : CsvImportRowOutcome()
}

interface CsvImportCollectionHandler {
    suspend fun onExistingCollection(
        gameMasterId: Long,
        row: CsvImportRow,
        existingItem: CollectionItemEntity,
    ): CsvImportRowOutcome
}

class SkipExistingCollectionHandler : CsvImportCollectionHandler {
    override suspend fun onExistingCollection(
        gameMasterId: Long,
        row: CsvImportRow,
        existingItem: CollectionItemEntity,
    ): CsvImportRowOutcome = CsvImportRowOutcome.Skipped
}

object CsvImportHandlerFactory {
    fun create(policy: CsvImportConflictPolicy): CsvImportCollectionHandler = when (policy) {
        CsvImportConflictPolicy.SKIP -> SkipExistingCollectionHandler()
    }
}

data class CsvImportResult(
    val successCount: Int,
    val skippedCount: Int,
    val errorCount: Int,
    val errorMessages: List<String> = emptyList(),
)
