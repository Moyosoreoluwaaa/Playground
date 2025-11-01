package com.playground.freelancer.presentation.uistate

import androidx.compose.ui.graphics.Color
import com.playground.freelancer.domain.model.*

typealias FreelancerId = String
data class FreelancerFeaturedSummary(
    val id: FreelancerId,
    val name: String,
    val title: String,
    val hourlyRate: String,
    val imageUrl: String
)
data class FreelancerSummary(
    val id: FreelancerId,
    val name: String,
    val title: String,
    val imageUrl: String
)
data class FreelancerProfileDetail(
    val id: FreelancerId,
    val name: String,
    val title: String,
    val imageUrl: String,
    val bio: String,
    val tags: List<String> = emptyList()
)
data class FreelancerRoleSelectionUiState(
    val roles: List<FreelancerRole>,
    val selectedRoleIds: Set<String>,
    val isLoading: Boolean = false,
    val isSelectAllActive: Boolean = false
)
data class FreelancerSearchUiState(
    val searchQuery: String = "",
    val totalResults: Int = 0,
    val featuredFreelancer: FreelancerFeaturedSummary? = null,
    val standardFreelancers: List<FreelancerSummary> = emptyList(),
    val isLoading: Boolean = false,
    val selectedNavTab: FreelancerSearchTabType = FreelancerSearchTabType.SEARCH,
    val status: LoadStatus = LoadStatus.LOADING,
    val searchResults: List<FreelancerSearchResult> = emptyList(),
    val errorMessage: String? = null
)
data class FreelancerDetailUiState(
    val freelancer: FreelancerProfileDetail,
    val selectedTab: FreelancerProfileTabType = FreelancerProfileTabType.RESPONSIBILITIES,
    val isBookmarked: Boolean = false,
    val isBioExpanded: Boolean = false,
    val isLoading: Boolean = false
)
data class ProfileUiState(
    val freelancer: Freelancer, // Contains all static profile data
    val selectedTab: ProfileTab = ProfileTab.RESPONSIBILITIES,
    val isBioExpanded: Boolean = false,
    val isFavorite: Boolean = false,
    val responsibilities: List<String> = emptyList(), // Content for Tab 1
    val status: LoadStatus = LoadStatus.LOADING
)

data class PlayFreelancerProfileCardUiState(
    val id: String,
    val name: String,
    val imageUrl: String,
    val jobLevel: PlayFreelancerJobLevel,
    val jobTitle: String,
    val salary: String,
    val hasUnreadMessages: Boolean = false
)

data class PlayFreelancerCardStackUiState(
    val cardQueue: List<PlayFreelancerProfileCardUiState>,
    val historyStack: List<PlayFreelancerProfileCardUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ProfileCardUiState(
    val cards: List<CardData>
) {
    data class CardData(
        val name: String,
        val roleLevel: String, // "Senior"
        val jobTitle: String, // "Hardware Engineer"
        val salary: String, // "$2400 / month"
        val imageUrl: String,
        val depth: CardDepth = CardDepth.TOP,
        val backgroundColor: Color // Custom color for the card
    )

    companion object {
        val NeonYellow = Color(0xFFE9FF00) // Custom bright color
        val DarkGrey = Color(0xFF333333)
    }
}
sealed class ProfileCardEvent {
    data class OnDetailsClick(val profileId: String) : ProfileCardEvent()
    data class OnContactClick(val profileId: String) : ProfileCardEvent()
    data class OnCardDismissed(val profileId: String, val direction: Any) : ProfileCardEvent() // 'Any' stands in for a Compose Direction enum
}

data class FreelancerSearchResult(
    val id: String,
    val name: String,
    val title: String, // e.g., "DevOps Engineer"
    val hourlyRate: Double,
    val rating: Float, // e.g., 4.9
    val avatarUrl: String,
    val isFavorite: Boolean = false
)