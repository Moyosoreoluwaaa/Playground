package com.playground.planner.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.playground.planner.domain.model.PlannerViewType

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlannerTaskViewTabs(
    currentView: PlannerViewType,
    onTabSelected: (PlannerViewType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Increased padding: 16.dp horizontal, 12.dp vertical
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PlannerViewType.entries.forEach { viewType ->
                val isSelected = currentView == viewType
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        Modifier.clickable { onTabSelected(viewType) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ... Tab Icon and Text UI (Unchanged) ...
                        when (viewType) {
                            PlannerViewType.LIST -> Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            PlannerViewType.BOARD -> Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                            PlannerViewType.TIMELINE -> Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                            PlannerViewType.CALENDAR -> Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            viewType.name.replaceFirstChar { it.titlecase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Underline indicator
                    Spacer(Modifier.height(4.dp))
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(70.dp)
                                .clip(RoundedCornerShape(100))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        // Full width horizontal divider below the tabs
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    } else {
                        Spacer(Modifier.height(2.dp).width(50.dp))
                    }
                }
            }
        }

        // Full width horizontal divider below the tabs
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    }
}
