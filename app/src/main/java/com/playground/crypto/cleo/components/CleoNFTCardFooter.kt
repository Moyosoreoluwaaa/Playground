package com.playground.crypto.cleo.components

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true, name = "CleoNFTCardFooter")
@Composable
fun CleoNFTCardFooterPreview() {
    val AccentColor = Color(0xFF90A4AE)
    MaterialTheme(
        colorScheme = darkColorScheme(
            onSurface = Color.White,
            onSurfaceVariant = Color.LightGray.copy(alpha = 0.7f),
            primary = AccentColor,
            onPrimary = Color.White
        )
    ) {
        // Background to simulate the card's translucent overlay
        Column(modifier = Modifier.background(Color.Black.copy(alpha = 0.8f))) {
            CleoNFTCardFooter(
                remainingTime = "23h : 12m : 34s",
                currentPrice = "0.288 ETH",
                onQuickActionClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Footer - Light Theme")
@Composable
fun CleoNFTCardFooterLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            surfaceContainer = Color(0xFFEFEFEF), // Example M3 light container color
            onSurface = Color.Black,
            onSurfaceVariant = Color.DarkGray
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White), // Card/Screen Background
            contentAlignment = Alignment.Center
        ) {
            CleoNFTCardFooter(
                remainingTime = "23h : 12m : 34s",
                currentPrice = "0.288 ETH",
                onQuickActionClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Footer - Dark Theme")
@Composable
fun CleoNFTCardFooterDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            surfaceContainer = Color(0xFF2B2B2B), // Example M3 dark container color
            onSurface = Color.White,
            onSurfaceVariant = Color.LightGray
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Black), // Card/Screen Background
            contentAlignment = Alignment.Center
        ) {
            CleoNFTCardFooter(
                remainingTime = "23h : 12m : 34s",
                currentPrice = "0.288 ETH",
                onQuickActionClick = {},
            )
        }
    }
}

@Composable
fun CleoNFTCardFooter(
    remainingTime: String,
    currentPrice: String,
    onQuickActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine theme-adaptive colors using M3 tokens
    val adaptiveBackgroundColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f) // Translucent surface
    val mainTextColor = MaterialTheme.colorScheme.onSurface // Adapts to high contrast
    val labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant // Adapts to low contrast

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(adaptiveBackgroundColor) // Theme-adaptive translucent background
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Remaining time",
                    style = MaterialTheme.typography.bodySmall,
                    color = labelTextColor // Use variant for secondary text
                )
                Text(
                    remainingTime,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = mainTextColor // Use onSurface for primary data
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Current Price",
                    style = MaterialTheme.typography.bodySmall,
                    color = labelTextColor
                )
                Text(
                    currentPrice,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = mainTextColor
                )
            }
            Spacer(Modifier.width(16.dp))
            // FAB colors are reversed for maximum contrast on the translucent background
            FloatingActionButton(
                onClick = onQuickActionClick,
                modifier = Modifier.size(48.dp),
                // FAB Container: Use the color that has high contrast on the background
                containerColor = mainTextColor.copy(alpha = 0.9f),
                // FAB Content: Use the color that has high contrast on the container (usually surface)
                contentColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Quick buy/trade")
            }
        }
    }
}


