package com.playground.visualizer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.visualizer.domain.model.CuePoint

@Composable
fun AudioCuePoint(
    cuePoint: CuePoint,
    onTap: (CuePoint) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onTap(cuePoint) },
        modifier = modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.extraLarge),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = cuePoint.id.toString(),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}
