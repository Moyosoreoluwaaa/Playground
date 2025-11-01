package com.playground.crypto.presentation.screens.`class`

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.ui.theme.PlaygroundTheme

// Domain Models (for preview purposes)
data class UserProfile(
    val name: String,
    val grade: String,
    val profileImageUrl: String
)
data class TeacherModel(
    val name: String,
    val profileImageUrl: String
)
data class ClassModel(
    val name: String,
    val time: String,
    val teacher: TeacherModel,
    val isHomeworkDone: Boolean
)
data class EventModel(
    val id: String,
    val name: String,
    val date: String,
    val imageUrl: String,
    val isFavorited: Boolean
)


data class ClassDashboardUiState(
    val user: UserProfile,
    val searchQuery: String = "",
    val nextClass: ClassModel? = null,
    val events: List<EventModel> = emptyList(),
    val selectedTab: DashboardTab = DashboardTab.Home
)

@Composable
fun ClassDashboardScreen(
    uiState: ClassDashboardUiState,
    onQueryChange: (String) -> Unit,
    onNotificationClicked: () -> Unit,
    onSeeAllClassesClicked: () -> Unit,
    onSeeAllEventsClicked: () -> Unit,
    onEventFavoriteClicked: (eventId: String) -> Unit,
    onTabSelected: (DashboardTab) -> Unit
) {
    Scaffold(
        bottomBar = {

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopBarClass(
                profileImageUrl = uiState.user.profileImageUrl,
                userName = uiState.user.name,
                userGrade = uiState.user.grade,
                onNotificationClicked = onNotificationClicked
            )
            SearchBarClass(
                query = uiState.searchQuery,
                onQueryChange = onQueryChange
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                // Next Class Section
                item {
                    SectionHeaderClass(
                        title = "Next class",
                        onSeeAllClicked = onSeeAllClassesClicked
                    )
                    uiState.nextClass?.let {
                        ClassCard(
                            classModel = it,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Events Section
                item {
                    SectionHeaderClass(
                        title = "Events",
                        onSeeAllClicked = onSeeAllEventsClicked,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                items(uiState.events) { event ->
                    EventCard(
                        eventModel = event,
                        onFavoriteClicked = onEventFavoriteClicked
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClassDashboardScreenPreview() {
    PlaygroundTheme {
        ClassDashboardScreen(
            uiState = ClassDashboardUiState(
                user = UserProfile(
                    name = "Erica Hawkins",
                    grade = "6th grade",
                    profileImageUrl = "https://i.pravatar.cc/150?u=a042581f4e29026704d"
                ),
                nextClass = ClassModel(
                    name = "Basic mathematics",
                    time = "Today, 08:15AM",
                    teacher = TeacherModel(name = "Jane Cooper", profileImageUrl = "https://i.pravatar.cc/150?u=a042581f4e29026704e"),
                    isHomeworkDone = true
                ),
                events = listOf(
                    EventModel(
                        id = "1",
                        name = "Comedy show",
                        date = "26 Apr, 6:30pm",
                        imageUrl = "https://images.unsplash.com/photo-1549247796-0e9f16d7a5b3?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb",
                        isFavorited = false
                    ),
                    EventModel(
                        id = "2",
                        name = "Dance evening",
                        date = "2 May, 5:40pm",
                        imageUrl = "https://images.unsplash.com/photo-1521404051012-07612f361172?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb",
                        isFavorited = true
                    ),
                    EventModel(
                        id = "3",
                        name = "Art Exhibition",
                        date = "10 May, 2:00pm",
                        imageUrl = "https://images.unsplash.com/photo-1579227181515-3b0f4f9b8702?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb",
                        isFavorited = false
                    )
                )
            ),
            onQueryChange = {},
            onNotificationClicked = {},
            onSeeAllClassesClicked = {},
            onSeeAllEventsClicked = {},
            onEventFavoriteClicked = {},
            onTabSelected = {}
        )
    }
}
