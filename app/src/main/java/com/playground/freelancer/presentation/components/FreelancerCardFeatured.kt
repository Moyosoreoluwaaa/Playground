package com.playground.freelancer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.R
import com.playground.freelancer.presentation.uistate.FreelancerFeaturedSummary
import com.playground.freelancer.presentation.uistate.FreelancerId
import com.playground.freelancer.domain.model.FreelancerSearchTabType

// Freelancer Card Featured Component
@Composable
fun FreelancerCardFeatured(
    freelancer: FreelancerFeaturedSummary,
    onDetailsClicked: (FreelancerId) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary, // Lime Green
            contentColor = MaterialTheme.colorScheme.onPrimary // Dark text
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Image and Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Using AsyncImage for remote image (Coil)
                AsyncImage(
                    model = freelancer.imageUrl,
                    contentDescription = stringResource(id = R.string.cd_freelancer_profile_picture, freelancer.name),
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White), // Placeholder/Error background
                    contentScale = ContentScale.Crop,
                    // Note: Placeholder and Error fallbacks are implied by global instruction
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = freelancer.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Middle Section: Role and Rate
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Senior",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
                Text(
                    text = freelancer.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = freelancer.hourlyRate,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Bottom Section: Action Button
            Button(
                onClick = { onDetailsClicked(freelancer.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Dark background button
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("See details", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


// Freelancer App Bottom Bar Component
@Composable
fun FreelancerAppBottomBar(
    selectedTab: FreelancerSearchTabType,
    onNavItemSelected: (FreelancerSearchTabType) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    // Custom Search Bar area
    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column {
            // Note: The search bar is visually part of the bottom navigation section in the image
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Implementing the "Search" input field (Styled to look like a pill)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.background)
                        .height(48.dp)
                        .clickable(onClick = { /* Focus/Open search */ }) // Make the whole box clickable
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Using Text for simple non-functional search preview/placeholder look
                    // In production, this would be a BasicTextField
                    Text(
                        text = if (searchQuery.isEmpty()) "Search" else searchQuery,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(Modifier.width(16.dp))

                // Profile Icon (Account Icon)
                IconButton(onClick = { onNavItemSelected(FreelancerSearchTabType.PROFILE) }) {
                    Icon(
                        imageVector = if (selectedTab == FreelancerSearchTabType.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = stringResource(id = R.string.cd_profile_tab),
                        tint = if (selectedTab == FreelancerSearchTabType.PROFILE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
