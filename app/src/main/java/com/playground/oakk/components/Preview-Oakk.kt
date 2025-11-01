package com.playground.oakk.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.playground.oakk.OakkTheme
import com.playground.oakk.uistates.OakkAssetCategory
import com.playground.oakk.uistates.OakkMonthData
import com.playground.oakk.uistates.OakkSegment
import com.playground.oakk.uistates.TabType

// Assuming OakkTheme and other Oakk components are defined elsewhere and accessible

// Preview for OakkPillTabRow
@Preview(showBackground = true, name = "Pill Tab Row Preview")
@Composable
fun OakkPillTabRowPreview() {
    OakkTheme {
        OakkPillTabRow(
            selectedTab = TabType.INVESTMENTS,
            onTabSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

// Preview for OakkTotalNetWorthCard
@Preview(showBackground = true, name = "Total Net Worth Card Preview")
@Composable
fun OakkTotalNetWorthCardPreview() {
    OakkTheme {
        val mockData = listOf(
            OakkMonthData(
                "JAN",
                listOf(OakkSegment("A", 20.0, Color.Red), OakkSegment("B", 80.0, Color.Blue))
            ),
            OakkMonthData(
                "FEB",
                listOf(OakkSegment("A", 50.0, Color.Red), OakkSegment("B", 50.0, Color.Blue))
            ),
            OakkMonthData(
                "MAR",
                listOf(OakkSegment("A", 30.0, Color.Red), OakkSegment("B", 70.0, Color.Blue))
            ),
        )
            OakkTotalNetWorthCardWithBarChart(
            netWorth = "SAR 600,200.88",
            changeText = "Going up 13% this month, good job",
            modifier = Modifier.padding(16.dp),
                monthlyData = mockData
        )
    }
}

// Preview for OakkSegmentedBarChart
@Preview(showBackground = true, name = "Segmented Bar Chart Preview")
@Composable
fun OakkSegmentedBarChartPreview() {
    val mockData = listOf(
        OakkMonthData(
            "JAN",
            listOf(OakkSegment("A", 20.0, Color.Red), OakkSegment("B", 80.0, Color.Blue))
        ),
        OakkMonthData(
            "FEB",
            listOf(OakkSegment("A", 50.0, Color.Red), OakkSegment("B", 50.0, Color.Blue))
        ),
        OakkMonthData(
            "MAR",
            listOf(OakkSegment("A", 30.0, Color.Red), OakkSegment("B", 70.0, Color.Blue))
        ),
    )
    OakkTheme {
        OakkSegmentedBarChart(
            monthlyData = mockData,
            modifier = Modifier.padding(16.dp)
        )
    }
}



// Updated Preview with the new component
@Preview(showBackground = true, name = "Asset Distribution Card Preview")
@Composable
fun OakkAssetDistributionCardPreview() {
    val mockCategories = listOf(
        OakkAssetCategory("Real Estate", "42%", Color(0xFF6A605A)),
        OakkAssetCategory("Accounts", "16%", Color(0xFFB1A7A0)),
        OakkAssetCategory("Cars", "15%", Color(0xFF566E44)),
        OakkAssetCategory("Crypto", "14%", Color(0xFFD6C8C1)),
        OakkAssetCategory("Stocks", "8%", Color(0xFF908E88)),
        OakkAssetCategory("Others", "3%", Color(0xFFDB8053))
    )
    OakkTheme {
        OakkAssetDistributionCard(
            categories = mockCategories,
            modifier = Modifier.padding(16.dp)
        )
    }
}

//
//// Preview for OakkAssetDistributionCard
//@Preview(showBackground = true, name = "Asset Distribution Card Preview")
//@Composable
//fun OakkAssetDistributionCardPreview() {
//    val mockCategories = listOf(
//        OakkAssetCategory("Real Estate", "42%", Color(0xFF6A605A)),
//        OakkAssetCategory("Accounts", "16%", Color(0xFFB1A7A0)),
//        OakkAssetCategory("Cars", "15%", Color(0xFF566E44)),
//        OakkAssetCategory("Crypto", "14%", Color(0xFFD6C8C1)),
//        OakkAssetCategory("Stocks", "8%", Color(0xFF908E88)),
//        OakkAssetCategory("Others", "3%", Color(0xFFDB8053))
//    )
//    OakkTheme {
//        OakkAssetDistributionCard(
//            categories = mockCategories,
//            modifier = Modifier.padding(16.dp)
//        )
//    }
//}