package com.playground.rooms.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.RoomsLegalEvent
import com.playground.rooms.domain.model.RoomsNotificationEvent
import com.playground.rooms.presentation.components.RoomsAppLogo
import com.playground.rooms.presentation.components.RoomsLegalTermsText
import com.playground.rooms.presentation.components.RoomsNotificationPrompt
import com.playground.rooms.presentation.uistate.RoomsNotificationUiState

// --- SCREEN COMPOSABLE ---

@Composable
fun RoomsNotificationScreen(
    uiState: RoomsNotificationUiState,
    onEvent: (RoomsNotificationEvent) -> Unit
) {
    // Event mapper for legal text
    val legalEventMapper: (RoomsLegalEvent) -> Unit = { event ->
        when (event) {
            is RoomsNotificationEvent -> onEvent(event)
            else -> Unit
        }
    }
    
    val isButtonActionEnabled = !uiState.isProcessing

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. App Logo
            RoomsAppLogo(modifier = Modifier.align(Alignment.Start))

            Spacer(Modifier.height(64.dp))

            // 2. Headline
            Text(
                text = "Don't miss a thing",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(48.dp))

            // 3. Notification Prompt & Icon
            RoomsNotificationPrompt(
                instructionText = "Stay up-to-date with new messages and activity in your rooms. You can change this later in settings."
            )

            // Fill space until the bottom block
            Spacer(Modifier.weight(1f))

            // 4. Action Buttons

            // Enable Notifications (Primary)
            Button(
                onClick = { onEvent(RoomsNotificationEvent.EnableClicked) },
                enabled = isButtonActionEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Enable notifications", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Skip for now (Secondary)
            OutlinedButton(
                onClick = { onEvent(RoomsNotificationEvent.SkipClicked) },
                enabled = isButtonActionEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Skip for now", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))

            // 5. Legal/Terms Block
            RoomsLegalTermsText(
                onEvent = legalEventMapper,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms Notification Screen (Default)")
@Composable
fun RoomsNotificationScreenPreviewDefault() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsNotificationScreen(
            uiState = RoomsNotificationUiState(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Rooms Notification Screen (Processing)")
@Composable
fun RoomsNotificationScreenPreviewProcessing() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsNotificationScreen(
            uiState = RoomsNotificationUiState(isProcessing = true),
            onEvent = {}
        )
    }
}
