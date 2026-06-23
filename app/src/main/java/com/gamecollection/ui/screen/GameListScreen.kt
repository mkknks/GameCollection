package com.gamecollection.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamecollection.ui.components.CollectionFilterBar
import com.gamecollection.ui.components.GameListItem
import com.gamecollection.ui.viewmodel.GameListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onGameClick: (Long) -> Unit,
    onManualRegisterClick: () -> Unit,
    onMasterSearchClick: () -> Unit,
    viewModel: GameListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ゲームコレクション") },
                actions = {
                    IconButton(onClick = onMasterSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "マスタ検索")
                    }
                    IconButton(onClick = onManualRegisterClick) {
                        Icon(Icons.Default.Add, contentDescription = "手動登録")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onMasterSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "マスタから追加")
            }
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        CollectionFilterBar(
                            filter = uiState.filter,
                            availablePlatforms = uiState.availablePlatforms,
                            onOwnershipChange = viewModel::setOwnershipFilter,
                            onPlayStatusChange = viewModel::setPlayStatusFilter,
                            onRatingChange = viewModel::setRatingFilter,
                            onPlatformChange = viewModel::setPlatformFilter,
                        )
                    }

                    if (uiState.games.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "条件に一致するゲームがありません",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    } else {
                        items(uiState.games, key = { it.collectionItem.id }) { game ->
                            GameListItem(
                                game = game,
                                onClick = { onGameClick(game.collectionItem.id) },
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
