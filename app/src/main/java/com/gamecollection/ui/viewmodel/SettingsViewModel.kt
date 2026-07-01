package com.gamecollection.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.repository.GameRepository
import com.gamecollection.export.CsvExporter
import com.gamecollection.export.CsvImporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val errorMessage: String? = null,
    val exportSuccessMessage: String? = null,
    val importResultMessage: String? = null,
    val shareUri: Uri? = null,
)

class SettingsViewModel(
    private val repository: GameRepository,
    private val csvExporter: CsvExporter,
    private val csvImporter: CsvImporter,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun exportCsv() {
        if (_uiState.value.isExporting || _uiState.value.isImporting) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isExporting = true,
                    errorMessage = null,
                    exportSuccessMessage = null,
                    importResultMessage = null,
                    shareUri = null,
                )
            }
            runCatching {
                val games = repository.getAllCollection()
                csvExporter.export(games)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = error.message ?: "CSVの出力に失敗しました",
                    )
                }
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportSuccessMessage = "${result.rowCount}件をエクスポートしました",
                        shareUri = result.uri,
                    )
                }
            }
        }
    }

    fun importCsv(uri: Uri) {
        if (_uiState.value.isExporting || _uiState.value.isImporting) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isImporting = true,
                    errorMessage = null,
                    exportSuccessMessage = null,
                    importResultMessage = null,
                )
            }
            runCatching {
                repository.importCollectionFromCsv(uri, csvImporter)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        errorMessage = error.message ?: "CSVのインポートに失敗しました",
                    )
                }
            }.onSuccess { result ->
                val message = buildString {
                    appendLine("インポート完了")
                    append("成功: ${result.successCount}件")
                    append(" / スキップ: ${result.skippedCount}件")
                    append(" / エラー: ${result.errorCount}件")
                }
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        importResultMessage = message.trim(),
                        errorMessage = result.errorMessages.takeIf { it.isNotEmpty() }?.joinToString("\n"),
                    )
                }
            }
        }
    }

    fun onShareHandled() {
        _uiState.update { it.copy(shareUri = null) }
    }

    class Factory(
        private val repository: GameRepository,
        private val csvExporter: CsvExporter,
        private val csvImporter: CsvImporter,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository, csvExporter, csvImporter) as T
        }
    }
}
