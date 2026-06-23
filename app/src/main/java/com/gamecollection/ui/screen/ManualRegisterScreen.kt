package com.gamecollection.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamecollection.ui.components.OwnershipStatusSelector
import com.gamecollection.ui.components.PlayStatusSelector
import com.gamecollection.ui.viewmodel.ManualRegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualRegisterScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    viewModel: ManualRegisterViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("手動登録") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "マスタに新規ゲームを追加し、コレクションへ登録します",
                style = MaterialTheme.typography.bodyMedium,
            )

            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("タイトル *") },
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.platform,
                onValueChange = viewModel::onPlatformChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("プラットフォーム") },
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.publisher,
                onValueChange = viewModel::onPublisherChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("発売元") },
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.releaseYear,
                onValueChange = viewModel::onReleaseYearChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("発売年") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = uiState.janCode,
                onValueChange = viewModel::onJanCodeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("JANコード") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Text("所持状態", style = MaterialTheme.typography.titleSmall)
            OwnershipStatusSelector(
                selected = uiState.ownershipStatus,
                onSelected = viewModel::onOwnershipStatusChange,
            )

            Text("プレイ状態", style = MaterialTheme.typography.titleSmall)
            PlayStatusSelector(
                selected = uiState.playStatus,
                onSelected = viewModel::onPlayStatusChange,
            )

            OutlinedTextField(
                value = uiState.notes,
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

            Button(
                onClick = { viewModel.submit(onRegistered) },
                enabled = !uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (uiState.isSubmitting) "登録中..." else "登録する")
            }
        }
    }
}
