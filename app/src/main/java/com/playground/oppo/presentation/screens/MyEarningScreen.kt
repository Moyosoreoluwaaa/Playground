package com.playground.oppo.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.presentation.components.MaxAppBar
import com.playground.oppo.domain.model.EarningSource
import com.playground.oppo.domain.model.MyEarningSourceType
import com.playground.oppo.presentation.components.EarningSourceCard
import com.playground.oppo.presentation.components.PrimaryActionButton
import com.playground.oppo.presentation.uistate.MyEarningEvent
import com.playground.oppo.presentation.uistate.MyEarningUiState

// --- Screen Composable ---
@Composable
fun MyEarningScreen(
    uiState: MyEarningUiState,
    onEvent: (MyEarningEvent) -> Unit
) {
    val background = Color(0xFFF3F7F2) // Light Background from blueprint

    Scaffold(
//        topBar = { MaxAppBar(onMenuClick = { onEvent(MyEarningEvent.OnMenuClick) }) },
        containerColor = background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Earning Summary Block
            Text(
                "My Earning",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$${"%,.0f".format(uiState.totalEarnings)}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 96.sp, // Custom size for "Main Metrics"
                    fontWeight = FontWeight.Light, // Serif-like Light weight
                    lineHeight = 96.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = uiState.dateRangeText,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clickable { onEvent(MyEarningEvent.OnDateRangeClick) }
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(32.dp))

            // Primary Action
            PrimaryActionButton(
                text = "My Account",
                onClick = { onEvent(MyEarningEvent.OnMyAccountClick) }
            )

            Spacer(Modifier.height(16.dp))

            // Disclaimer
            Text(
                uiState.disclaimer,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Earning Sources List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.sources) { source ->
                    EarningSourceCard(
                        source = source,
                        onClick = { onEvent(MyEarningEvent.OnSourceClick(source.id)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyEarningScreen() {
    // Mock UiState
    val mockSources = listOf(
        EarningSource(
            id = "org",
            type = MyEarningSourceType.ORGANIC,
            title = "Organic",
            iconColor = Color.White,
            backgroundColor = Color(0xFFFF7B00),
            percentageChange = 56.50,
            amount = 25520.0,
            timestamp = "4 Mins Ago",
            isPositiveChange = true
        ),
        EarningSource(
            id = "social",
            type = MyEarningSourceType.SOCIAL_ADS,
            title = "Social Ads",
            iconColor = Color.White,
            backgroundColor = Color(0xFFAEEA00),
            percentageChange = 43.50,
            amount = 20150.0,
            timestamp = "2 Mins Ago",
            isPositiveChange = true
        )
    )

    val mockUiState = MyEarningUiState(
        totalEarnings = 45670.0,
        dateRangeText = "28 May – 04 June",
        sources = mockSources,
        disclaimer = "*Data are bases on selective range point"
    )

    MaterialTheme { // Use Material 3 Theme for M3 colors
        MyEarningScreen(
            uiState = mockUiState,
            onEvent = {} // No-op for preview
        )
    }
}
