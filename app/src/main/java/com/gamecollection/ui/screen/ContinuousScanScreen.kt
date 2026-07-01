package com.gamecollection.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.gamecollection.data.model.ContinuousScanEntryType
import com.gamecollection.ui.components.BarcodeCameraPreview
import com.gamecollection.ui.viewmodel.ContinuousScanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinuousScanScreen(
    onBack: () -> Unit,
    onFinishScan: () -> Unit,
    viewModel: ContinuousScanViewModel = viewModel(),
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(session.snackbarMessage) {
        session.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("連続スキャン") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Button(
                onClick = onFinishScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("スキャン終了")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            ScanStatsRow(
                readyCount = session.readyCount,
                pendingCount = session.pendingCount,
                unregisteredCount = session.unregisteredCount,
                alreadyRegisteredCount = session.alreadyRegisteredCount,
            )

            session.lastScannedJan?.let { jan ->
                Text(
                    text = "直近JAN: $jan",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (hasCameraPermission) {
                BarcodeCameraPreview(
                    onBarcodeScanned = viewModel::onBarcodeScanned,
                    isScanningEnabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("カメラ権限を許可")
                    }
                }
            }

            Text(
                text = "スキャン結果一覧",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleSmall,
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (session.scanHistory.isEmpty()) {
                    item {
                        Text(
                            text = "バーコードをスキャンしてください",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(session.scanHistory.reversed(), key = { it.scanId }) { entry ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(entry.title, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = "JAN: ${entry.janCode}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    text = entryTypeLabel(entry.type),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                entry.detail?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanStatsRow(
    readyCount: Int,
    pendingCount: Int,
    unregisteredCount: Int,
    alreadyRegisteredCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatChip("登録予定", readyCount)
        StatChip("確認待ち", pendingCount)
        StatChip("未登録", unregisteredCount)
        StatChip("登録済", alreadyRegisteredCount)
    }
}

@Composable
private fun StatChip(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

private fun entryTypeLabel(type: ContinuousScanEntryType): String = when (type) {
    ContinuousScanEntryType.READY -> "登録予定"
    ContinuousScanEntryType.PENDING -> "確認待ち"
    ContinuousScanEntryType.UNREGISTERED -> "未登録"
    ContinuousScanEntryType.ALREADY_REGISTERED -> "登録済み"
}
