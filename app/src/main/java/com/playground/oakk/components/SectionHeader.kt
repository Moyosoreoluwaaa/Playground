package com.playground.oakk.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.oakk.OakkTheme

/**
 * A simple text header for a section.
 *
 * @param title The text to display.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun SectionHeaderPreview() {
    OakkTheme {
        SectionHeader(title = "PRODUCTS")
    }
}
