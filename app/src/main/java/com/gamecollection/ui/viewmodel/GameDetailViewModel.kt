package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.PurchaseCondition
import com.gamecollection.data.model.Visibility
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameDetailUiState(
    val game: GameWithMaster? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccessMessage: String? = null,
)

data class GameDetailForm(
    val ownershipStatus: OwnershipStatus = OwnershipStatus.OWNING,
    val playStatus: PlayStatus = PlayStatus.NOT_PLAYED,
    val rating: Int? = null,
    val notes: String = "",
    val purchasePrice: String = "",
    val purchaseStore: String = "",
    val purchaseDate: String = "",
    val purchaseCondition: PurchaseCondition = PurchaseCondition.UNKNOWN,
    val purchaseMemo: String = "",
    val visibility: Visibility = Visibility.PRIVATE,
)

class GameDetailViewModel(
    private val repository: GameRepository,
    private val collectionItemId: Long,
) : ViewModel() {
    private val transientState = MutableStateFlow(TransientDetailState())
    private val formState = MutableStateFlow(GameDetailForm())

    init {
        viewModelScope.launch {
            repository.observeCollectionItem(collectionItemId).collect { game ->
                if (game != null && transientState.value.loadedItemId != game.collectionItem.id) {
                    formState.value = game.toForm()
                    transientState.update { it.copy(loadedItemId = game.collectionItem.id) }
                }
            }
        }
    }

    val uiState: StateFlow<GameDetailUiState> = combine(
        repository.observeCollectionItem(collectionItemId),
        transientState,
    ) { game, transient ->
        GameDetailUiState(
            game = game,
            isLoading = game == null && transient.errorMessage == null,
            isSaving = transient.isSaving,
            errorMessage = transient.errorMessage,
            saveSuccessMessage = transient.saveSuccessMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GameDetailUiState(),
    )

    val form: StateFlow<GameDetailForm> = formState.asStateFlow()

    fun onOwnershipStatusChange(value: OwnershipStatus) {
        formState.update { it.copy(ownershipStatus = value) }
    }

    fun onPlayStatusChange(value: PlayStatus) {
        formState.update { it.copy(playStatus = value) }
    }

    fun onRatingChange(value: Int?) {
        formState.update { it.copy(rating = value) }
    }

    fun onNotesChange(value: String) {
        formState.update { it.copy(notes = value) }
    }

    fun onPurchasePriceChange(value: String) {
        formState.update { it.copy(purchasePrice = value) }
    }

    fun onPurchaseStoreChange(value: String) {
        formState.update { it.copy(purchaseStore = value) }
    }

    fun onPurchaseDateChange(value: String) {
        formState.update { it.copy(purchaseDate = value) }
    }

    fun onPurchaseConditionChange(value: PurchaseCondition) {
        formState.update { it.copy(purchaseCondition = value) }
    }

    fun onPurchaseMemoChange(value: String) {
        formState.update { it.copy(purchaseMemo = value) }
    }

    fun onVisibilityChange(value: Visibility) {
        formState.update { it.copy(visibility = value) }
    }

    fun saveChanges() {
        val current = uiState.value.game ?: return
        val form = formState.value

        val purchasePrice = form.purchasePrice.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        if (form.purchasePrice.isNotBlank() && purchasePrice == null) {
            transientState.update {
                it.copy(errorMessage = "購入価格は数値で入力してください", saveSuccessMessage = null)
            }
            return
        }

        if (form.rating != null && form.rating !in 1..10) {
            transientState.update {
                it.copy(errorMessage = "評価は1〜10で入力してください", saveSuccessMessage = null)
            }
            return
        }

        viewModelScope.launch {
            transientState.update {
                it.copy(isSaving = true, errorMessage = null, saveSuccessMessage = null)
            }
            runCatching {
                repository.updateCollectionItem(
                    current.collectionItem.copy(
                        ownershipStatus = form.ownershipStatus,
                        playStatus = form.playStatus,
                        rating = form.rating,
                        notes = form.notes.trim().takeIf { it.isNotEmpty() },
                        purchasePrice = purchasePrice,
                        purchaseStore = form.purchaseStore.trim().takeIf { it.isNotEmpty() },
                        purchaseDate = form.purchaseDate.trim().takeIf { it.isNotEmpty() },
                        purchaseCondition = form.purchaseCondition,
                        purchaseMemo = form.purchaseMemo.trim().takeIf { it.isNotEmpty() },
                        visibility = form.visibility,
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
                transientState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccessMessage = "保存しました",
                    )
                }
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

    private fun GameWithMaster.toForm(): GameDetailForm = GameDetailForm(
        ownershipStatus = collectionItem.ownershipStatus,
        playStatus = collectionItem.playStatus,
        rating = collectionItem.rating,
        notes = collectionItem.notes.orEmpty(),
        purchasePrice = collectionItem.purchasePrice?.toString().orEmpty(),
        purchaseStore = collectionItem.purchaseStore.orEmpty(),
        purchaseDate = collectionItem.purchaseDate.orEmpty(),
        purchaseCondition = collectionItem.purchaseCondition,
        purchaseMemo = collectionItem.purchaseMemo.orEmpty(),
        visibility = collectionItem.visibility,
    )

    private data class TransientDetailState(
        val loadedItemId: Long? = null,
        val isSaving: Boolean = false,
        val errorMessage: String? = null,
        val saveSuccessMessage: String? = null,
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
