package com.gamecollection.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamecollection.ui.components.BarcodeCameraPreview
import com.gamecollection.ui.viewmodel.BarcodeScanPhase
import com.gamecollection.ui.viewmodel.BarcodeScanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScanScreen(
    onBack: () -> Unit,
    onNavigateToManualRegister: (janCode: String) -> Unit,
    onRegistered: () -> Unit,
    viewModel: BarcodeScanViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val isCameraActive = hasCameraPermission && uiState.phase == BarcodeScanPhase.SCANNING

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("バーコードスキャン") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (isCameraActive) {
                BarcodeCameraPreview(
                    onBarcodeScanned = viewModel::onBarcodeScanned,
                    isScanningEnabled = true,
                    modifier = Modifier.fillMaxSize(),
                )
                Text(
                    text = "JAN / EAN-13 バーコードを枠内に合わせてください",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            when (uiState.phase) {
                BarcodeScanPhase.SCANNING -> {
                    if (!hasCameraPermission) {
                        PermissionRequiredContent(
                            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        )
                    }
                }

                BarcodeScanPhase.LOADING -> {
                    LoadingOverlay()
                }

                BarcodeScanPhase.CONFIRM -> {
                    uiState.gameMaster?.let { master ->
                        ResultOverlay {
                            ConfirmContent(
                                title = master.title,
                                platform = master.platform,
                                publisher = master.publisher,
                                janCode = uiState.scannedJanCode,
                                isRegistering = uiState.isRegistering,
                                errorMessage = uiState.errorMessage,
                                onRegister = { viewModel.registerGame(onRegistered) },
                                onRescan = viewModel::resumeScanning,
                            )
                        }
                    }
                }

                BarcodeScanPhase.ALREADY_REGISTERED -> {
                    uiState.gameMaster?.let { master ->
                        ResultOverlay {
                            AlreadyRegisteredContent(
                                title = master.title,
                                platform = master.platform,
                                janCode = uiState.scannedJanCode,
                                onRescan = viewModel::resumeScanning,
                            )
                        }
                    }
                }

                BarcodeScanPhase.NOT_FOUND -> {
                    ResultOverlay {
                        NotFoundContent(
                            janCode = uiState.scannedJanCode.orEmpty(),
                            onManualRegister = {
                                onNavigateToManualRegister(uiState.scannedJanCode.orEmpty())
                            },
                            onRescan = viewModel::resumeScanning,
                        )
                    }
                }

                BarcodeScanPhase.MULTIPLE -> {
                    ResultOverlay {
                        MultipleMatchesContent(
                            games = uiState.multipleGames,
                            errorMessage = uiState.errorMessage,
                            onSelect = viewModel::selectGame,
                            onRescan = viewModel::resumeScanning,
                        )
                    }
                }

                BarcodeScanPhase.ERROR -> {
                    ResultOverlay {
                        ErrorContent(
                            message = uiState.errorMessage ?: "エラーが発生しました",
                            onRescan = viewModel::resumeScanning,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ResultOverlay(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun PermissionRequiredContent(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("カメラの権限が必要です")
            Button(onClick = onRequestPermission) {
                Text("権限を許可")
            }
        }
    }
}

@Composable
private fun ConfirmContent(
    title: String,
    platform: String?,
    publisher: String?,
    janCode: String?,
    isRegistering: Boolean,
    errorMessage: String?,
    onRegister: () -> Unit,
    onRescan: () -> Unit,
) {
    Text("ゲームを確認", style = MaterialTheme.typography.titleMedium)
    InfoLine("タイトル", title)
    platform?.let { InfoLine("プラットフォーム", it) }
    publisher?.let { InfoLine("発売元", it) }
    janCode?.let { InfoLine("JANコード", it) }
    Text(
        text = "所持中 / 未プレイ / 非公開 で登録します",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error)
    }
    Button(
        onClick = onRegister,
        enabled = !isRegistering,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(if (isRegistering) "登録中..." else "コレクションに登録")
    }
    Button(onClick = onRescan, modifier = Modifier.fillMaxWidth()) {
        Text("再スキャン")
    }
}

@Composable
private fun AlreadyRegisteredContent(
    title: String,
    platform: String?,
    janCode: String?,
    onRescan: () -> Unit,
) {
    Text("登録済みです", style = MaterialTheme.typography.titleMedium)
    InfoLine("タイトル", title)
    platform?.let { InfoLine("プラットフォーム", it) }
    janCode?.let { InfoLine("JANコード", it) }
    Button(onClick = onRescan, modifier = Modifier.fillMaxWidth()) {
        Text("再スキャン")
    }
}

@Composable
private fun NotFoundContent(
    janCode: String,
    onManualRegister: () -> Unit,
    onRescan: () -> Unit,
) {
    Text("マスタに一致するゲームがありません", style = MaterialTheme.typography.titleMedium)
    InfoLine("JANコード", janCode)
    Button(onClick = onManualRegister, modifier = Modifier.fillMaxWidth()) {
        Text("手動登録へ")
    }
    Button(onClick = onRescan, modifier = Modifier.fillMaxWidth()) {
        Text("再スキャン")
    }
}

@Composable
private fun MultipleMatchesContent(
    games: List<com.gamecollection.data.entity.GameMasterEntity>,
    errorMessage: String?,
    onSelect: (com.gamecollection.data.entity.GameMasterEntity) -> Unit,
    onRescan: () -> Unit,
) {
    Text("複数のゲームが見つかりました", style = MaterialTheme.typography.titleMedium)
    errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error)
    }
    games.forEach { game ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect(game) },
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(game.title, style = MaterialTheme.typography.titleSmall)
                game.platform?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
    Button(onClick = onRescan, modifier = Modifier.fillMaxWidth()) {
        Text("再スキャン")
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRescan: () -> Unit,
) {
    Text(message, color = MaterialTheme.colorScheme.error)
    Button(onClick = onRescan, modifier = Modifier.fillMaxWidth()) {
        Text("再スキャン")
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
