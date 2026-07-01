package com.gamecollection.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.gamecollection.ui.components.CandidateSelectionDialog
import com.gamecollection.ui.viewmodel.ContinuousScanResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinuousScanResultScreen(
    onBack: () -> Unit,
    onNavigateToManualRegister: (janCode: String) -> Unit,
    viewModel: ContinuousScanResultViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bulkForm by viewModel.bulkEditFormState.collectAsStateWithLifecycle()
    val session = uiState.session

    uiState.candidateDialogItem?.let { pending ->
        CandidateSelectionDialog(
            janCode = pending.janCode,
            candidates = pending.candidates,
            onSelect = viewModel::resolveCandidate,
            onDismiss = viewModel::dismissCandidateDialog,
        )
    }

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
                title = { Text("連続スキャン結果") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "登録予定 ${session.readyCount} / 確認待ち ${session.pendingCount} / " +
                        "未登録 ${session.unregisteredCount} / 登録済 ${session.alreadyRegisteredCount}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text("登録予定ゲーム", style = MaterialTheme.typography.titleMedium)
                if (session.readyToRegister.isEmpty()) {
                    Text(
                        text = "登録予定のゲームがありません",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            TextButton(onClick = viewModel::selectAllReady) { Text("全選択") }
                            TextButton(onClick = viewModel::clearSelection) { Text("選択解除") }
                        }
                        session.readyToRegister.forEach { item ->
                            ReadyItemRow(
                                title = item.gameMaster.title,
                                platform = item.gameMaster.platform,
                                janCode = item.janCode,
                                isSelected = item.scanId in uiState.selectedScanIds,
                                onToggle = { viewModel.toggleSelection(item.scanId) },
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = viewModel::registerSelected,
                    enabled = !uiState.isRegistering && uiState.selectedScanIds.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        if (uiState.isRegistering) {
                            "登録中..."
                        } else {
                            "選択したゲームを登録（${uiState.selectedScanIds.size}件）"
                        },
                    )
                }
                uiState.registerError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }

            if (uiState.registeredCount > 0) {
                item {
                    Text(
                        text = "登録完了: ${uiState.registeredCount}件",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Button(
                        onClick = viewModel::showBulkEditDialog,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("登録したゲームを一括変更")
                    }
                    if (uiState.bulkUpdateSuccess) {
                        Text(
                            text = "一括変更を保存しました",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            if (session.pendingConfirmation.isNotEmpty()) {
                item {
                    Text("確認待ち（候補選択）", style = MaterialTheme.typography.titleMedium)
                    session.pendingConfirmation.forEach { pending ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text("JAN: ${pending.janCode}")
                                Text("候補 ${pending.candidates.size}件")
                                Button(onClick = { viewModel.showCandidateDialog(pending) }) {
                                    Text("候補を選択")
                                }
                            }
                        }
                    }
                }
            }

            if (session.unregistered.isNotEmpty()) {
                item {
                    Text("未登録JAN", style = MaterialTheme.typography.titleMedium)
                    session.unregistered.forEach { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(item.janCode, style = MaterialTheme.typography.bodyLarge)
                                TextButton(onClick = { onNavigateToManualRegister(item.janCode) }) {
                                    Text("手動登録")
                                }
                            }
                        }
                    }
                }
            }

            if (session.alreadyRegistered.isNotEmpty()) {
                item {
                    Text("登録済み", style = MaterialTheme.typography.titleMedium)
                    session.alreadyRegistered.forEach { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(item.gameMaster.title, style = MaterialTheme.typography.titleSmall)
                                Text("JAN: ${item.janCode}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = "登録済みです",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadyItemRow(
    title: String,
    platform: String?,
    janCode: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = isSelected, onCheckedChange = null)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                platform?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                Text("JAN: $janCode", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
