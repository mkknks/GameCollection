package com.gamecollection.data.csvimport

import com.gamecollection.data.dao.CollectionItemDao
import com.gamecollection.data.dao.GameMasterDao
import com.gamecollection.data.entity.CollectionItemEntity
import com.gamecollection.data.entity.GameMasterEntity
import com.gamecollection.export.CsvImportRow
import com.gamecollection.export.CsvImporter
import com.gamecollection.export.CsvLabelMappings

class CsvImportProcessor(
    private val gameMasterDao: GameMasterDao,
    private val collectionItemDao: CollectionItemDao,
    private val collectionHandler: CsvImportCollectionHandler,
) {
    suspend fun importRows(rows: List<CsvImportRow>): CsvImportResult {
        var successCount = 0
        var skippedCount = 0
        var errorCount = 0
        val errorMessages = mutableListOf<String>()

        rows.forEachIndexed { index, row ->
            val rowNumber = index + 2
            when (val outcome = importRow(row)) {
                is CsvImportRowOutcome.Imported -> successCount++
                is CsvImportRowOutcome.Skipped -> skippedCount++
                is CsvImportRowOutcome.Error -> {
                    errorCount++
                    errorMessages.add("${rowNumber}行目: ${outcome.message}")
                }
            }
        }

        return CsvImportResult(
            successCount = successCount,
            skippedCount = skippedCount,
            errorCount = errorCount,
            errorMessages = errorMessages,
        )
    }

    private suspend fun importRow(row: CsvImportRow): CsvImportRowOutcome {
        val ownershipStatus = CsvLabelMappings.ownershipFromLabel(row.ownershipStatusLabel)
            ?: return CsvImportRowOutcome.Error("所持状態が不正です: ${row.ownershipStatusLabel}")
        val playStatus = CsvLabelMappings.playStatusFromLabel(row.playStatusLabel)
            ?: return CsvImportRowOutcome.Error("プレイ状態が不正です: ${row.playStatusLabel}")
        val visibility = CsvLabelMappings.visibilityFromLabel(row.visibilityLabel)
            ?: return CsvImportRowOutcome.Error("公開設定が不正です: ${row.visibilityLabel}")
        val purchaseCondition = CsvLabelMappings.purchaseConditionFromLabel(row.purchaseConditionLabel)
            ?: return CsvImportRowOutcome.Error("購入状態が不正です: ${row.purchaseConditionLabel}")

        if (row.rating != null && row.rating !in 1..10) {
            return CsvImportRowOutcome.Error("評価は1〜10で指定してください")
        }

        return runCatching {
            val gameMaster = resolveOrCreateGameMaster(row)
            if (collectionItemDao.isInCollection(gameMaster.id)) {
                val existing = collectionItemDao.getByGameMasterId(gameMaster.id)
                    ?: return@runCatching CsvImportRowOutcome.Skipped
                return@runCatching collectionHandler.onExistingCollection(
                    gameMasterId = gameMaster.id,
                    row = row,
                    existingItem = existing,
                )
            }

            collectionItemDao.insert(
                CollectionItemEntity(
                    gameMasterId = gameMaster.id,
                    ownershipStatus = ownershipStatus,
                    playStatus = playStatus,
                    rating = row.rating,
                    notes = row.notes,
                    purchasePrice = row.purchasePrice,
                    purchaseStore = row.purchaseStore,
                    purchaseDate = row.purchaseDate,
                    purchaseCondition = purchaseCondition,
                    purchaseMemo = row.purchaseMemo,
                    visibility = visibility,
                    addedAt = CsvImporter.parseAddedAt(row.addedAtLabel) ?: System.currentTimeMillis(),
                ),
            )
            CsvImportRowOutcome.Imported
        }.getOrElse { error ->
            CsvImportRowOutcome.Error(error.message ?: "取り込みに失敗しました")
        }
    }

    private suspend fun resolveOrCreateGameMaster(row: CsvImportRow): GameMasterEntity {
        findExistingGameMaster(row)?.let { return it }

        val id = gameMasterDao.insert(
            GameMasterEntity(
                title = row.title.trim(),
                platform = row.platform?.trim()?.takeIf { it.isNotEmpty() },
                publisher = row.publisher?.trim()?.takeIf { it.isNotEmpty() },
                releaseYear = row.releaseYear,
                janCode = row.janCode,
                isUserAdded = true,
            ),
        )
        return gameMasterDao.getById(id)
            ?: throw IllegalStateException("マスタの作成に失敗しました")
    }

    private suspend fun findExistingGameMaster(row: CsvImportRow): GameMasterEntity? {
        if (!row.janCode.isNullOrBlank()) {
            return gameMasterDao.findByJanCode(row.janCode).firstOrNull()
        }
        return gameMasterDao.findByTitleAndPlatform(
            title = row.title.trim(),
            platform = row.platform?.trim()?.takeIf { it.isNotEmpty() },
        )
    }
}
