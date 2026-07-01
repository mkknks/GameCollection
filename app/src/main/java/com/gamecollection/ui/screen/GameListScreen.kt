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
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamecollection.ui.components.BulkEditDialog
import com.gamecollection.ui.components.CollectionFilterBar
import com.gamecollection.ui.components.GameListItem
import com.gamecollection.ui.viewmodel.GameListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onGameClick: (Long) -> Unit,
    onManualRegisterClick: () -> Unit,
    onMasterSearchClick: () -> Unit,
    onBacklogListClick: () -> Unit,
    onBarcodeScanClick: () -> Unit,
    onContinuousScanClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: GameListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bulkForm by viewModel.bulkEditFormState.collectAsStateWithLifecycle()

    if (uiState.showBulkEditDialog) {
        BulkEditDialog(
            form = bulkForm,
            isUpdating = uiState.isBulkUpdating,
            errorMessage = uiState.bulkUpdateError,
            onOwnershipChange = viewModel::onBulkOwnershipChange,
            onPlayStatusChange = viewModel::onBulkPlayStatusChange,
            onVisibilityChange = viewModel::onBulkVisibilityChange,
            onConfirm = viewModel::applyBulkUpdate,
            onDismiss = viewModel::dismissBulkEditDialog,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isSelectionMode) {
                            "${uiState.selectedIds.size}件選択中"
                        } else {
                            "ゲームコレクション"
                        },
                    )
                },
                navigationIcon = {
                    if (uiState.isSelectionMode) {
                        TextButton(onClick = viewModel::toggleSelectionMode) {
                            Text("キャンセル")
                        }
                    }
                },
                actions = {
                    if (uiState.isSelectionMode) {
                        TextButton(
                            onClick = viewModel::clearSelection,
                            enabled = uiState.selectedIds.isNotEmpty(),
                        ) {
                            Text("選択解除")
                        }
                    } else {
                        IconButton(onClick = onBacklogListClick) {
                            Icon(Icons.Default.VideogameAsset, contentDescription = "積みゲー一覧")
                        }
                        IconButton(onClick = onBarcodeScanClick) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "バーコードスキャン")
                        }
                        IconButton(onClick = onContinuousScanClick) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = "連続スキャン")
                        }
                        IconButton(onClick = viewModel::toggleSelectionMode) {
                            Icon(Icons.Default.Checklist, contentDescription = "複数選択")
                        }
                        IconButton(onClick = onMasterSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "マスタ検索")
                        }
                        IconButton(onClick = onManualRegisterClick) {
                            Icon(Icons.Default.Add, contentDescription = "手動登録")
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "設定")
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (uiState.isSelectionMode && uiState.selectedIds.isNotEmpty()) {
                BottomAppBar {
                    IconButton(onClick = viewModel::showBulkEditDialog) {
                        Icon(Icons.Default.Edit, contentDescription = "一括変更")
                    }
                    Text(
                        text = "${uiState.selectedIds.size}件を一括変更",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(onClick = onMasterSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = "マスタから追加")
                }
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
                    if (!uiState.isSelectionMode) {
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
                            val itemId = game.collectionItem.id
                            GameListItem(
                                game = game,
                                onClick = {
                                    if (uiState.isSelectionMode) {
                                        viewModel.toggleSelection(itemId)
                                    } else {
                                        onGameClick(itemId)
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 16.dp),
                                isSelectionMode = uiState.isSelectionMode,
                                isSelected = uiState.selectedIds.contains(itemId),
                            )
                        }
                    }
                }
            }
        }
    }
}
