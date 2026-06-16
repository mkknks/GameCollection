package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.entity.GameMasterEntity
import com.gamecollection.data.model.CollectionStatus
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MasterSearchUiState(
    val query: String = "",
    val results: List<GameMasterEntity> = emptyList(),
    val selectedGameMasterId: Long? = null,
    val status: CollectionStatus = CollectionStatus.OWNED,
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class MasterSearchViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val formState = MutableStateFlow(FormState())

    val uiState: StateFlow<MasterSearchUiState> = combine(
        query,
        formState,
        query.flatMapLatest { searchQuery ->
            if (searchQuery.isBlank()) {
                flowOf(emptyList())
            } else {
                repository.searchMasterByTitle(searchQuery)
            }
        },
    ) { searchQuery, form, results ->
        MasterSearchUiState(
            query = searchQuery,
            results = results,
            selectedGameMasterId = form.selectedGameMasterId,
            status = form.status,
            notes = form.notes,
            isSubmitting = form.isSubmitting,
            errorMessage = form.errorMessage,
            successMessage = form.successMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MasterSearchUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
        formState.update {
            it.copy(
                selectedGameMasterId = null,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun onGameSelected(gameMasterId: Long) {
        formState.update {
            it.copy(
                selectedGameMasterId = gameMasterId,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun onStatusChange(value: CollectionStatus) {
        formState.update { it.copy(status = value) }
    }

    fun onNotesChange(value: String) {
        formState.update { it.copy(notes = value) }
    }

    fun registerSelected(onSuccess: () -> Unit) {
        val form = formState.value
        val selectedId = form.selectedGameMasterId
        if (selectedId == null) {
            formState.update { it.copy(errorMessage = "ゲームを選択してください") }
            return
        }

        viewModelScope.launch {
            formState.update {
                it.copy(isSubmitting = true, errorMessage = null, successMessage = null)
            }
            runCatching {
                repository.addFromMaster(
                    gameMasterId = selectedId,
                    status = form.status,
                    notes = form.notes,
                )
            }.onFailure { error ->
                formState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "登録に失敗しました",
                    )
                }
            }.onSuccess {
                query.value = ""
                formState.value = FormState(successMessage = "コレクションに追加しました")
                onSuccess()
            }
        }
    }

    private data class FormState(
        val selectedGameMasterId: Long? = null,
        val status: CollectionStatus = CollectionStatus.OWNED,
        val notes: String = "",
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null,
    )

    class Factory(
        private val repository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MasterSearchViewModel(repository) as T
        }
    }
}
