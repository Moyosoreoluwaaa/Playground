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
import com.playground.rooms.domain.model.MainNavTab
import com.playground.rooms.domain.model.Room
import com.playground.rooms.presentation.components.RoomsAppBar
import com.playground.rooms.presentation.components.RoomCard
import com.playground.rooms.presentation.components.RoomsBottomNavBar
import com.playground.rooms.presentation.uistate.RoomsDashboardEvent
import com.playground.rooms.presentation.uistate.RoomsDashboardUiState

@Composable
fun RoomsDashboardScreen(
    uiState: RoomsDashboardUiState,
    onEvent: (RoomsDashboardEvent) -> Unit
) {
    Scaffold(
        topBar = {
            // Note: The screen has a custom header area, so we use the AppBar component directly
        },
        bottomBar = {
            RoomsBottomNavBar(
                currentTab = uiState.currentTab,
                onTabSelected = { onEvent(RoomsDashboardEvent.TabSelected(it)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Top Header
            RoomsAppBar(
                userProfileUrl = uiState.userProfileUrl,
                onProfileClicked = { onEvent(RoomsDashboardEvent.ProfileClicked) }
            )

            // Content padding starts here
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(32.dp))

                // 2. Headline
                Text(
                    text = "Your Rooms",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // 3. Search Bar (Using TextField for simplicity in this view)
                OutlinedTextField(
                    value = uiState.currentSearchQuery,
                    onValueChange = { onEvent(RoomsDashboardEvent.SearchQueryChanged(it)) },
                    label = { Text("Find rooms...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    // Use onClick to trigger navigation to a full search screen
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(Modifier.height(16.dp))

                // 4. Rooms List
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.rooms.isEmpty()) {
                    Text("You haven't joined any rooms yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                    ) {
                        items(uiState.rooms) { room ->
                            RoomCard(
                                room = room,
                                onClick = { onEvent(RoomsDashboardEvent.RoomSelected(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms Dashboard Screen")
@Composable
fun RoomsDashboardScreenPreview() {
    val sampleRooms = listOf(
        Room(
            id = "1",
            name = "Daily Design Critiques",
            subtitle = "Discuss UI/UX with peers",
            avatarUrl = null,
            roomColor = Color(0xFF9966CC) // Purple
        ),
        Room(
            id = "2",
            name = "Startup Founders",
            subtitle = "1.2K members",
            avatarUrl = null,
            roomColor = Color(0xFF6A5ACD) // Dark Slate Blue
        ),
        Room(
            id = "3",
            name = "Book Club",
            subtitle = "Reading 'Dune'",
            avatarUrl = null,
            roomColor = Color(0xFF3CB371) // Medium Sea Green
        )
    )

    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsDashboardScreen(
            uiState = RoomsDashboardUiState(
                rooms = sampleRooms,
                userProfileUrl = "https://i.pravatar.cc/150?img=12"
            ),
            onEvent = {}
        )
    }
}
