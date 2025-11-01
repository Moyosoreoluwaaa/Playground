package com.playground.language.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.language.presentation.uistate.LangMatchWord

@Composable
fun LangDraggableMatchNodeLeft(
    word: LangMatchWord,
    isMatched: Boolean,
    modifier: Modifier = Modifier,
) {
    val matchedColor = MaterialTheme.colorScheme.primary
    val defaultColor = MaterialTheme.colorScheme.surface
    val matchedBorder = Color(0xFF4CAF50)
    val defaultBorder = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)

    val backgroundColor by animateColorAsState(
        targetValue = if (isMatched) matchedColor else defaultColor, label = "node_bg_animation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isMatched) matchedBorder else defaultBorder,
        label = "node_border_animation"
    )

    Card(
        shape = RoundedCornerShape(LangSpacing / 2),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(if (isMatched) 2.dp else 0.dp),
        modifier = modifier
            .width(IntrinsicSize.Max)
            .border(1.dp, borderColor, RoundedCornerShape(LangSpacing / 2))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = LangSpacing, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word.text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(8.dp))
            LangMatchNodeIndicator(isMatched = isMatched)
        }
    }
}