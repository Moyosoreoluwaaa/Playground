package com.playground.freelancer.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.freelancer.domain.model.ProfileTab
import com.playground.freelancer.presentation.uistate.ProfileUiState
import com.playground.freelancer.presentation.components.InfoPill
import com.playground.freelancer.presentation.components.NumberedListItem
import com.playground.freelancer.presentation.components.SegmentedTabChips

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onReadMoreClick: () -> Unit,
    onTabSelected: (ProfileTab) -> Unit,
    onSendMessageClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { ProfileTab.entries.size },
        initialPage = ProfileTab.entries.indexOf(uiState.selectedTab)
    )

    // Effect to handle external state change (tab click) updating the pager
    LaunchedEffect(uiState.selectedTab) {
        pagerState.animateScrollToPage(ProfileTab.entries.indexOf(uiState.selectedTab))
    }
    // Effect to handle internal pager change (swipe) updating the state
    LaunchedEffect(pagerState.currentPage) {
        onTabSelected(ProfileTab.entries[pagerState.currentPage])
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = onSendMessageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC7FF00), // Custom bright yellow
                    contentColor = Color.Black // Explicitly Black for contrast
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Send Message",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background // Screen background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Header/Curved Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFC7FF00)) // Accent background
                        .height(220.dp)
                ) {
                    // Back Button (Top Left)
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.Black
                        )
                    }

                    // Favorite Button (Top Right)
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Toggle favorite",
                            tint = if (uiState.isFavorite) Color.Red else Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Profile Details
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar (Placeholder)
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            uiState.freelancer.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        Text(
                            uiState.freelancer.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            item {
                // Main Content Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-32).dp) // Overlap the curved header
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color(0xFF1E1E1E)) // Dark Card Background
                        .padding(bottom = 16.dp)
                ) {
                    // Quick Info Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoPill(Icons.Default.LocationOn, uiState.freelancer.location)
                        InfoPill(Icons.Default.AccessTime, uiState.freelancer.yearsExperience)
                        InfoPill(Icons.Default.Work, uiState.freelancer.contractType)
                    }

                    // Biography Card
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        val bioText =
                            if (uiState.isBioExpanded) uiState.freelancer.fullBio else uiState.freelancer.shortBio
                        Text(
                            text = bioText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.animateContentSize() // Animate expansion
                        )
                        if (!uiState.isBioExpanded) {
                            TextButton(onClick = onReadMoreClick) {
                                Text("Read more", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            item {
                // Tab Control Bar
                SegmentedTabChips(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected
                )

                Spacer(Modifier.height(16.dp))
            }

            // Tab Content - MUST BE INSIDE LazyColumn (or Pager's height set)
            // For a minimal runnable preview, we use a fixed-height Pager inside the LazyColumn
            item {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Fixed height for scrolling context
                        .background(Color(0xFF1E1E1E)) // Match card background
                ) { page ->
                    when (ProfileTab.entries[page]) {
                        ProfileTab.RESPONSIBILITIES -> {
                            Column {
                                uiState.responsibilities.forEachIndexed { index, text ->
                                    NumberedListItem(number = index + 1, text = text)
                                }
                            }
                        }

                        ProfileTab.EXPERIENCE -> Text(
                            "Experience details here...",
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        ProfileTab.EDUCATION -> Text(
                            "Education details here...",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFC7FF00), // Bright Yellow/Green
            onPrimary = Color.Black,
            surface = Color(0xFF121212),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF333333)
        )
    ) {
        // ProfileScreen(
        //     uiState = ProfileUiState(
        //         isFavorite = true,
        //         selectedTab = ProfileTab.RESPONSIBILITIES
        //     ),
        //     onBackClick = {},
        //     onFavoriteToggle = {},
        //     onReadMoreClick = {},
        //     onTabSelected = {},
        //     onSendMessageClick = {}
        // )
    }
}
