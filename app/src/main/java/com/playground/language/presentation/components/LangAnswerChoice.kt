package com.playground.language.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.playground.language.domain.LangAnswer


@Composable
fun LangAnswerChoice(
    choice: LangAnswer,
    isSelected: Boolean,
    onClick: (LangAnswer) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        label = "choice_background_animation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "choice_border_animation"
    )

    Card(
        shape = RoundedCornerShape(LangSpacing / 2),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .border(1.dp, borderColor, RoundedCornerShape(LangSpacing / 2))
            .selectable(
                selected = isSelected,
                onClick = { onClick(choice) },
                role = androidx.compose.ui.semantics.Role.RadioButton
            )
    ) {
        Column(
            modifier = Modifier.padding(LangSpacing),
            verticalArrangement = Arrangement.Center
        ) {
            // Display foreign text (bold) and translation text
            val text = buildAnnotatedString {
                if (!choice.foreignText.isNullOrEmpty()) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(choice.foreignText)
                    }
                    append("\n")
                }
                append(choice.text)
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
