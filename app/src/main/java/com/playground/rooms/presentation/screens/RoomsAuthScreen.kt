package com.playground.rooms.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.RoomsAuthEvent
import com.playground.rooms.presentation.components.RoomsAppLogo
import com.playground.rooms.presentation.components.RoomsHeadlineWithAvatars
import com.playground.rooms.presentation.components.RoomsLegalTermsText
import com.playground.rooms.presentation.uistate.RoomsAuthUiState

@Composable
fun RoomsAuthScreen(
    uiState: RoomsAuthUiState,
    onEvent: (RoomsAuthEvent) -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. App Logo
            RoomsAppLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            )

            Spacer(Modifier.height(80.dp))

            // 2. Headline
            RoomsHeadlineWithAvatars(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .weight(1f, fill = false)
            )

            Spacer(Modifier.height(100.dp)) // Spacer to push buttons down

            // 3. Action Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Sign up with Apple
                FilledTonalButton(
                    onClick = { onEvent(RoomsAuthEvent.SignUpAppleClicked) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        // NOTE: Placeholder for Apple icon
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Sign up with Apple logo",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sign up with Apple", fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))

                // I have an account (Login)
                OutlinedButton(
                    onClick = { onEvent(RoomsAuthEvent.LoginClicked) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("I have an account", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // 4. Legal/Terms Block
            RoomsLegalTermsText(
                onEvent = { event ->
                    when (event) {
                        is RoomsAuthEvent -> onEvent(event)
                        else -> {}
                    }
                }
            )
        }
    }
    // Handle Loading/Error states
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

// --- PREVIEW (Refactored) ---

@Preview(showBackground = true, name = "Rooms Onboarding Screen")
@Composable
fun RoomsAuthScreenPreview() {
    // Assuming a simple MaterialTheme setup for preview
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsAuthScreen(
            uiState = RoomsAuthUiState(
                avatarUrls = listOf(
                    "https://i.pravatar.cc/40?img=1",
                    "https://i.pravatar.cc/40?img=5"
                )
            ),
            onEvent = {} // No-op for preview
        )
    }
}
