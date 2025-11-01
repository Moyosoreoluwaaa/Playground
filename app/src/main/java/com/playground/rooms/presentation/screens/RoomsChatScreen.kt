package com.playground.rooms.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.Message
import com.playground.rooms.presentation.components.MessageBubble
import com.playground.rooms.presentation.components.RoomsChatHeader
import com.playground.rooms.presentation.components.RoomsChatInputBar
import com.playground.rooms.presentation.uistate.RoomsChatEvent
import com.playground.rooms.presentation.uistate.RoomsChatUiState

// --- SCREEN COMPOSABLE ---

@Composable
fun RoomsChatScreen(
    uiState: RoomsChatUiState,
    onEvent: (RoomsChatEvent) -> Unit
) {
    Scaffold(
        topBar = {
            RoomsChatHeader(
                roomName = uiState.roomName,
                onBackClicked = { onEvent(RoomsChatEvent.BackClicked) }
            )
        },
        bottomBar = {
            RoomsChatInputBar(
                messageDraft = uiState.messageDraft,
                onMessageChange = { onEvent(RoomsChatEvent.MessageDraftChanged(it)) },
                onSendClicked = { onEvent(RoomsChatEvent.SendClicked) },
                canSend = uiState.messageDraft.isNotBlank()
            )
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()

        // To-do: Use LaunchedEffect to scroll to the bottom on initial load and new message arrival

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = listState,
            reverseLayout = false,
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(
                    message = message,
                    isCurrentUser = message.senderId == uiState.currentUserId,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (uiState.isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms Chat Screen")
@Composable
fun RoomsChatScreenPreview() {
    val otherUserId = "user_111"
    val currentUser = "user_456"

    val sampleMessages = listOf(
        Message(
            "m1",
            otherUserId,
            "Hey, check out this mockup!",
            "1:30 PM",
            senderAvatarColor = Color(0xFF9966CC)
        ),
        Message(
            "m2",
            otherUserId,
            "Hey, check out this",
            "1:30 PM",
            senderAvatarColor = Color(0xFF9966CC)
        ),
        Message(
            "m3",
            currentUser,
            "Looks great, but maybe try different font?",
            "1:35 PM",
            senderAvatarColor = Color.Black
        ),
        Message(
            "m4",
            currentUser,
            "Good point, I'll experiment.",
            "1:35 PM",
            senderAvatarColor = Color.Black
        ),
        Message(
            "m5",
            otherUserId,
            "Let's discuss it the meeting.",
            "1:35 PM",
            senderAvatarColor = Color(0xFF9966CC)
        ),
    )

    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsChatScreen(
            uiState = RoomsChatUiState(
                roomName = "Daily Design Critiques",
                messages = sampleMessages,
                currentUserId = currentUser,
                messageDraft = "I agree, a different font would"
            ),
            onEvent = {}
        )
    }
}
