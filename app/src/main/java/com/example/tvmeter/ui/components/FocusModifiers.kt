package com.example.tvmeter.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import com.example.tvmeter.ui.theme.TVFocusColor
import com.example.tvmeter.ui.theme.TVFocusColorDark

@Composable
fun Modifier.tvFocusable(): Modifier {
    var isFocused by remember { mutableStateOf(false) }

    return this
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused || focusState.hasFocus
        }
        .focusable()
        .then(
            if (isFocused) {
                Modifier.border(
                    width = 3.dp,
                    color = TVFocusColor,
                    shape = MaterialTheme.shapes.medium
                )
            } else {
                Modifier.border(
                    width = 1.dp,
                    color = Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
            }
        )
        .padding(2.dp)
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Modifier.tvClickableFocus(
    onClick: () -> Unit = {}
): Modifier {
    var isFocused by remember { mutableStateOf(false) }

    return this
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused || focusState.hasFocus
        }
        .focusable()
        .then(
            if (isFocused) {
                Modifier.border(
                    width = 3.dp,
                    color = TvMaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
            } else {
                Modifier.border(
                    width = 1.dp,
                    color = Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
            }
        )
        .padding(2.dp)
}

// Enhanced TV-specific focus modifier with better visual feedback
@Composable
fun Modifier.tvCardFocus(
    onClick: (() -> Unit)? = null
): Modifier {
    var isFocused by remember { mutableStateOf(false) }

    return this
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        }
        .focusable()
        .then(
            if (isFocused) {
                Modifier
                    .border(
                        width = 4.dp,
                        color = TVFocusColor, // Use theme color instead of hardcoded
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(2.dp)
            } else {
                Modifier
                    .border(
                        width = 2.dp,
                        color = Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(2.dp)
            }
        )
}