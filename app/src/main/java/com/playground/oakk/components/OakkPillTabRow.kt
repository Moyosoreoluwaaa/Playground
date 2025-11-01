package com.playground.oakk.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playground.oakk.uistates.OakkAssetCategory
import com.playground.oakk.uistates.OakkMonthData
import com.playground.oakk.uistates.TabType
import com.playground.oakk.viewmodels.OakkDashboardViewModel
import java.nio.file.WatchEvent

@Composable
fun OakkPillTabRow(
    selectedTab: TabType,
    onTabSelected: (TabType) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(TabType.DASHBOARD, TabType.INVESTMENTS, TabType.ANALYTICS)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            TextButton(
                onClick = { onTabSelected(tab) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent)
            ) {
                Text(
                    text = tab.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun OakkTotalNetWorthCardWithBarChart(
    netWorth: String,
    changeText: String,
    monthlyData: List<OakkMonthData>,
    modifier: Modifier = Modifier,
    viewModel: OakkDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {

        Column(Modifier.padding(24.dp)) {
            Text(
                text = "TOTAL NET WORTH IN ASSETS",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = netWorth,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif // Assuming this is from AppTypography
                )
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = changeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        OakkSegmentedBarChart(monthlyData = uiState.monthlyData)
    }
}

@Composable
fun OakkSegmentedBarChart(
    monthlyData: List<OakkMonthData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(24.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .semantics { contentDescription = "Monthly asset growth bar chart" }
            ) {
                val barWidth = 24.dp.toPx()
                val gap = (size.width - (monthlyData.size * barWidth)) / (monthlyData.size - 1)
                monthlyData.forEachIndexed { monthIndex, monthData ->
                    var currentY = size.height
                    val barX = monthIndex * (barWidth + gap)
                    monthData.segments.forEach { segment ->
                        val segmentHeight = (segment.value / 100) * size.height // Scale height
                        currentY -= segmentHeight.toFloat()
                        drawRect(
                            color = segment.color,
                            topLeft = Offset(barX, currentY),
                            size = Size(barWidth, segmentHeight.toFloat())
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                monthlyData.forEach { monthData ->
                    Text(
                        text = monthData.month,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

//@Composable
//fun OakkAssetDistributionCard(
//    categories: List<OakkAssetCategory>,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//    ) {
//        Column(Modifier.padding(24.dp)) {
//            Text(
//                text = "DISTRIBUTION OF ASSETS",
//                style = MaterialTheme.typography.labelMedium
//            )
//            Spacer(Modifier.height(16.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                categories.chunked(3).forEach { rowCategories ->
//                    Column(
//                        modifier = Modifier.weight(1f),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        rowCategories.forEach { category ->
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(8.dp)
//                                        .clip(RoundedCornerShape(4.dp))
//                                        .background(category.color)
//                                        .semantics { contentDescription = "${category.name} color swatch" }
//                                )
//                                Spacer(Modifier.width(8.dp))
//                                Text(
//                                    text = category.name,
//                                    style = MaterialTheme.typography.bodySmall
//                                )
//                                Spacer(Modifier.width(4.dp))
//                                Text(
//                                    text = category.percentage,
//                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}