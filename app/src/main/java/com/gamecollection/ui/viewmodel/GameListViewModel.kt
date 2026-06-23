package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.repository.GameRepository
import com.gamecollection.ui.model.RatingFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class CollectionListFilter(
    val ownershipStatus: OwnershipStatus? = null,
    val playStatus: PlayStatus? = null,
    val ratingFilter: RatingFilter = RatingFilter.All,
    val platform: String? = null,
)

data class GameListUiState(
    val games: List<GameWithMaster> = emptyList(),
    val availablePlatforms: List<String> = emptyList(),
    val filter: CollectionListFilter = CollectionListFilter(),
    val isLoading: Boolean = true,
)

class GameListViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val filterState = MutableStateFlow(CollectionListFilter())

    val uiState: StateFlow<GameListUiState> = combine(
        repository.observeCollection(),
        filterState,
    ) { allGames, filter ->
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

        GameListUiState(
            games = filtered,
            availablePlatforms = platforms,
            filter = filter,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GameListUiState(),
    )

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

    class Factory(
        private val repository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameListViewModel(repository) as T
        }
    }
}
