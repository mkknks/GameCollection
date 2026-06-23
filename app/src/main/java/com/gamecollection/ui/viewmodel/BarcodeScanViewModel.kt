package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.entity.GameMasterEntity
import com.gamecollection.data.model.JanLookupResult
import com.gamecollection.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BarcodeScanPhase {
    SCANNING,
    LOADING,
    CONFIRM,
    ALREADY_REGISTERED,
    NOT_FOUND,
    MULTIPLE,
    ERROR,
}

data class BarcodeScanUiState(
    val phase: BarcodeScanPhase = BarcodeScanPhase.SCANNING,
    val scannedJanCode: String? = null,
    val gameMaster: GameMasterEntity? = null,
    val multipleGames: List<GameMasterEntity> = emptyList(),
    val isRegistering: Boolean = false,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false,
)

class BarcodeScanViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BarcodeScanUiState())
    val uiState: StateFlow<BarcodeScanUiState> = _uiState.asStateFlow()

    private var isProcessing = false

    fun onBarcodeScanned(rawCode: String) {
        if (isProcessing || _uiState.value.phase != BarcodeScanPhase.SCANNING) return
        val janCode = GameRepository.normalizeJanCode(rawCode) ?: return

        isProcessing = true
        _uiState.update {
            it.copy(
                phase = BarcodeScanPhase.LOADING,
                scannedJanCode = janCode,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            runCatching {
                repository.lookupByJanCode(janCode)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        phase = BarcodeScanPhase.ERROR,
                        errorMessage = error.message ?: "検索に失敗しました",
                    )
                }
            }.onSuccess { result ->
                _uiState.update { state ->
                    when (result) {
                        is JanLookupResult.Found -> state.copy(
                            phase = BarcodeScanPhase.CONFIRM,
                            gameMaster = result.gameMaster,
                        )
                        is JanLookupResult.AlreadyRegistered -> state.copy(
                            phase = BarcodeScanPhase.ALREADY_REGISTERED,
                            gameMaster = result.gameMaster,
                        )
                        is JanLookupResult.NotFound -> state.copy(
                            phase = BarcodeScanPhase.NOT_FOUND,
                            scannedJanCode = result.janCode,
                        )
                        is JanLookupResult.Multiple -> state.copy(
                            phase = BarcodeScanPhase.MULTIPLE,
                            multipleGames = result.games,
                        )
                    }
                }
            }
            isProcessing = false
        }
    }

    fun selectGame(gameMaster: GameMasterEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(phase = BarcodeScanPhase.LOADING, errorMessage = null) }
            runCatching {
                repository.getRegistrationStatus(gameMaster.id)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        phase = BarcodeScanPhase.MULTIPLE,
                        errorMessage = error.message ?: "検索に失敗しました",
                    )
                }
            }.onSuccess { result ->
                when (result) {
                    is JanLookupResult.Found -> {
                        _uiState.update {
                            it.copy(
                                phase = BarcodeScanPhase.CONFIRM,
                                gameMaster = result.gameMaster,
                                scannedJanCode = result.gameMaster.janCode,
                            )
                        }
                    }
                    is JanLookupResult.AlreadyRegistered -> {
                        _uiState.update {
                            it.copy(
                                phase = BarcodeScanPhase.ALREADY_REGISTERED,
                                gameMaster = result.gameMaster,
                                scannedJanCode = result.gameMaster.janCode,
                            )
                        }
                    }
                    null -> {
                        _uiState.update {
                            it.copy(
                                phase = BarcodeScanPhase.NOT_FOUND,
                                scannedJanCode = gameMaster.janCode,
                            )
                        }
                    }
                    is JanLookupResult.NotFound -> {
                        _uiState.update {
                            it.copy(
                                phase = BarcodeScanPhase.NOT_FOUND,
                                scannedJanCode = result.janCode,
                            )
                        }
                    }
                    is JanLookupResult.Multiple -> Unit
                }
            }
        }
    }

    fun registerGame(onSuccess: () -> Unit) {
        val gameMasterId = _uiState.value.gameMaster?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, errorMessage = null) }
            runCatching {
                repository.addFromMasterByBarcode(gameMasterId)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        errorMessage = when {
                            error.message?.contains("既に") == true -> "登録済みです"
                            else -> error.message ?: "登録に失敗しました"
                        },
                    )
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        registerSuccess = true,
                    )
                }
                onSuccess()
            }
        }
    }

    fun resumeScanning() {
        isProcessing = false
        _uiState.value = BarcodeScanUiState()
    }

    class Factory(
        private val repository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BarcodeScanViewModel(repository) as T
        }
    }
}
