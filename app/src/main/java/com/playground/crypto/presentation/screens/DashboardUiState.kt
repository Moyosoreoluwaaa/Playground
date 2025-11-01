package com.playground.crypto.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material3.darkColorScheme

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userFirstName: String = "Alexa",
    val userAvatarUrl: String = "https://via.placeholder.com/150",
    val chatHistory: List<ChatHistoryItem> = emptyList(),
    val exploreItems: List<ExploreItem> = emptyList(),
    val promptLibraryChips: List<PromptChipItem> = emptyList(),
    val selectedPromptChip: PromptChipItem? = null
)

data class ChatHistoryItem(
    val id: String,
    val title: String,
    val lastMessagePreview: String,
)

data class ExploreItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

data class PromptChipItem(
    val id: String,
    val text: String,
    val isSelected: Boolean
)

@Composable
fun MindMate(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "App Logo",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Mindmate",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onNewChatClick: () -> Unit,
    onChatHistoryClick: (chatId: String) -> Unit,
    onPromptChipClick: (promptId: String) -> Unit,
    onNavItemSelected: (item: NavItemType) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavItemSelected(NavItemType.HOME) }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = { onNavItemSelected(NavItemType.VOICE) }) {
                        Icon(Icons.Default.Search, contentDescription = "Voice")
                    }
                    IconButton(onClick = { onNavItemSelected(NavItemType.HISTORY) }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween ) {
                    Column {
                        Text(
                            text = "Good morning,",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = state.userFirstName,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = state.userAvatarUrl,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onNewChatClick,
                    modifier = Modifier.height(56.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Chat",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "New chat", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Chat History Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Chat history",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (state.chatHistory.isEmpty()) {
                    Text(
                        text = "No chat history found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.chatHistory) { item ->
                            ElevatedCard(
                                onClick = { onChatHistoryClick(item.id) },
                                modifier = Modifier.width(200.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = item.lastMessagePreview,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                }
                            }
                        }
                    }
                }
            }

            // Explore More Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(){
                    Text(
                        text = "Explore more",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Open chat"
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.exploreItems) { item ->
                        FeatureCard(item = item)
                    }
                }
            }

            // Prompt Library Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Prompt library",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.promptLibraryChips.forEach { chip ->
                        PromptChip(
                            text = chip.text,
                            isSelected = chip.isSelected,
                            onClick = { onPromptChipClick(chip.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text = text) },
        modifier = modifier
    )
}

@Composable
fun FeatureCard(item: ExploreItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(180.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

enum class NavItemType {
    HOME, VOICE, HISTORY
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF1E272E),
            onBackground = Color(0xFFFFFFFF),
            surface = Color(0xFF1E272E),
            onSurface = Color(0xFFB0D9C3),
            primary = Color(0xFF4CAF50),
            onPrimary = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFF2F3C47),
            onSurfaceVariant = Color(0xFFCFD8DC),
            secondary = Color(0xFF00796B),
            tertiary = Color(0xFF00B0FF),
        )
    ) {
        val mockState = DashboardUiState(
            userFirstName = "Alexa",
            userAvatarUrl = "https://via.placeholder.com/150",
            chatHistory = listOf(
                ChatHistoryItem(
                    id = "1",
                    title = "Job Finder UX",
                    lastMessagePreview = "I’ve implemented a user-friendly job search UI..."
                ),
                ChatHistoryItem(
                    id = "2",
                    title = "Marketing Strategy",
                    lastMessagePreview = "Could you suggest some strategies for..."
                ),
            ),
            exploreItems = listOf(
                ExploreItem(
                    id = "1",
                    title = "Business",
                    description = "AI writing function with advanced input for...",
                    icon = Icons.Default.Favorite
                ),
                ExploreItem(
                    id = "2",
                    title = "Interviewing",
                    description = "AI writing function with advanced input for...",
                    icon = Icons.Default.Star
                )
            ),
            promptLibraryChips = listOf(
                PromptChipItem(id = "1", text = "Sales", isSelected = false),
                PromptChipItem(id = "2", text = "SEO", isSelected = false),
                PromptChipItem(id = "3", text = "Midjourney", isSelected = true),
                PromptChipItem(id = "4", text = "Marketing", isSelected = false),
                PromptChipItem(id = "5", text = "Design", isSelected = false),
                PromptChipItem(id = "6", text = "Email", isSelected = false),
                PromptChipItem(id = "7", text = "Research", isSelected = false),
                PromptChipItem(id = "8", text = "Summarize", isSelected = false),
                PromptChipItem(id = "9", text = "Code", isSelected = false),
                PromptChipItem(id = "10", text = "Poem", isSelected = false),
            )
        )
        DashboardScreen(
            state = mockState,
            onNewChatClick = { /* no-op */ },
            onChatHistoryClick = { /* no-op */ },
            onPromptChipClick = { /* no-op */ },
            onNavItemSelected = { /* no-op */ }
        )
    }
}
