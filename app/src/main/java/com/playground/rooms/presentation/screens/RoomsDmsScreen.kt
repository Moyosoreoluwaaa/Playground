package com.playground.rooms.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.Conversation
import com.playground.rooms.domain.model.MainNavTab
import com.playground.rooms.presentation.components.DMCard
import com.playground.rooms.presentation.components.RoomsBottomNavBar
import com.playground.rooms.presentation.components.RoomsDmsHeader
import com.playground.rooms.presentation.uistate.RoomsDmsEvent
import com.playground.rooms.presentation.uistate.RoomsDmsUiState

// --- SCREEN COMPOSABLE ---

@Composable
fun RoomsDmsScreen(
    uiState: RoomsDmsUiState,
    onEvent: (RoomsDmsEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            RoomsBottomNavBar(
                currentTab = uiState.currentTab,
                onTabSelected = { tab ->
                    onEvent(RoomsDmsEvent.TabSelected(tab))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Top Header
            RoomsDmsHeader(
                onNewMessageClicked = { onEvent(RoomsDmsEvent.NewMessageClicked) }
            )

            // Content padding starts here
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(32.dp))

                // 2. Headline
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // 3. Search Bar
                OutlinedTextField(
                    value = uiState.currentSearchQuery,
                    onValueChange = { onEvent(RoomsDmsEvent.SearchQueryChanged(it)) },
                    label = { Text("Find messages...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search messages") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(Modifier.height(16.dp))

                // 4. DM List
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.conversations.isEmpty()) {
                    Text("Your inbox is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp)
                    ) {
                        items(uiState.conversations) { conversation ->
                            DMCard(
                                conversation = conversation,
                                onClick = { onEvent(RoomsDmsEvent.ConversationSelected(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms DMS Screen")
@Composable
fun RoomsDmsScreenPreview() {
    val sampleConversations = listOf(
        Conversation(
            id = "dm1",
            title = "Anna Scott",
            lastMessage = "Hey, did you get may message?",
            timestamp = "2h",
            isUnread = true,
            avatarUrl = null,
            isGroup = false,
            avatarTint = Color(0xFF9966CC) // Purple
        ),
        Conversation(
            id = "gp1",
            title = "Group Project",
            lastMessage = "Meeting at 3 PM today.",
            timestamp = "2h",
            isUnread = false,
            avatarUrl = null,
            isGroup = true,
            avatarTint = Color(0xFF3CB371) // Green
        ),
        Conversation(
            id = "dm2",
            title = "Sam",
            lastMessage = "What time works for you?",
            timestamp = "2h",
            isUnread = false,
            avatarUrl = null,
            isGroup = false,
            avatarTint = Color(0xFF6A5ACD) // Blue
        )
    )

    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsDmsScreen(
            uiState = RoomsDmsUiState(
                conversations = sampleConversations,
                currentTab = MainNavTab.DMS // Ensure DMS is highlighted
            ),
            onEvent = {}
        )
    }
}
