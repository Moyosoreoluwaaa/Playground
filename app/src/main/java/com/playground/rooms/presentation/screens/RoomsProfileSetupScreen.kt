package com.playground.rooms.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.RoomsLegalEvent
import com.playground.rooms.domain.model.RoomsProfileSetupEvent
import com.playground.rooms.presentation.components.RoomsAppLogo
import com.playground.rooms.presentation.components.RoomsLegalTermsText
import com.playground.rooms.presentation.components.RoomsProfilePictureArea
import com.playground.rooms.presentation.uistate.RoomsProfileSetupUiState

// --- SCREEN COMPOSABLE ---

@Composable
fun RoomsProfileSetupScreen(
    uiState: RoomsProfileSetupUiState,
    onEvent: (RoomsProfileSetupEvent) -> Unit
) {
    // Event mapper for legal text
    val legalEventMapper: (RoomsLegalEvent) -> Unit = { event ->
        when (event) {
            is RoomsProfileSetupEvent -> onEvent(event)
            else -> Unit
        }
    }

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
                text = "Add a profile picture",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(48.dp))

            // 3. Profile Picture Area
            RoomsProfilePictureArea(
                imageUrl = uiState.currentPhotoUrl,
                isUploading = uiState.isUploading,
                onUploadClicked = { onEvent(RoomsProfileSetupEvent.UploadPhotoClicked) },
                onChooseAvatarClicked = { onEvent(RoomsProfileSetupEvent.ChooseAvatarClicked) },
                modifier = Modifier.weight(1f, fill = false)
            )

            // Spacer to push content down
            Spacer(Modifier.weight(.8f))

            // 4. Action Buttons
            val isButtonActionEnabled = !uiState.isUploading && !uiState.isCompletingSetup

            // Continue Button (Primary)
            Button(
                onClick = { onEvent(RoomsProfileSetupEvent.ContinueClicked) },
                enabled = isButtonActionEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black) // Use a high-contrast dark color
            ) {
                if (uiState.isCompletingSetup) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Continue", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Skip Button (Secondary)
            OutlinedButton(
                onClick = { onEvent(RoomsProfileSetupEvent.SkipClicked) },
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

@Preview(showBackground = true, name = "Rooms Profile Setup Screen (Default)")
@Composable
fun RoomsProfileSetupScreenPreviewDefault() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsProfileSetupScreen(
            uiState = RoomsProfileSetupUiState(currentPhotoUrl = null),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Rooms Profile Setup Screen (Loading)")
@Composable
fun RoomsProfileSetupScreenPreviewLoading() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsProfileSetupScreen(
            uiState = RoomsProfileSetupUiState(
                currentPhotoUrl = "https://i.pravatar.cc/150?img=49",
                isCompletingSetup = true
            ),
            onEvent = {}
        )
    }
}
