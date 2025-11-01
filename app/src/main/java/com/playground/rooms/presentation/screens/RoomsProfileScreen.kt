package com.playground.rooms.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import com.playground.rooms.presentation.components.RoomsEditableAvatar
import com.playground.rooms.presentation.components.RoomsProfileHeader
import com.playground.rooms.presentation.components.RoomsProfileListItem
import com.playground.rooms.presentation.uistate.RoomsProfileEvent
import com.playground.rooms.presentation.uistate.RoomsProfileUiState

// --- SCREEN COMPOSABLE ---

@Composable
fun RoomsProfileScreen(
    uiState: RoomsProfileUiState,
    onEvent: (RoomsProfileEvent) -> Unit
) {
    Scaffold(
        topBar = {
            RoomsProfileHeader(
                title = "Profile",
                onBackClicked = { onEvent(RoomsProfileEvent.BackClicked) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Profile Picture and Name/Handle
            Spacer(Modifier.height(16.dp))
            RoomsEditableAvatar(
                avatarUrl = uiState.avatarUrl,
                onEditClicked = { onEvent(RoomsProfileEvent.EditAvatarClicked) }
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = uiState.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = uiState.handle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // 2. Account Info List (using a Column for simplicity over LazyColumn)
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f) // Push bottom items down
            ) {
                // Account row is slightly different
                RoomsProfileListItem(
                    title = "Account",
                    value = uiState.name, // Using name as the account value placeholder
                    onClick = { onEvent(RoomsProfileEvent.AccountSettingsClicked) },
                    modifier = Modifier.padding(top = 8.dp)
                )

                RoomsProfileListItem(
                    title = "Username",
                    value = uiState.handle,
                    onClick = { onEvent(RoomsProfileEvent.EditUsernameClicked) }
                )
                
                RoomsProfileListItem(
                    title = "Email",
                    value = uiState.email,
                    onClick = { onEvent(RoomsProfileEvent.EditEmailClicked) }
                )
                
                RoomsProfileListItem(
                    title = "Bio",
                    value = if (uiState.bio.length > 20) uiState.bio.substring(0, 20) + "..." else uiState.bio,
                    onClick = { onEvent(RoomsProfileEvent.EditBioClicked) }
                )

                // Notification Settings row - Note: this row doesn't show a value in the image
                ListItem(
                    headlineContent = { Text("Notification Settings") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth().clickable(onClick = { onEvent(RoomsProfileEvent.NotificationSettingsClicked) }),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(Modifier.height(16.dp))
            }

            // 3. Action Buttons
            val isButtonActionEnabled = !uiState.isSaving && uiState.hasChanges

            Button (
                onClick = { onEvent(RoomsProfileEvent.SaveChangesClicked) },
                enabled = isButtonActionEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onEvent(RoomsProfileEvent.LogoutClicked) },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Secondary Log Out", fontWeight = FontWeight.SemiBold, color = Color.Black)
            }

            Spacer(Modifier.height(24.dp))

            // 4. Footer Text
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi non tortor vitae turpis.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms Profile Screen")
@Composable
fun RoomsProfileScreenPreview() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsProfileScreen(
            uiState = RoomsProfileUiState(
                name = "Anna Scott",
                handle = "@anna_scott",
                email = "anna.scott@email.com",
                hasChanges = true // Button enabled for preview
            ),
            onEvent = {}
        )
    }
}
