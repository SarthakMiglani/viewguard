package com.example.tvmeter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmeter.data.remote.DeviceRepository
import com.example.tvmeter.data.remote.dto.RegisterDeviceResponse
import com.example.tvmeter.utils.NetworkResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PairingViewModel(
    private val repository: DeviceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PairingUiState())
    val uiState: StateFlow<PairingUiState> = _uiState.asStateFlow()

    fun registerDevice() {
        viewModelScope.launch {
            repository.registerDevice().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            pairingCode = result.data?.pairingCode,
                            deviceId = result.data?.deviceId,
                            step = PairingStep.WAITING_FOR_PAIR
                        )
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun checkPairingStatus() {
        viewModelScope.launch {
            _uiState.value.pairingCode?.let { code ->
                repository.pairDevice(code).collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                        is NetworkResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                step = PairingStep.PAIRED_SUCCESS
                            )
                        }
                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PairingUiState(
    val isLoading: Boolean = false,
    val pairingCode: String? = null,
    val deviceId: String? = null,
    val step: PairingStep = PairingStep.INITIAL,
    val error: String? = null
)

enum class PairingStep {
    INITIAL,
    REGISTERING,
    WAITING_FOR_PAIR,
    PAIRED_SUCCESS,
    PAIRED_FAILURE
} 