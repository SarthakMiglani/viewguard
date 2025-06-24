package com.example.tvmeter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmeter.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.*

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val pin: StateFlow<String> = settingsDataStore.pin
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

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
}