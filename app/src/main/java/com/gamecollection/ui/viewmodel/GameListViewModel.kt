package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.Visibility
import com.gamecollection.data.repository.BulkUpdateParams
import com.gamecollection.data.repository.GameRepository
import com.gamecollection.ui.model.RatingFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CollectionListFilter(
    val ownershipStatus: OwnershipStatus? = null,
    val playStatus: PlayStatus? = null,
    val ratingFilter: RatingFilter = RatingFilter.All,
    val platform: String? = null,
)

data class BulkEditForm(
    val ownershipStatus: OwnershipStatus = OwnershipStatus.OWNING,
    val playStatus: PlayStatus = PlayStatus.NOT_PLAYED,
    val visibility: Visibility = Visibility.PRIVATE,
)

data class GameListUiState(
    val games: List<GameWithMaster> = emptyList(),
    val availablePlatforms: List<String> = emptyList(),
    val filter: CollectionListFilter = CollectionListFilter(),
    val isLoading: Boolean = true,
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val showBulkEditDialog: Boolean = false,
    val isBulkUpdating: Boolean = false,
    val bulkUpdateError: String? = null,
)

class GameListViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val filterState = MutableStateFlow(CollectionListFilter())
    private val selectionState = MutableStateFlow(SelectionState())
    private val bulkEditForm = MutableStateFlow(BulkEditForm())

    val uiState: StateFlow<GameListUiState> = combine(
        repository.observeCollection(),
        filterState,
        selectionState,
        bulkEditForm,
    ) { allGames, filter, selection, bulkForm ->
        val platforms = allGames
            .mapNotNull { it.gameMaster.platform?.trim()?.takeIf { platform -> platform.isNotEmpty() } }
            .distinct()
            .sorted()

        val filtered = allGames.filter { game ->
            val item = game.collectionItem
            val master = game.gameMaster

            val ownershipMatch = filter.ownershipStatus == null ||
                item.ownershipStatus == filter.ownershipStatus
            val playMatch = filter.playStatus == null ||
                item.playStatus == filter.playStatus
            val ratingMatch = when (val ratingFilter = filter.ratingFilter) {
                RatingFilter.All -> true
                RatingFilter.Unrated -> item.rating == null
                is RatingFilter.Exact -> item.rating == ratingFilter.value
            }
            val platformMatch = filter.platform == null ||
                master.platform == filter.platform

            ownershipMatch && playMatch && ratingMatch && platformMatch
        }

        val visibleIds = filtered.map { it.collectionItem.id }.toSet()
        val selectedIds = selection.selectedIds.intersect(visibleIds)

        GameListUiState(
            games = filtered,
            availablePlatforms = platforms,
            filter = filter,
            isLoading = false,
            isSelectionMode = selection.isSelectionMode,
            selectedIds = selectedIds,
            showBulkEditDialog = selection.showBulkEditDialog,
            isBulkUpdating = selection.isBulkUpdating,
            bulkUpdateError = selection.bulkUpdateError,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GameListUiState(),
    )

    val bulkEditFormState: StateFlow<BulkEditForm> = bulkEditForm.asStateFlow()

    fun setOwnershipFilter(value: OwnershipStatus?) {
        filterState.update { it.copy(ownershipStatus = value) }
    }

    fun setPlayStatusFilter(value: PlayStatus?) {
        filterState.update { it.copy(playStatus = value) }
    }

    fun setRatingFilter(value: RatingFilter) {
        filterState.update { it.copy(ratingFilter = value) }
    }

    fun setPlatformFilter(value: String?) {
        filterState.update { it.copy(platform = value) }
    }

    fun toggleSelectionMode() {
        selectionState.update { state ->
            if (state.isSelectionMode) {
                SelectionState()
            } else {
                state.copy(isSelectionMode = true, selectedIds = emptySet())
            }
        }
    }

    fun toggleSelection(collectionItemId: Long) {
        selectionState.update { state ->
            val updated = state.selectedIds.toMutableSet()
            if (updated.contains(collectionItemId)) {
                updated.remove(collectionItemId)
            } else {
                updated.add(collectionItemId)
            }
            state.copy(selectedIds = updated)
        }
    }

    fun clearSelection() {
        selectionState.update { it.copy(selectedIds = emptySet()) }
    }

    fun showBulkEditDialog() {
        selectionState.update { it.copy(showBulkEditDialog = true, bulkUpdateError = null) }
    }

    fun dismissBulkEditDialog() {
        selectionState.update { it.copy(showBulkEditDialog = false, bulkUpdateError = null) }
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
        val selectedIds = uiState.value.selectedIds
        if (selectedIds.isEmpty()) return

        val form = bulkEditForm.value
        viewModelScope.launch {
            selectionState.update {
                it.copy(isBulkUpdating = true, bulkUpdateError = null)
            }
            runCatching {
                repository.bulkUpdateCollectionItems(
                    collectionItemIds = selectedIds,
                    params = BulkUpdateParams(
                        ownershipStatus = form.ownershipStatus,
                        playStatus = form.playStatus,
                        visibility = form.visibility,
                    ),
                )
            }.onFailure { error ->
                selectionState.update {
                    it.copy(
                        isBulkUpdating = false,
                        bulkUpdateError = error.message ?: "一括更新に失敗しました",
                    )
                }
            }.onSuccess {
                selectionState.value = SelectionState()
                bulkEditForm.value = BulkEditForm()
            }
        }
    }

    private data class SelectionState(
        val isSelectionMode: Boolean = false,
        val selectedIds: Set<Long> = emptySet(),
        val showBulkEditDialog: Boolean = false,
        val isBulkUpdating: Boolean = false,
        val bulkUpdateError: String? = null,
    )

    class Factory(
        private val repository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameListViewModel(repository) as T
        }
    }
}
