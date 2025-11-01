package com.playground.rooms.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme.colorScheme
import com.playground.rooms.domain.model.HandleValidationState
import com.playground.rooms.domain.model.RoomsHandleEvent
import com.playground.rooms.presentation.components.RoomsAppLogo
import com.playground.rooms.presentation.components.RoomsHandleInput
import com.playground.rooms.presentation.uistate.RoomsHandleUiState

// --- SCREEN COMPOSABLE ---

@Composable
fun RoomsHandleScreen(
    uiState: RoomsHandleUiState,
    onEvent: (RoomsHandleEvent) -> Unit
) {
    // Note: Reusing the legal text component from the previous step
    val legalEventMapper: (RoomsHandleEvent) -> Unit = { event ->
        when (event) {
            is RoomsHandleEvent.TermsClicked -> onEvent(RoomsHandleEvent.TermsClicked)
            is RoomsHandleEvent.PrivacyClicked -> onEvent(RoomsHandleEvent.PrivacyClicked)
            else -> Unit
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // 1. App Logo
            RoomsAppLogo(modifier = Modifier.align(Alignment.Start))

            Spacer(Modifier.height(64.dp))

            // 2. Headline
            Text(
                text = "Choose your unique handle",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(48.dp))

            // 3. Input Section
            RoomsHandleInput(
                handle = uiState.currentHandle,
                validationState = uiState.validationState,
                onHandleChange = { onEvent(RoomsHandleEvent.HandleChanged(it)) }
            )

            // Fill space until the bottom block
            Spacer(Modifier.weight(1f))

            // 4. Confirm Button (Assumed)
            FilledTonalButton(
                onClick = { onEvent(RoomsHandleEvent.ConfirmHandleClicked) },
                enabled = uiState.isConfirmEnabled && !uiState.isScreenLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isScreenLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = colorScheme.onPrimary)
                } else {
                    Text("Confirm Handle", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // 5. Legal/Terms Block
            // Note: The legal text in the image looks center-aligned but the previous plan used padding for visual effect
//            RoomsHandleLegalTermsText(
//                onEvent = legalEventMapper,
//                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
//            )
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms Handle Screen (Valid)")
@Composable
fun RoomsHandleScreenPreviewValid() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsHandleScreen(
            uiState = RoomsHandleUiState(
                currentHandle = "cool_user_1",
                validationState = HandleValidationState.VALID,
                isConfirmEnabled = true
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Rooms Handle Screen (Unavailable/Error)")
@Composable
fun RoomsHandleScreenPreviewUnavailable() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsHandleScreen(
            uiState = RoomsHandleUiState(
                currentHandle = "rooms",
                validationState = HandleValidationState.UNAVAILABLE,
                isConfirmEnabled = false
            ),
            onEvent = {}
        )
    }
}
