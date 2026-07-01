package com.gamecollection.data.model

import com.gamecollection.data.entity.GameMasterEntity
import java.util.UUID

data class ContinuousScanReadyItem(
    val scanId: String = UUID.randomUUID().toString(),
    val janCode: String,
    val gameMaster: GameMasterEntity,
)

data class ContinuousScanPendingItem(
    val scanId: String = UUID.randomUUID().toString(),
    val janCode: String,
    val candidates: List<GameMasterEntity>,
    val selectedGameMaster: GameMasterEntity? = null,
)

data class ContinuousScanUnregisteredItem(
    val scanId: String = UUID.randomUUID().toString(),
    val janCode: String,
)

data class ContinuousScanAlreadyRegisteredItem(
    val scanId: String = UUID.randomUUID().toString(),
    val janCode: String,
    val gameMaster: GameMasterEntity,
)

enum class ContinuousScanEntryType {
    READY,
    PENDING,
    UNREGISTERED,
    ALREADY_REGISTERED,
}

data class ContinuousScanListEntry(
    val scanId: String,
    val janCode: String,
    val title: String,
    val type: ContinuousScanEntryType,
    val detail: String? = null,
)

data class ContinuousScanSession(
    val readyToRegister: List<ContinuousScanReadyItem> = emptyList(),
    val pendingConfirmation: List<ContinuousScanPendingItem> = emptyList(),
    val unregistered: List<ContinuousScanUnregisteredItem> = emptyList(),
    val alreadyRegistered: List<ContinuousScanAlreadyRegisteredItem> = emptyList(),
    val scanHistory: List<ContinuousScanListEntry> = emptyList(),
    val lastScannedJan: String? = null,
    val snackbarMessage: String? = null,
) {
    val readyCount: Int get() = readyToRegister.size
    val pendingCount: Int get() = pendingConfirmation.size
    val unregisteredCount: Int get() = unregistered.size
    val alreadyRegisteredCount: Int get() = alreadyRegistered.size
}

data class BulkRegisterResult(
    val registeredCollectionItemIds: List<Long>,
    val skippedCount: Int,
)
