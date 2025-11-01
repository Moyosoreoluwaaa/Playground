package com.playground.layer.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.layer.data.source.GENERIC_DAPP_ICON_URL
import com.playground.layer.domain.model.HistoryEntry

@Composable
fun HistoryItem(
    entry: HistoryEntry,
    onClick: (HistoryEntry) -> Unit,
    modifier: Modifier = Modifier
) {

    // Logic check: Uses iconUrl if available, otherwise falls back to generic
    val imageUrl = entry.iconUrl.ifEmpty { GENERIC_DAPP_ICON_URL }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(entry) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // *** Uses AsyncImage with determined URL ***
        AsyncImage(
            model = imageUrl,
            contentDescription = "${entry.title} icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                entry.url,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate to ${entry.title}",
            tint = Color.Gray
        )
    }
    Divider(color = Color.DarkGray.copy(alpha = 0.5f))
}