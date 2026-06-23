package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BacklogListUiState(
    val games: List<GameWithMaster> = emptyList(),
    val isLoading: Boolean = true,
)

class BacklogListViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    val uiState: StateFlow<BacklogListUiState> = repository.observeBacklog()
        .map { games ->
            BacklogListUiState(
                games = games,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BacklogListUiState(),
        )

    class Factory(
        private val repository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BacklogListViewModel(repository) as T
        }
    }
}
