package com.playground.planner.presentation.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.playground.planner.presentation.uistate.TaskDetailUiState

@Composable
fun CommentInputBar(
    uiState: TaskDetailUiState,
    onCommentTextChanged: (String) -> Unit,
    onAttachClicked: () -> Unit,
    onSendClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), // Darker background
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = uiState.newCommentText,
                onValueChange = onCommentTextChanged,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                decorationBox = { innerTextField ->
                    if (uiState.newCommentText.isEmpty()) {
                        Text("Add a comment...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    innerTextField()
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClicked() })
            )

            IconButton(onClick = onAttachClicked) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach file")
            }

            // Send button logic: only enabled if text is present
            val isSendEnabled = uiState.newCommentText.isNotBlank()
            IconButton(onClick = onSendClicked, enabled = isSendEnabled) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send comment",
                    tint = if (isSendEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.4f
                    )
                )
            }
        }
    }
}
