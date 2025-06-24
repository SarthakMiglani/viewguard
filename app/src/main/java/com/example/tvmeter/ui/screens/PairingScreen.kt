package com.example.tvmeter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvmeter.viewmodels.PairingStep
import com.example.tvmeter.viewmodels.PairingUiState
import kotlinx.coroutines.delay

@Composable
fun PairingScreen(
    uiState: PairingUiState,
    onRegisterDevice: () -> Unit,
    onCheckPairing: () -> Unit,
    onClearError: () -> Unit
) {
    // Focus management
    val startPairingFocusRequester = remember { FocusRequester() }
    val tryAgainFocusRequester = remember { FocusRequester() }
    val dismissErrorFocusRequester = remember { FocusRequester() }

    var isStartPairingFocused by remember { mutableStateOf(false) }
    var isTryAgainFocused by remember { mutableStateOf(false) }
    var isDismissErrorFocused by remember { mutableStateOf(false) }

    // Auto-check pairing status
    LaunchedEffect(uiState.step) {
        if (uiState.step == PairingStep.WAITING_FOR_PAIR) {
            while (true) {
                delay(5000)
                onCheckPairing()
            }
        }
    }

    // Focus management for initial load and state changes
    LaunchedEffect(uiState.step) {
        when (uiState.step) {
            PairingStep.INITIAL -> {
                delay(100) // Small delay to ensure composition is complete
                startPairingFocusRequester.requestFocus()
            }
            PairingStep.PAIRED_FAILURE -> {
                delay(100)
                tryAgainFocusRequester.requestFocus()
            }
            else -> { /* No focus change needed */ }
        }
    }

    // Focus error dismiss button when error appears
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            delay(100)
            dismissErrorFocusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Title with TV-appropriate sizing
                Text(
                    text = "📺 TV Meter Pairing",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Content based on step
                when (uiState.step) {
                    PairingStep.INITIAL -> {
                        Text(
                            text = "Connect your TV to the TV Meter mobile app",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // TV-friendly button with focus indication
                        Button(
                            onClick = onRegisterDevice,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(56.dp)
                                .focusRequester(startPairingFocusRequester)
                                .onFocusChanged { isStartPairingFocused = it.isFocused }
                                .then(
                                    if (isStartPairingFocused) {
                                        Modifier.border(
                                            3.dp,
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(28.dp)
                                        )
                                    } else Modifier
                                ),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = if (isStartPairingFocused) 8.dp else 4.dp
                            )
                        ) {
                            Text(
                                text = "Start Pairing",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    PairingStep.WAITING_FOR_PAIR -> {
                        Text(
                            text = "Enter this pairing code in your mobile app:",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        // Enhanced pairing code display
                        Card(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = uiState.pairingCode ?: "------",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                letterSpacing = 6.sp,
                                modifier = Modifier.padding(horizontal = 40.dp, vertical = 24.dp)
                            )
                        }

                        Text(
                            text = "📱 Open TV Meter app on your phone and enter the code above",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        if (uiState.isLoading) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                                Text(
                                    text = "Waiting for connection...",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    PairingStep.PAIRED_SUCCESS -> {
                        // Success animation-like display
                        Card(
                            modifier = Modifier.wrapContentSize(),
                            shape = RoundedCornerShape(50.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "✅",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(24.dp)
                            )
                        }

                        Text(
                            text = "Successfully Paired!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Your TV is now connected to TV Meter.\nYou can now monitor your screen time.",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }

                    PairingStep.PAIRED_FAILURE -> {
                        Card(
                            modifier = Modifier.wrapContentSize(),
                            shape = RoundedCornerShape(50.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(24.dp)
                            )
                        }

                        Text(
                            text = "Pairing Failed",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Unable to connect to TV Meter app.\nPlease try again.",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onRegisterDevice,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(56.dp)
                                .focusRequester(tryAgainFocusRequester)
                                .onFocusChanged { isTryAgainFocused = it.isFocused }
                                .then(
                                    if (isTryAgainFocused) {
                                        Modifier.border(
                                            3.dp,
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(28.dp)
                                        )
                                    } else Modifier
                                ),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = if (isTryAgainFocused) 8.dp else 4.dp
                            )
                        ) {
                            Text(
                                text = "Try Again",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    else -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "Loading...",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                // Enhanced error display with TV-friendly styling
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "Error",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }

                            Text(
                                text = error,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )

                            Button(
                                onClick = onClearError,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(48.dp)
                                    .focusRequester(dismissErrorFocusRequester)
                                    .onFocusChanged { isDismissErrorFocused = it.isFocused }
                                    .then(
                                        if (isDismissErrorFocused) {
                                            Modifier.border(
                                                2.dp,
                                                MaterialTheme.colorScheme.error,
                                                RoundedCornerShape(24.dp)
                                            )
                                        } else Modifier
                                    ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = RoundedCornerShape(24.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = if (isDismissErrorFocused) 6.dp else 3.dp
                                )
                            ) {
                                Text(
                                    text = "Dismiss",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PairingScreenPreview() {
    MaterialTheme {
        PairingScreen(
            uiState = PairingUiState(
                step = PairingStep.WAITING_FOR_PAIR,
                pairingCode = "TV-123456"
            ),
            onRegisterDevice = {},
            onCheckPairing = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PairingScreenInitialPreview() {
    MaterialTheme {
        PairingScreen(
            uiState = PairingUiState(
                step = PairingStep.INITIAL
            ),
            onRegisterDevice = {},
            onCheckPairing = {},
            onClearError = {}
        )
    }
}