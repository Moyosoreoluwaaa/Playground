package com.playground.screens.presentation.uistate

import androidx.compose.ui.graphics.vector.ImageVector
import com.playground.screens.domain.model.ClassModel
import com.playground.screens.domain.model.DashboardTab
import com.playground.screens.domain.model.EventModel
import com.playground.screens.domain.model.NavItemType
import com.playground.screens.domain.model.UserProfile

data class ChatUiState(
    val isLoading: Boolean = false,
    val title: String = "New Chat",
    val botAvatarUrl: String = "https://via.placeholder.com/150",
    val chatMessages: List<ChatMessage> = emptyList(),
    val isBotGenerating: Boolean = false
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean
)

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

data class WelcomeUiState(
    val title: String = "Explore infinite capabilities of writing",
    val features: List<FeatureItem> = listOf(
        FeatureItem("Remembers what user said earlier in the conversation", null), // Icons.Default.Info
        FeatureItem("Allows user to provide follow-up corrections", null), // Icons.Default.List
        FeatureItem("Trained to decline inappropriate requests", null) // Icons.Default.Warning
    )
)

data class FeatureItem(
    val description: String,
    val icon: ImageVector?
)

data class ClassDashboardUiState(
    val user: UserProfile,
    val searchQuery: String = "",
    val nextClass: ClassModel? = null,
    val events: List<EventModel> = emptyList(),
    val selectedTab: DashboardTab = DashboardTab.Home
)
