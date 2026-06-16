package com.gamecollection.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamecollection.ui.components.StatusSelector
import com.gamecollection.ui.viewmodel.GameDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val game = uiState.game

    var notes by rememberSaveable { mutableStateOf("") }
    var rating by rememberSaveable { mutableFloatStateOf(0f) }

    LaunchedEffect(game?.collectionItem?.id, game?.collectionItem?.notes, game?.collectionItem?.rating) {
        notes = game?.collectionItem?.notes.orEmpty()
        rating = (game?.collectionItem?.rating ?: 0).toFloat()
    }

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
                    DetailRow(label = "プラットフォーム", value = game.gameMaster.platform)
                    DetailRow(label = "発売元", value = game.gameMaster.publisher)
                    DetailRow(
                        label = "発売年",
                        value = game.gameMaster.releaseYear?.toString(),
                    )
                    DetailRow(
                        label = "マスタ種別",
                        value = if (game.gameMaster.isUserAdded) "ユーザー追加" else "共有マスタ",
                    )

                    Text("ステータス", style = MaterialTheme.typography.titleSmall)
                    StatusSelector(
                        selected = game.collectionItem.status,
                        onSelected = viewModel::updateStatus,
                    )

                    Text("評価: ${rating.toInt()} / 5", style = MaterialTheme.typography.titleSmall)
                    Slider(
                        value = rating,
                        onValueChange = { rating = it },
                        valueRange = 0f..5f,
                        steps = 4,
                        onValueChangeFinished = {
                            val value = rating.toInt().takeIf { it > 0 }
                            viewModel.updateRating(value)
                        },
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("メモ") },
                        minLines = 3,
                    )
                    Button(
                        onClick = { viewModel.updateNotes(notes) },
                        enabled = !uiState.isSaving,
                    ) {
                        Text("メモを保存")
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                        )
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
