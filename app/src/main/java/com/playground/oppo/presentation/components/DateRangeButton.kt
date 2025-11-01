package com.playground.oppo.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun DateRangeButton(dateRange: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = dateRange,
        style = MaterialTheme.typography.titleMedium,
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.clickable(onClick = onClick)
    )
}
