package com.playground.oppo.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.presentation.components.MaxAppBar
import com.playground.oppo.domain.model.ActivityItemData
import com.playground.oppo.presentation.components.ActivityCard
import com.playground.oppo.presentation.components.AnalyticsHeader
import com.playground.oppo.presentation.components.DateRangeButton
import com.playground.oppo.presentation.uistate.OppoStatementDetailEvent
import com.playground.oppo.presentation.uistate.OppoStatementDetailUiState

// --- Screen Composable ---
@Composable
fun OppoStatementDetailScreen(
    uiState: OppoStatementDetailUiState,
    onEvent: (OppoStatementDetailEvent) -> Unit
) {
    val background = Color(0xFFF3F7F2) // Light Background from blueprint

    Scaffold(
//        topBar = { MaxAppBar(onMenuClick = { onEvent(OppoStatementDetailEvent.OnMenuClick) }) },
        containerColor = background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Earning Summary Block
            item {
                Spacer(Modifier.height(32.dp))
                Text(
                    uiState.screenTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$${"%,.0f".format(uiState.totalEarning)}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 90.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(4.dp))
                DateRangeButton(uiState.dateRange, onClick = { onEvent(OppoStatementDetailEvent.OnDateRangeClick) })
                Spacer(Modifier.height(24.dp))

                // Action Button
                Button(
                    onClick = { onEvent(OppoStatementDetailEvent.OnMyAccountClick) },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("My Account", color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                // Context Note
                Text(
                    "*Data are bases on selective range point",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = Color.Gray.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(40.dp))

                // Activity Header
                AnalyticsHeader(
                    title = uiState.sectionTitle,
                    onSettingsClick = { onEvent(OppoStatementDetailEvent.OnSettingsClick) },
                    onMaximizeClick = { onEvent(OppoStatementDetailEvent.OnMaximizeClick) }
                )
                Spacer(Modifier.height(16.dp))
            }

            // Activity List
            items(uiState.activityList) { activity ->
                ActivityCard(
                    activityData = activity,
                    onClick = { onEvent(OppoStatementDetailEvent.OnActivityClick(it)) }
                )
                Spacer(Modifier.height(16.dp))
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOppoStatementDetailScreen() {
    MaterialTheme {
        OppoStatementDetailScreen(
            uiState = OppoStatementDetailUiState.mock(),
            onEvent = {}
        )
    }
}
