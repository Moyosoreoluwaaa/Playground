package com.playground.rooms.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.NotificationSetting
import com.playground.rooms.domain.model.NotificationType
import com.playground.rooms.presentation.components.RoomsNotificationToggleItem
import com.playground.rooms.presentation.components.RoomsProfileHeader
import com.playground.rooms.presentation.uistate.RoomsNotificationSettingsEvent
import com.playground.rooms.presentation.uistate.RoomsNotificationSettingsUiState

// --- SCREEN COMPOSABLE ---

@Composable
fun RoomsNotificationSettingsScreen(
    uiState: RoomsNotificationSettingsUiState,
    onEvent: (RoomsNotificationSettingsEvent) -> Unit
) {
    Scaffold(
        topBar = {
            RoomsProfileHeader(
                title = "Notifications",
                onBackClicked = { onEvent(RoomsNotificationSettingsEvent.BackClicked) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Section Header: Messages
            item {
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            // List of Toggle Items
            items(uiState.settings) { setting ->
                RoomsNotificationToggleItem(
                    setting = setting,
                    onToggleChanged = { type, isEnabled ->
                        onEvent(RoomsNotificationSettingsEvent.ToggleSetting(type, isEnabled))
                    }
                )
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms Notification Settings Screen")
@Composable
fun RoomsNotificationSettingsScreenPreview() {
    val sampleSettings = listOf(
        NotificationSetting(
            type = NotificationType.DIRECT_MESSAGES,
            title = "Direct Messages",
            description = "Alerts for one-on-one chats.",
            isEnabled = true
        ),
        NotificationSetting(
            type = NotificationType.ROOMS,
            title = "Rooms",
            description = "Alerts for all room activity.",
            isEnabled = true
        ),
        NotificationSetting(
            type = NotificationType.MENTIONS,
            title = "Mentions (@your-handle)",
            description = "Alerts when you are mentioned.",
            isEnabled = false
        ),
        NotificationSetting(
            type = NotificationType.NEW_ROOM_MESSAGES,
            title = "New Messages in joined rooms",
            description = "Only for rooms you have joined.",
            isEnabled = true
        ),
        NotificationSetting(
            type = NotificationType.TRENDING_ROOMS,
            title = "Trending rooms",
            description = "Only for rooms you have joined.",
            isEnabled = false
        ),
        NotificationSetting(
            type = NotificationType.ANNOUNCEMENTS,
            title = "App announcements",
            description = "Product updates and news.",
            isEnabled = true
        )
    )

    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsNotificationSettingsScreen(
            uiState = RoomsNotificationSettingsUiState(settings = sampleSettings),
            onEvent = {}
        )
    }
}
