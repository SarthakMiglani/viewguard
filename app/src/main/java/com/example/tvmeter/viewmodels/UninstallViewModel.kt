package com.example.tvmeter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmeter.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UninstallViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _pin = MutableStateFlow(listOf("", "", "", ""))
    val pin: StateFlow<List<String>> = _pin.asStateFlow()

    private val _isValidated = MutableStateFlow(false)
    val isValidated: StateFlow<Boolean> = _isValidated.asStateFlow()

    private val _currentFocusIndex = MutableStateFlow(0)
    val currentFocusIndex: StateFlow<Int> = _currentFocusIndex.asStateFlow()

    fun updateDigit(index: Int, digit: String) {
        if (digit.length <= 1 && digit.all { it.isDigit() }) {
            val newPin = _pin.value.toMutableList()
            newPin[index] = digit
            _pin.value = newPin

            if (digit.isNotEmpty() && index < 3) {
                _currentFocusIndex.value = index + 1
            }
        }
    }

    fun setFocusIndex(index: Int) {
        _currentFocusIndex.value = index
    }

    fun validatePin() {
        val enteredPin = _pin.value.joinToString("")
        val isValid = enteredPin == "1234" // Hardcoded validation
        _isValidated.value = isValid

        if (isValid) {
            viewModelScope.launch {
                settingsDataStore.savePin(enteredPin)
            }
        }
    }

    fun clearPin() {
        _pin.value = listOf("", "", "", "")
        _currentFocusIndex.value = 0
        _isValidated.value = false
    }
}