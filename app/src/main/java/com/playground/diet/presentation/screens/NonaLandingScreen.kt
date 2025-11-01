package com.playground.diet.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.diet.domain.model.NonaFeature
import com.playground.diet.domain.model.NonaFeatureType
import com.playground.diet.presentation.components.NonaBottomNavBar
import com.playground.diet.presentation.components.NonaCalculateButton
import com.playground.diet.presentation.components.NonaFeatureCard
import com.playground.diet.presentation.components.NonaTextLink
import com.playground.diet.presentation.components.NonaTopBar
import com.playground.diet.presentation.uistate.NonaInteractionEvent
import com.playground.diet.presentation.uistate.NonaUiState
import com.playground.diet.presentation.theme.NonaFeatureColorGain
import com.playground.diet.presentation.theme.NonaFeatureColorLoss
import com.playground.diet.presentation.theme.NonaFeatureColorMaintain

// --- SCREEN ---

@Composable
fun NonaLandingScreen(
    state: NonaUiState,
    onEvent: (NonaInteractionEvent) -> Unit
) {
    Scaffold(
        topBar = {
            NonaTopBar(
                onLoginClick = { onEvent(NonaInteractionEvent.OnLoginClick) },
                onMenuClick = { onEvent(NonaInteractionEvent.OnMenuClick) }
            )
        },
        bottomBar = {
            NonaBottomNavBar(
                selectedRoute = state.selectedNavRoute,
                notificationCount = state.notificationCount,
                onNavigate = { onEvent(NonaInteractionEvent.OnNavSelected(it)) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Hero/Headline Section
            Text(
                text = "Best Healthy Diet\nFor You.",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Normal,
                    lineHeight = 44.sp,
                    fontFamily = FontFamily.Serif // Simulating the classic font
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Click below and we will find the best plan based on your caloric needs",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(48.dp))

            // Action Section
            NonaCalculateButton(
                onClick = { onEvent(NonaInteractionEvent.OnCalculateClick) }
            )
            NonaTextLink(
                text = "Talk to a dietician",
                onClick = { onEvent(NonaInteractionEvent.OnDieticianLinkClick) },
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(Modifier.height(48.dp))

            // Features Section
            Text(
                text = "Features",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Distributes the cards evenly
            ) {
                state.features.forEach { feature ->
                    NonaFeatureCard(
                        feature = feature,
                        onClick = { onEvent(NonaInteractionEvent.OnFeatureSelected(it)) },
                    )
                }
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true)
@Composable
fun NonaLandingScreenPreview() {
    val mockState = NonaUiState(
        features = listOf(
            NonaFeature(
                "Weight Loss",
                Icons.Default.Spa,
                NonaFeatureColorLoss,
                NonaFeatureType.WEIGHT_LOSS
            ),
            NonaFeature(
                "Maintain",
                Icons.Default.Balance,
                NonaFeatureColorMaintain,
                NonaFeatureType.MAINTAIN
            ),
            NonaFeature(
                "Gain",
                Icons.Default.MonitorWeight,
                NonaFeatureColorGain,
                NonaFeatureType.GAIN
            )
        ),
        notificationCount = 2
    )

    // Using a minimal custom theme to simulate the black/white aesthetic
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
        ),
        typography = Typography(
            displaySmall = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 40.sp
            ),
            titleLarge = TextStyle(fontWeight = FontWeight.Bold),
            bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
            labelLarge = TextStyle(fontWeight = FontWeight.SemiBold)
        )
    ) {
        NonaLandingScreen(state = mockState, onEvent = { /* NO-OP */ })
    }
}
