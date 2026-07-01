package com.gamecollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gamecollection.data.model.ContinuousScanSession
import com.gamecollection.data.repository.GameRepository
import com.gamecollection.data.session.ContinuousScanSessionHolder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContinuousScanViewModel(
    private val repository: GameRepository,
    private val sessionHolder: ContinuousScanSessionHolder,
) : ViewModel() {
    val session: StateFlow<ContinuousScanSession> = sessionHolder.session
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ContinuousScanSession(),
        )

    init {
        sessionHolder.startNewSession()
    }

    fun onBarcodeScanned(rawCode: String) {
        viewModelScope.launch {
            sessionHolder.handleBarcode(rawCode, repository)
        }
    }

    fun clearSnackbar() {
        sessionHolder.clearSnackbar()
    }

    class Factory(
        private val repository: GameRepository,
        private val sessionHolder: ContinuousScanSessionHolder,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ContinuousScanViewModel(repository, sessionHolder) as T
        }
    }
}
