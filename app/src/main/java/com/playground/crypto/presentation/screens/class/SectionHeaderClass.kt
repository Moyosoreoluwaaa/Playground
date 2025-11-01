package com.playground.crypto.presentation.screens.`class`

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.ui.theme.PlaygroundTheme

//
//@Immutable
//data class DashboardUiState(
//    val user: UserProfile,
//    val searchQuery: String = "",
//    val nextClass: ClassModel? = null,
//    val events: List<EventModel> = emptyList(),
//    val selectedTab: DashboardTab = DashboardTab.Home,
//    val isLoading: Boolean = false,
//    val error: String? = null
//)

enum class DashboardTab {
    Home,
    Calendar,
    Settings,
    Messages
}
@Composable
fun SectionHeaderClass(
    title: String,
    onSeeAllClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextButton(
            onClick = onSeeAllClicked
        ) {
            Text(
                text = "See all",
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SectionHeaderClassPreview() {
    PlaygroundTheme {
        SectionHeaderClass(
            title = "Next class",
            onSeeAllClicked = {}
        )
    }
}
