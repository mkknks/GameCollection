package com.gamecollection.data.session

import com.gamecollection.data.entity.GameMasterEntity
import com.gamecollection.data.model.ContinuousScanAlreadyRegisteredItem
import com.gamecollection.data.model.ContinuousScanEntryType
import com.gamecollection.data.model.ContinuousScanListEntry
import com.gamecollection.data.model.ContinuousScanPendingItem
import com.gamecollection.data.model.ContinuousScanReadyItem
import com.gamecollection.data.model.ContinuousScanSession
import com.gamecollection.data.model.ContinuousScanUnregisteredItem
import com.gamecollection.data.model.JanLookupResult
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ContinuousScanSessionHolder {
    private val _session = MutableStateFlow(ContinuousScanSession())
    val session: StateFlow<ContinuousScanSession> = _session.asStateFlow()

    private val processedJans = mutableSetOf<String>()
    private val inFlightJans = mutableSetOf<String>()
    private val lastDetectionMs = mutableMapOf<String, Long>()

    private val _registeredCollectionItemIds = MutableStateFlow<List<Long>>(emptyList())
    val registeredCollectionItemIds: StateFlow<List<Long>> = _registeredCollectionItemIds.asStateFlow()

    fun startNewSession() {
        processedJans.clear()
        inFlightJans.clear()
        lastDetectionMs.clear()
        _session.value = ContinuousScanSession()
        _registeredCollectionItemIds.value = emptyList()
    }

    fun clearSnackbar() {
        _session.update { it.copy(snackbarMessage = null) }
    }

    suspend fun handleBarcode(rawCode: String, repository: GameRepository): Boolean {
        val janCode = GameRepository.normalizeJanCode(rawCode) ?: return false
        val now = System.currentTimeMillis()

        if (processedJans.contains(janCode)) {
            _session.update {
                it.copy(
                    lastScannedJan = janCode,
                    snackbarMessage = "スキャン済みです",
                )
            }
            return false
        }

        val lastDetected = lastDetectionMs[janCode]
        if (lastDetected != null && now - lastDetected < DEBOUNCE_MS) {
            return false
        }
        lastDetectionMs[janCode] = now

        if (inFlightJans.contains(janCode)) {
            return false
        }
        inFlightJans.add(janCode)

        return try {
            val result = repository.lookupByJanCode(janCode)
            processedJans.add(janCode)
            applyLookupResult(janCode, result)
            true
        } finally {
            inFlightJans.remove(janCode)
        }
    }

    fun resolvePendingSelection(scanId: String, selected: GameMasterEntity) {
        _session.update { state ->
            val pending = state.pendingConfirmation.find { it.scanId == scanId } ?: return@update state
            val readyItem = ContinuousScanReadyItem(
                scanId = scanId,
                janCode = pending.janCode,
                gameMaster = selected,
            )
            state.copy(
                pendingConfirmation = state.pendingConfirmation.filter { it.scanId != scanId },
                readyToRegister = state.readyToRegister + readyItem,
                scanHistory = state.scanHistory.map { entry ->
                    if (entry.scanId == scanId) {
                        entry.copy(
                            title = selected.title,
                            type = ContinuousScanEntryType.READY,
                            detail = "候補確定",
                        )
                    } else {
                        entry
                    }
                },
            )
        }
    }

    fun removeReadyItems(scanIds: Set<String>) {
        _session.update { state ->
            state.copy(
                readyToRegister = state.readyToRegister.filter { it.scanId !in scanIds },
            )
        }
    }

    fun setRegisteredCollectionItemIds(ids: List<Long>) {
        _registeredCollectionItemIds.value = ids
    }

    private fun applyLookupResult(janCode: String, result: JanLookupResult) {
        _session.update { state ->
            val base = state.copy(lastScannedJan = janCode, snackbarMessage = null)
            when (result) {
                is JanLookupResult.Found -> {
                    val item = ContinuousScanReadyItem(janCode = janCode, gameMaster = result.gameMaster)
                    base.copy(
                        readyToRegister = state.readyToRegister + item,
                        scanHistory = state.scanHistory + entry(
                            item.scanId,
                            janCode,
                            result.gameMaster.title,
                            ContinuousScanEntryType.READY,
                        ),
                    )
                }
                is JanLookupResult.AlreadyRegistered -> {
                    val item = ContinuousScanAlreadyRegisteredItem(
                        janCode = janCode,
                        gameMaster = result.gameMaster,
                    )
                    base.copy(
                        alreadyRegistered = state.alreadyRegistered + item,
                        scanHistory = state.scanHistory + entry(
                            item.scanId,
                            janCode,
                            result.gameMaster.title,
                            ContinuousScanEntryType.ALREADY_REGISTERED,
                            "登録済み",
                        ),
                    )
                }
                is JanLookupResult.NotFound -> {
                    val item = ContinuousScanUnregisteredItem(janCode = janCode)
                    base.copy(
                        unregistered = state.unregistered + item,
                        scanHistory = state.scanHistory + entry(
                            item.scanId,
                            janCode,
                            "未登録マスタ",
                            ContinuousScanEntryType.UNREGISTERED,
                        ),
                    )
                }
                is JanLookupResult.Multiple -> {
                    val item = ContinuousScanPendingItem(janCode = janCode, candidates = result.games)
                    base.copy(
                        pendingConfirmation = state.pendingConfirmation + item,
                        scanHistory = state.scanHistory + entry(
                            item.scanId,
                            janCode,
                            "候補 ${result.games.size}件",
                            ContinuousScanEntryType.PENDING,
                        ),
                    )
                }
            }
        }
    }

    private fun entry(
        scanId: String,
        janCode: String,
        title: String,
        type: ContinuousScanEntryType,
        detail: String? = null,
    ) = ContinuousScanListEntry(
        scanId = scanId,
        janCode = janCode,
        title = title,
        type = type,
        detail = detail,
    )

    companion object {
        private const val DEBOUNCE_MS = 2000L
    }
}
