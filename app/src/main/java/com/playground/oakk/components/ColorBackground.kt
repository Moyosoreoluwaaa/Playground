package com.playground.oakk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Restaurant

// --- Custom Colors (Approximated from the image) ---
// Note: In a real app, these would be defined in a Theme file.
val ColorBackground = Color(0xFFEDECE9)
val ColorSurface = Color(0xFFFFFFFF)
val ColorTextDark = Color(0xFF1E1E1E)
val ColorTextLight = Color(0xFFC0C0C0)
val ColorTextMuted = Color(0xFF707070)

val ColorCard1 = Color(0xFF8B8B7B)
val ColorCard2 = Color(0xFFAD9D80)
val ColorCard3 = Color(0xFF4C586B)
val ColorCard4 = Color(0xFF383C49)

val ColorTabBarSelected = Color(0xFF1E1E1E)
val ColorTabBarUnselected = Color(0xFF707070)

// Progress bar segment colors (approximated)
val ColorProgress1 = Color(0xFF968270)
val ColorProgress2 = Color(0xFF5A4D4C)
val ColorProgress3 = Color(0xFF38383D)
val ColorProgress4 = Color(0xFF7D726D)
// ----------------------------------------------------

// Data Model
data class Category(
    val name: String,
    val amount: String, // SAR 400.20
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val topCategories = listOf(
    Category("GROCERIES", "SAR 400.20", ColorCard2, Icons.Filled.ShoppingCart),
    Category("RESTAURANTS", "SAR 210.70", ColorCard1, Icons.Filled.Restaurant),
    Category("TAXI", "SAR 120.50", ColorCard4, Icons.Filled.DirectionsCar),
    Category("SUBSCRIPTIONS", "SAR 80.10", ColorCard3, Icons.Filled.DirectionsCar),
)


// 1. Welcome Header Composable
@Composable
fun WelcomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 16.dp), // Padded down to account for the status bar and notch area
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back,",
                fontSize = 28.sp,
                color = ColorTextDark,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = "Alexandr Kosov",
                fontSize = 28.sp,
                color = ColorTextDark,
                fontWeight = FontWeight.ExtraBold
            )
        }
        // Placeholder for the single black icon (like a '1' or custom logo)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ColorTextDark),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "I", color = Color.White, fontSize = 20.sp)
        }
    }
}

// 2. Dashboard Tabs Composable
@Composable
fun DashboardTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tab function wrapper
        @Composable
        fun TabItem(text: String, isSelected: Boolean) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = if (isSelected) ColorSurface else ColorTabBarUnselected,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ColorTabBarSelected else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        TabItem("Dashboard", isSelected = false)
        TabItem("Investments", isSelected = false)
        TabItem("Analytics", isSelected = true)
    }
}

// Category List Item Composable
@Composable
fun CategoryListItem(category: Category, isTop: Boolean, isBottom: Boolean) {
    val cornerShape = when {
        isTop -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
        isBottom -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cornerShape) // Clip based on position in the list
            .background(category.color)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ColorSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = category.amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorSurface
            )
        }
        Icon(
            category.icon,
            contentDescription = category.name,
            tint = ColorSurface,
            modifier = Modifier.size(24.dp)
        )
    }
    // Add a divider if it's not the last item (no explicit divider in the design, but common)
    // Removed the divider to strictly match the design's flush blocks.
}
