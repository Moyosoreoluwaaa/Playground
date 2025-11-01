package com.playground.oakk.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// 6. Category List Composable
@Composable
fun CategoryList(categories: List<Category>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        categories.forEachIndexed { index, category ->
            CategoryListItem(
                category = category,
                isTop = index == 0, // Apply top corner to the first item
                isBottom = index == categories.lastIndex // Apply bottom corner to the last item
            )
        }
    }
}
