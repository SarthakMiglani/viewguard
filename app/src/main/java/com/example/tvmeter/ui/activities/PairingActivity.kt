package com.example.tvmeter.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tvmeter.TvMeterApplication
import com.example.tvmeter.ui.screens.PairingScreen
import com.example.tvmeter.ui.theme.TVMeterTheme
import com.example.tvmeter.viewmodels.PairingViewModel

class PairingActivity : ComponentActivity() {
    private lateinit var viewModel: PairingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as TvMeterApplication).appContainer
        viewModel = appContainer.createPairingViewModel()
        setContent {
            TVMeterTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                PairingScreen(
                    uiState = uiState,
                    onRegisterDevice = { viewModel.registerDevice() },
                    onCheckPairing = { viewModel.checkPairingStatus() },
                    onClearError = { viewModel.clearError() }
                )
            }
        }
    }
} 