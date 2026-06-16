package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.CollectionStatus
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameDetailUiState(
    val game: GameWithMaster? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class GameDetailViewModel(
    private val repository: GameRepository,
    private val collectionItemId: Long,
) : ViewModel() {
    private val transientState = MutableStateFlow(TransientDetailState())

    val uiState: StateFlow<GameDetailUiState> = combine(
        repository.observeCollectionItem(collectionItemId),
        transientState,
    ) { game, transient ->
        GameDetailUiState(
            game = game,
            isLoading = game == null && transient.errorMessage == null,
            isSaving = transient.isSaving,
            errorMessage = transient.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GameDetailUiState(),
    )

    fun updateStatus(status: CollectionStatus) {
        val current = uiState.value.game ?: return
        viewModelScope.launch {
            transientState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                repository.updateCollectionItem(
                    current.collectionItem.copy(status = status),
                )
            }.onFailure { error ->
                transientState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "更新に失敗しました",
                    )
                }
            }.onSuccess {
                transientState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun updateNotes(notes: String) {
        val current = uiState.value.game ?: return
        viewModelScope.launch {
            transientState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                repository.updateCollectionItem(
                    current.collectionItem.copy(
                        notes = notes.trim().takeIf { it.isNotEmpty() },
                    ),
                )
            }.onFailure { error ->
                transientState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "更新に失敗しました",
                    )
                }
            }.onSuccess {
                transientState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun updateRating(rating: Int?) {
        val current = uiState.value.game ?: return
        viewModelScope.launch {
            transientState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                repository.updateCollectionItem(
                    current.collectionItem.copy(rating = rating),
                )
            }.onFailure { error ->
                transientState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "更新に失敗しました",
                    )
                }
            }.onSuccess {
                transientState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun deleteGame(onDeleted: () -> Unit) {
        val current = uiState.value.game ?: return
        viewModelScope.launch {
            transientState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                repository.deleteCollectionItem(current.collectionItem)
            }.onFailure { error ->
                transientState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "削除に失敗しました",
                    )
                }
            }.onSuccess {
                transientState.update { it.copy(isSaving = false) }
                onDeleted()
            }
        }
    }

    private data class TransientDetailState(
        val isSaving: Boolean = false,
        val errorMessage: String? = null,
    )

    class Factory(
        private val repository: GameRepository,
        private val collectionItemId: Long,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameDetailViewModel(repository, collectionItemId) as T
        }
    }
}
