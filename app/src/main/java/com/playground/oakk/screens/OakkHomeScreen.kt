package com.playground.oakk.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.oakk.components.CashbackCard
import com.playground.oakk.components.CategoryList
import com.playground.oakk.components.ColorBackground
import com.playground.oakk.components.ColorTextMuted
import com.playground.oakk.components.DashboardTabs
import com.playground.oakk.components.ReturnCards
import com.playground.oakk.components.WelcomeHeader
import com.playground.oakk.components.topCategories


@Composable
fun OakkAnalytics() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ColorBackground // Light background color from the design
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Enable scrolling for the whole column
                .padding(horizontal = 20.dp)
        ) {
            // 1. Top Bar / Welcome Header
            WelcomeHeader()

            // 2. Dashboard Tabs
            DashboardTabs()

            Spacer(Modifier.height(24.dp))

            // 3. Return Cards
            ReturnCards()

            Spacer(Modifier.height(16.dp))

            // 4. Cashback Card
            CashbackCard()

            Spacer(Modifier.height(24.dp))

            // 5. Category List Header
            Text(
                text = "TOP 4 CATEGORIES FOR CASHBACK",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 6. Category List
            CategoryList(topCategories)
            
            // Add padding to the bottom to make the last item visible above the navigation bar
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun OakkAnalyticsPreview() {
    // Using MaterialTheme as a base for standard text styles
    MaterialTheme {
        OakkAnalytics()
    }
}