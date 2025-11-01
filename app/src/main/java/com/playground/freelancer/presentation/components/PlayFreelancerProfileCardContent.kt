package com.playground.freelancer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.freelancer.presentation.uistate.PlayFreelancerProfileCardUiState

/**
 * REFRACTOR: Extract the content of the card so it can be used inside the progressive Card.
 */
@Composable
fun PlayFreelancerProfileCardContent(
    uiState: PlayFreelancerProfileCardUiState,
    isProgressive: Boolean = false
) {
    val contentColor =
        if (isProgressive) Color.Black else Color.Black // Keep content black for visibility

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top section: Image and Message Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Image Placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.1f))
            )

            if (uiState.hasUnreadMessages) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "Unread Messages",
                    tint = contentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = uiState.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )

        // Fills remaining space to push the bottom content down
        Spacer(modifier = Modifier.weight(1f))

        // Job Details
        Text(
            text = uiState.jobLevel.name.lowercase().replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor.copy(alpha = 0.7f)
        )
        Text(
            text = uiState.jobTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom section: Salary and Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$${uiState.salary}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
            Button(
                onClick = { /* Handle details click */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = contentColor)
            ) {
                Text("See details", color = Color.White)
            }
        }
    }
}
