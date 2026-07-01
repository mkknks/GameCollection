package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.entity.GameMasterEntity
import com.gamecollection.data.model.ContinuousScanPendingItem
import com.gamecollection.data.model.ContinuousScanSession
import com.gamecollection.data.repository.BulkUpdateParams
import com.gamecollection.data.repository.GameRepository
import com.gamecollection.data.session.ContinuousScanSessionHolder
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.Visibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContinuousScanResultUiState(
    val session: ContinuousScanSession = ContinuousScanSession(),
    val selectedScanIds: Set<String> = emptySet(),
    val isRegistering: Boolean = false,
    val registerError: String? = null,
    val registeredCount: Int = 0,
    val registeredCollectionItemIds: List<Long> = emptyList(),
    val showBulkEditDialog: Boolean = false,
    val isBulkUpdating: Boolean = false,
    val bulkUpdateError: String? = null,
    val bulkUpdateSuccess: Boolean = false,
    val candidateDialogItem: ContinuousScanPendingItem? = null,
)

class ContinuousScanResultViewModel(
    private val repository: GameRepository,
    private val sessionHolder: ContinuousScanSessionHolder,
) : ViewModel() {
    private val selectionState = MutableStateFlow<Set<String>>(emptySet())
    private val transientState = MutableStateFlow(TransientState())
    private var selectionInitialized = false

    val uiState: StateFlow<ContinuousScanResultUiState> = combine(
        sessionHolder.session,
        sessionHolder.registeredCollectionItemIds,
        selectionState,
        transientState,
    ) { session, registeredIds, selectedIds, transient ->
        ContinuousScanResultUiState(
            session = session,
            selectedScanIds = selectedIds,
            isRegistering = transient.isRegistering,
            registerError = transient.registerError,
            registeredCount = registeredIds.size,
            registeredCollectionItemIds = registeredIds,
            showBulkEditDialog = transient.showBulkEditDialog,
            isBulkUpdating = transient.isBulkUpdating,
            bulkUpdateError = transient.bulkUpdateError,
            bulkUpdateSuccess = transient.bulkUpdateSuccess,
            candidateDialogItem = transient.candidateDialogItem,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ContinuousScanResultUiState(),
    )

    init {
        val initial = sessionHolder.session.value
        if (initial.readyToRegister.isNotEmpty()) {
            selectionInitialized = true
            selectionState.value = initial.readyToRegister.map { it.scanId }.toSet()
        }
    }

    private val bulkEditForm = MutableStateFlow(BulkEditForm())
    val bulkEditFormState: StateFlow<BulkEditForm> = bulkEditForm.asStateFlow()

    fun toggleSelection(scanId: String) {
        selectionState.update { current ->
            current.toMutableSet().apply {
                if (contains(scanId)) remove(scanId) else add(scanId)
            }
        }
    }

    fun selectAllReady() {
        selectionState.value = uiState.value.session.readyToRegister.map { it.scanId }.toSet()
    }

    fun clearSelection() {
        selectionState.value = emptySet()
    }

    fun showCandidateDialog(item: ContinuousScanPendingItem) {
        transientState.update { it.copy(candidateDialogItem = item) }
    }

    fun dismissCandidateDialog() {
        transientState.update { it.copy(candidateDialogItem = null) }
    }

    fun resolveCandidate(selected: GameMasterEntity) {
        val pending = transientState.value.candidateDialogItem ?: return
        sessionHolder.resolvePendingSelection(pending.scanId, selected)
        selectionState.update { it + pending.scanId }
        transientState.update { it.copy(candidateDialogItem = null) }
    }

    fun registerSelected() {
        val session = uiState.value.session
        val selectedItems = session.readyToRegister.filter { it.scanId in uiState.value.selectedScanIds }
        if (selectedItems.isEmpty()) return

        viewModelScope.launch {
            transientState.update { it.copy(isRegistering = true, registerError = null) }
            runCatching {
                repository.bulkAddFromMasterByBarcode(selectedItems.map { it.gameMaster.id })
            }.onFailure { error ->
                transientState.update {
                    it.copy(
                        isRegistering = false,
                        registerError = error.message ?: "登録に失敗しました",
                    )
                }
            }.onSuccess { result ->
                val allRegistered = sessionHolder.registeredCollectionItemIds.value + result.registeredCollectionItemIds
                sessionHolder.setRegisteredCollectionItemIds(allRegistered)
                sessionHolder.removeReadyItems(selectedItems.map { it.scanId }.toSet())
                selectionState.update { it - selectedItems.map { item -> item.scanId }.toSet() }
                transientState.update {
                    it.copy(
                        isRegistering = false,
                        registerError = if (result.skippedCount > 0) {
                            "${result.skippedCount}件は登録をスキップしました"
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }

    fun showBulkEditDialog() {
        transientState.update {
            it.copy(showBulkEditDialog = true, bulkUpdateError = null, bulkUpdateSuccess = false)
        }
    }

    fun dismissBulkEditDialog() {
        transientState.update { it.copy(showBulkEditDialog = false) }
    }

    fun onBulkOwnershipChange(value: OwnershipStatus) {
        bulkEditForm.update { it.copy(ownershipStatus = value) }
    }

    fun onBulkPlayStatusChange(value: PlayStatus) {
        bulkEditForm.update { it.copy(playStatus = value) }
    }

    fun onBulkVisibilityChange(value: Visibility) {
        bulkEditForm.update { it.copy(visibility = value) }
    }

    fun applyBulkUpdate() {
        val ids = uiState.value.registeredCollectionItemIds.toSet()
        if (ids.isEmpty()) return
        val form = bulkEditForm.value

        viewModelScope.launch {
            transientState.update {
                it.copy(isBulkUpdating = true, bulkUpdateError = null, bulkUpdateSuccess = false)
            }
            runCatching {
                repository.bulkUpdateCollectionItems(
                    collectionItemIds = ids,
                    params = BulkUpdateParams(
                        ownershipStatus = form.ownershipStatus,
                        playStatus = form.playStatus,
                        visibility = form.visibility,
                    ),
                )
            }.onFailure { error ->
                transientState.update {
                    it.copy(
                        isBulkUpdating = false,
                        bulkUpdateError = error.message ?: "一括更新に失敗しました",
                    )
                }
            }.onSuccess {
                transientState.update {
                    it.copy(
                        isBulkUpdating = false,
                        bulkUpdateSuccess = true,
                        showBulkEditDialog = false,
                    )
                }
            }
        }
    }

    private data class TransientState(
        val isRegistering: Boolean = false,
        val registerError: String? = null,
        val showBulkEditDialog: Boolean = false,
        val isBulkUpdating: Boolean = false,
        val bulkUpdateError: String? = null,
        val bulkUpdateSuccess: Boolean = false,
        val candidateDialogItem: ContinuousScanPendingItem? = null,
    )

    class Factory(
        private val repository: GameRepository,
        private val sessionHolder: ContinuousScanSessionHolder,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ContinuousScanResultViewModel(repository, sessionHolder) as T
        }
    }
}
