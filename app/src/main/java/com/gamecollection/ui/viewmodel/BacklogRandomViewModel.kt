package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class BacklogRandomUiState(
    val backlogGames: List<GameWithMaster> = emptyList(),
    val pickedGame: GameWithMaster? = null,
    val isLoading: Boolean = true,
    val hasPicked: Boolean = false,
)

class BacklogRandomViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val pickState = MutableStateFlow(PickState())

    val uiState: StateFlow<BacklogRandomUiState> = combine(
        repository.observeBacklog(),
        pickState,
    ) { games, pick ->
        BacklogRandomUiState(
            backlogGames = games,
            pickedGame = pick.pickedGame,
            isLoading = false,
            hasPicked = pick.hasPicked,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BacklogRandomUiState(),
    )

    fun pickRandom() {
        val games = uiState.value.backlogGames
        if (games.isEmpty()) {
            pickState.update { PickState(hasPicked = true, pickedGame = null) }
            return
        }
        pickState.update {
            PickState(
                hasPicked = true,
                pickedGame = games.random(),
            )
        }
    }

    fun resetPick() {
        pickState.value = PickState()
    }

    private data class PickState(
        val pickedGame: GameWithMaster? = null,
        val hasPicked: Boolean = false,
    )

    class Factory(
        private val repository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BacklogRandomViewModel(repository) as T
        }
    }
}
