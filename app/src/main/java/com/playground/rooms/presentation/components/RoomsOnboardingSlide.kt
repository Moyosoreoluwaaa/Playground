package com.playground.rooms.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.OnboardingSlide
import com.playground.rooms.domain.model.OnboardingSlideContent

@Composable
fun RoomsOnboardingSlide(
    content: OnboardingSlideContent,
    isLastPage: Boolean,
    onNextClicked: () -> Unit,
    onProceedClicked: () -> Unit, // New callback for final page
    onSkipClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar (Logo & Skip)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoomsAppLogo()
            TextButton(onClick = onSkipClicked) {
                Text("Skip", color = Color.Black)
            }
        }

        Spacer(Modifier.height(64.dp))

        // Headline
        Text(
            text = content.headline,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().align(Alignment.Start)
        )

        Spacer(Modifier.height(48.dp))

        // Illustration
        OnboardingIllustration(slide = content.slide)

        Spacer(Modifier.height(48.dp))

        // Body Text
        Text(
            text = content.body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.weight(1f))

        // Action Button
        Button(
            // Use the correct action based on the page status
            onClick = if (isLastPage) onProceedClicked else onNextClicked,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            val buttonText = if (isLastPage) "Proceed" else "Next"
            Text(buttonText, color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        // Page Indicator
        val pageCount = OnboardingSlide.entries.size
        val currentPageIndex = OnboardingSlide.entries.indexOf(content.slide)
        OnboardingPageIndicator(
            pageCount = pageCount,
            currentPage = currentPageIndex,
            onIndicatorClicked = { /* Handled by Pager/ViewModel */ }
        )
    }
}
