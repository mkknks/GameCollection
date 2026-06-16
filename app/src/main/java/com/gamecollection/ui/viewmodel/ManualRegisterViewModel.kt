package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.CollectionStatus
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ManualRegisterUiState(
    val title: String = "",
    val platform: String = "",
    val publisher: String = "",
    val releaseYear: String = "",
    val status: CollectionStatus = CollectionStatus.OWNED,
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)

class ManualRegisterViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ManualRegisterUiState())
    val uiState: StateFlow<ManualRegisterUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, errorMessage = null) }
    fun onPlatformChange(value: String) = _uiState.update { it.copy(platform = value) }
    fun onPublisherChange(value: String) = _uiState.update { it.copy(publisher = value) }
    fun onReleaseYearChange(value: String) = _uiState.update { it.copy(releaseYear = value) }
    fun onStatusChange(value: CollectionStatus) = _uiState.update { it.copy(status = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "タイトルを入力してください") }
            return
        }

        val releaseYear = state.releaseYear.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        if (state.releaseYear.isNotBlank() && releaseYear == null) {
            _uiState.update { it.copy(errorMessage = "発売年は数値で入力してください") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            runCatching {
                repository.addManualGame(
                    title = state.title,
                    platform = state.platform,
                    publisher = state.publisher,
                    releaseYear = releaseYear,
                    status = state.status,
                    notes = state.notes,
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "登録に失敗しました",
                    )
                }
            }.onSuccess {
                _uiState.update {
                    ManualRegisterUiState(isSuccess = true)
                }
                onSuccess()
            }
        }
    }

    class Factory(
        private val repository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManualRegisterViewModel(repository) as T
        }
    }
}
