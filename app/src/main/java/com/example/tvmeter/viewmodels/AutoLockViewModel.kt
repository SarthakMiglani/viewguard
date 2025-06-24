package com.example.tvmeter.viewmodels

// AutoLockViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmeter.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AutoLockViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val autoLockEnabled: StateFlow<Boolean> = settingsDataStore.autoLockEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val autoLockTime: StateFlow<Int> = settingsDataStore.autoLockTime
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 60
        )

    private val _hours = MutableStateFlow(1)
    val hours: StateFlow<Int> = _hours.asStateFlow()

    private val _minutes = MutableStateFlow(0)
    val minutes: StateFlow<Int> = _minutes.asStateFlow()

    fun toggleAutoLock() {
        viewModelScope.launch {
            settingsDataStore.saveAutoLockEnabled(!autoLockEnabled.value)
        }
    }

    fun incrementHours() {
        if (_hours.value < 23) {
            _hours.value += 1
            updateTotalTime()
        }
    }

    fun decrementHours() {
        if (_hours.value > 0) {
            _hours.value -= 1
            updateTotalTime()
        }
    }

    fun incrementMinutes() {
        if (_minutes.value < 59) {
            _minutes.value += 1
            updateTotalTime()
        }
    }

    fun decrementMinutes() {
        if (_minutes.value > 0) {
            _minutes.value -= 1
            updateTotalTime()
        }
    }

    private fun updateTotalTime() {
        val totalMinutes = _hours.value * 60 + _minutes.value
        viewModelScope.launch {
            settingsDataStore.saveAutoLockTime(totalMinutes)
        }
    }
}