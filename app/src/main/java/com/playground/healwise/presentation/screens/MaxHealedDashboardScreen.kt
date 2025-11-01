package com.playground.healwise.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.domain.model.MaxMetricCardData
import com.playground.healwise.domain.model.MaxMetricType
import com.playground.healwise.domain.model.SingleMetricData
import com.playground.healwise.domain.model.StackedMetricData
import com.playground.healwise.presentation.components.MaxAppBar
import com.playground.healwise.presentation.components.MaxMetricCard
import com.playground.healwise.presentation.components.MaxTitleBlock
import com.playground.healwise.presentation.components.SingleMetricCard
import com.playground.healwise.presentation.components.StackedMetricCard
import com.playground.healwise.presentation.theme.DarkContrast
import com.playground.healwise.presentation.theme.MainAppBackground
import com.playground.healwise.presentation.theme.MaxBlueCard
import com.playground.healwise.presentation.theme.MaxFilterTagColor
import com.playground.healwise.presentation.theme.MaxGreenCard
import com.playground.healwise.presentation.theme.MaxPinkCard
import com.playground.healwise.presentation.theme.MaxPurpleCard
import com.playground.healwise.presentation.uistate.MaxDashboardEvent
import com.playground.healwise.presentation.uistate.MaxDashboardUiState
import com.playground.healwise.presentation.uistate.MaxHealedEvent
import com.playground.healwise.presentation.uistate.MaxHealedUiState


//@Composable
//fun MaxDashboardScreen(state: MaxHealedUiState, onEvent: (MaxHealedEvent) -> Unit) {
//    Surface(
////        color = MainAppBackground
//    ) { // Main background is black
//        Column(modifier = Modifier.fillMaxSize()) {
//            MaxAppBar(
//                isVerified = state.isVerified,
//                onMenuClick = { onEvent(MaxHealedEvent.OnMenuClick) },
////                modifier = Modifier.statusBarsPadding()
//            )
//
//            // 1. Title Block (Purple Background)
//            MaxTitleBlock(title = state.title, subtitle = state.subtitle)
//
//            // 2. Main KPI Metric (On Black Background)
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 32.dp)
//            ) {
//                // Average Patient Enroll Daily Label
//                Text(
//                    state.mainMetricLabel,
//                    style = MaterialTheme.typography.bodyMedium,
//                )
//                Spacer(Modifier.height(8.dp))
//
//                // +82^ Metric
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        state.mainMetricValue.dropLast(1), // "+82"
//                        style = MaterialTheme.typography.displayLarge.copy(
//                            fontSize = 120.sp,
//                            fontWeight = FontWeight.Normal,
//                            lineHeight = 100.sp,
//                            fontFamily = FontFamily.Serif
//                        ),
//                    )
//                    // The Caret/Exponent (^)
//                    Text(
//                        state.mainMetricValue.last().toString(),
//                        style = MaterialTheme.typography.titleLarge.copy(
//                            fontSize = 48.sp,
//                            fontWeight = FontWeight.ExtraLight
//                        ),
//                        color = Color.White,
//                        modifier = Modifier.offset(y = (-40).dp) // Super-script offset
//                    )
//                }
//            }
//            Spacer(Modifier.height(32.dp))
//
//            // 3. Metric Cards Row
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                MaxMetricCard(
//                    cardData = state.patientsCard,
//                    onClick = { onEvent(MaxHealedEvent.OnPatientsCardClick) },
//                    modifier = Modifier.weight(1f)
//                )
//                MaxMetricCard(
//                    cardData = state.familiesCard,
//                    onClick = { onEvent(MaxHealedEvent.OnFamiliesCardClick) },
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//    }
//}

@Composable
fun MaxDashboardScreen(state: MaxDashboardUiState, onEvent: (MaxDashboardEvent) -> Unit) {
    Surface(color = MainAppBackground) { // Main background is black
        Column(modifier = Modifier.fillMaxSize()) {
            MaxAppBar(
                isVerified = state.isVerified,
                onMenuClick = { onEvent(MaxDashboardEvent.OnMenuClick) },
                modifier = Modifier.statusBarsPadding()
            )

            // 1. Title Block (Purple Background)
            MaxTitleBlock(title = state.title, subtitle = state.subtitle)

            // 2. Main KPI Metric (On Black Background)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                // Average Patient Enroll Daily Label
                Text(
                    state.mainMetricLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(8.dp))

                // +82^ Metric
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        state.mainMetricValue.dropLast(1), // "+82"
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 120.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 100.sp,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color.White
                    )
                    // The Caret/Exponent (^)
                    Text(
                        state.mainMetricValue.last().toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraLight
                        ),
                        color = Color.White,
                        modifier = Modifier.offset(y = (-40).dp) // Super-script offset
                    )
                }
            }
            Spacer(Modifier.height(32.dp))

            // 3. Metric Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MaxMetricCard(
                    cardData = state.patientsCard,
                    onClick = { onEvent(MaxDashboardEvent.OnPatientsCardClick) },
                    modifier = Modifier.weight(1f)
                )
                MaxMetricCard(
                    cardData = state.familiesCard,
                    onClick = { onEvent(MaxDashboardEvent.OnFamiliesCardClick) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

val MockMaxUiState = MaxDashboardUiState(
    patientsCard = MaxMetricCardData(
        type = MaxMetricType.PATIENTS_SEEN,
        icon = Icons.Default.PersonOutline,
        metricValue = "2.96M",
        description = "Patients Seen",
        subtext = "Globally—",
        backgroundColor = MaxGreenCard
    ),
    familiesCard = MaxMetricCardData(
        type = MaxMetricType.HAPPY_FAMILIES,
        icon = Icons.Default.Group,
        metricValue = "98.2%",
        description = "Happy Families",
        subtext = "5.0+ Rated",
        backgroundColor = MaxPinkCard
    )
)

// --- Mocks ---
val MockMaxHealedUiState = MaxHealedUiState(
    activeDoctorsCard = StackedMetricData(
        metricValue = "18",
        description = "Active Doctors",
        icon = Icons.Default.Add,
        backgroundColor = MaxGreenCard // Same color for both stacked cards
    ),
    hospitalsCard = StackedMetricData(
        metricValue = "6",
        description = "Hospitals",
        icon = Icons.Default.Apartment,
        backgroundColor = MaxGreenCard
    ),
    reAdmittedCard = SingleMetricData(
        metricValue = "312",
        description = "Total Re-Admitted Patients",
        icon = Icons.Default.Redo,
        backgroundColor = MaxBlueCard
    ),
    occupancyCard = SingleMetricData(
        metricValue = "35/62",
        subMetricValue = "50.4^", // Mocking the exponent with ^
        description = "Bed Occupancy Rate",
        icon = Icons.Default.TripOrigin, // Using a simple circle for the occupancy icon
        backgroundColor = MaxPurpleCard
    )
)

@Preview(showBackground = true)
@Composable
fun MaxDashboardPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = MainAppBackground,
            onBackground = Color.White
        )
    ) {
        MaxDashboardScreen(state = MockMaxUiState, onEvent = {})
    }
}