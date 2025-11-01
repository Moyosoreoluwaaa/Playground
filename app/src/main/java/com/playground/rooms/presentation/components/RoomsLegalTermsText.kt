package com.playground.rooms.presentation.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.rooms.domain.model.RoomsLegalEvent

@Composable
fun RoomsLegalTermsText(
    onEvent: (RoomsLegalEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        lineHeight = 16.sp
    )
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )

    val annotatedString = buildAnnotatedString {
        append("By continuing you confirm that you agree to our ")
        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(style = linkStyle) {
            append("Terms of Service")
        }
        pop()
        append(", ")
        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(style = linkStyle) {
            append("Privacy Policy")
        }
        pop()
        append(" and good behavior in chat with users (")
        pushStringAnnotation(tag = "LOVEDONES", annotation = "loved_ones")
        withStyle(style = linkStyle) {
            append("write to your loved ones more often <3")
        }
        pop()
        append(").")
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let { onEvent(RoomsLegalEvent.TermsClicked) }
            annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                .firstOrNull()?.let { onEvent(RoomsLegalEvent.PrivacyClicked) }
            annotatedString.getStringAnnotations(tag = "LOVEDONES", start = offset, end = offset)
                .firstOrNull()?.let { onEvent(RoomsLegalEvent.LovedOnesClicked) }
        },
        style = defaultStyle,
        modifier = modifier.padding(horizontal = 40.dp)
    )
}