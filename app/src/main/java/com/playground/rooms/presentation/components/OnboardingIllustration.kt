package com.playground.rooms.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.OnboardingSlide

@Composable
fun OnboardingIllustration(
    slide: OnboardingSlide,
    modifier: Modifier = Modifier
) {
    // Placeholder for the complex illustrations
    val color = when (slide) {
        OnboardingSlide.CONNECT -> Color(0xFF9966CC) // Purple/Connected
        OnboardingSlide.DISCOVER -> Color(0xFF3CB371) // Green/Search
        OnboardingSlide.SPACE_AWAITS -> Color(0xFF6A5ACD) // Blue/Door
    }

    val icon = when (slide) {
        OnboardingSlide.CONNECT -> Icons.Default.People // Mocking the interconnected illustration
        OnboardingSlide.DISCOVER -> Icons.Default.Search // Mocking the magnifying glass
        OnboardingSlide.SPACE_AWAITS -> Icons.Default.Home // Mocking the door/space
    }

    Box(
        modifier = modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Onboarding illustration for ${slide.name}",
            tint = color,
            modifier = Modifier.size(80.dp)
        )
    }
}
