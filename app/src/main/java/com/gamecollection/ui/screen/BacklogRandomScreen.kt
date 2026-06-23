package com.gamecollection.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.gamecollection.ui.viewmodel.BacklogRandomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacklogRandomScreen(
    onBack: () -> Unit,
    viewModel: BacklogRandomViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("積みゲーランダム選択") },
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

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "積みゲー: ${uiState.backlogGames.size}本",
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Button(
                        onClick = viewModel::pickRandom,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("ランダムに選ぶ")
                    }

                    when {
                        !uiState.hasPicked -> Unit

                        uiState.pickedGame == null -> {
                            Text(
                                text = "積みゲーがありません",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        else -> {
                            val game = uiState.pickedGame!!
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = game.gameMaster.title,
                                        style = MaterialTheme.typography.headlineSmall,
                                    )
                                    game.gameMaster.platform?.let { platform ->
                                        DetailLine(label = "プラットフォーム", value = platform)
                                    }
                                    game.collectionItem.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                                        DetailLine(label = "メモ", value = notes)
                                    }
                                    DetailLine(
                                        label = "評価",
                                        value = game.collectionItem.rating?.let { "$it / 10" } ?: "未評価",
                                    )
                                }
                            }
                            Button(
                                onClick = viewModel::resetPick,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("もう一度選ぶ")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
