package com.gamecollection.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamecollection.ui.components.OwnershipStatusSelector
import com.gamecollection.ui.components.PlayStatusSelector
import com.gamecollection.ui.components.PurchaseConditionSelector
import com.gamecollection.ui.components.VisibilitySelector
import com.gamecollection.ui.viewmodel.GameDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val game = uiState.game

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game?.gameMaster?.title ?: "ゲーム詳細") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            game == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                ) {
                    Text("ゲームが見つかりませんでした")
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("マスタ情報", style = MaterialTheme.typography.titleMedium)
                    DetailRow(label = "プラットフォーム", value = game.gameMaster.platform)
                    DetailRow(label = "発売元", value = game.gameMaster.publisher)
                    DetailRow(label = "発売年", value = game.gameMaster.releaseYear?.toString())
                    DetailRow(
                        label = "マスタ種別",
                        value = if (game.gameMaster.isUserAdded) "ユーザー追加" else "共有マスタ",
                    )

                    Text("所持状態", style = MaterialTheme.typography.titleSmall)
                    OwnershipStatusSelector(
                        selected = form.ownershipStatus,
                        onSelected = viewModel::onOwnershipStatusChange,
                    )

                    Text("プレイ状態", style = MaterialTheme.typography.titleSmall)
                    PlayStatusSelector(
                        selected = form.playStatus,
                        onSelected = viewModel::onPlayStatusChange,
                    )

                    Text("評価", style = MaterialTheme.typography.titleSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FilterChip(
                            selected = form.rating == null,
                            onClick = { viewModel.onRatingChange(null) },
                            label = { Text("未評価") },
                        )
                        if (form.rating != null) {
                            Text(
                                text = "${form.rating} / 10",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    if (form.rating == null) {
                        Text(
                            text = "評価を付ける場合は下のスライダーを操作してください",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Slider(
                            value = 5f,
                            onValueChange = { viewModel.onRatingChange(it.toInt()) },
                            valueRange = 1f..10f,
                            steps = 8,
                        )
                    } else {
                        Slider(
                            value = form.rating!!.toFloat(),
                            onValueChange = { viewModel.onRatingChange(it.toInt()) },
                            valueRange = 1f..10f,
                            steps = 8,
                        )
                    }

                    Text("購入情報", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = form.purchasePrice,
                        onValueChange = viewModel::onPurchasePriceChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("購入価格（円）") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = form.purchaseStore,
                        onValueChange = viewModel::onPurchaseStoreChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("購入店舗") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = form.purchaseDate,
                        onValueChange = viewModel::onPurchaseDateChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("購入日") },
                        singleLine = true,
                        placeholder = { Text("例: 2024-01-15") },
                    )
                    Text("購入状態", style = MaterialTheme.typography.titleSmall)
                    PurchaseConditionSelector(
                        selected = form.purchaseCondition,
                        onSelected = viewModel::onPurchaseConditionChange,
                    )
                    OutlinedTextField(
                        value = form.purchaseMemo,
                        onValueChange = viewModel::onPurchaseMemoChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("購入メモ") },
                        minLines = 2,
                    )

                    Text("公開設定", style = MaterialTheme.typography.titleSmall)
                    VisibilitySelector(
                        selected = form.visibility,
                        onSelected = viewModel::onVisibilityChange,
                    )

                    OutlinedTextField(
                        value = form.notes,
                        onValueChange = viewModel::onNotesChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("メモ") },
                        minLines = 3,
                    )

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    uiState.saveSuccessMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Button(
                        onClick = viewModel::saveChanges,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (uiState.isSaving) "保存中..." else "変更を保存")
                    }

                    Button(
                        onClick = { viewModel.deleteGame(onBack) },
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("コレクションから削除")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String?,
) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
