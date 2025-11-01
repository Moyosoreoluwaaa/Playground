package com.playground.rooms.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme.colorScheme
import com.playground.rooms.domain.model.HandleValidationState

@Composable
fun RoomsHandleInput(
    handle: String,
    validationState: HandleValidationState,
    onHandleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isError = validationState in listOf(
        HandleValidationState.INVALID_LENGTH,
        HandleValidationState.INVALID_CHARS,
        HandleValidationState.UNAVAILABLE
    )

    val helperText: String = when (validationState) {
        HandleValidationState.IDLE -> "This is how people will find and mention you."
        HandleValidationState.LOADING_CHECK -> "Checking availability..."
        HandleValidationState.VALID -> "Handle is available!"
        HandleValidationState.INVALID_LENGTH -> "Handle must be 3-15 characters long."
        HandleValidationState.INVALID_CHARS -> "Only letters, numbers, and underscores are allowed."
        HandleValidationState.UNAVAILABLE -> "That handle is already taken."
    }

    val helperColor = when (validationState) {
        HandleValidationState.VALID -> colorScheme.primary
        HandleValidationState.IDLE, HandleValidationState.LOADING_CHECK -> colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        else -> colorScheme.error
    }

    OutlinedTextField(
        value = handle,
        onValueChange = onHandleChange,
        label = { Text("Username or handle") },
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        shape = RoundedCornerShape(20) ,
        trailingIcon = {
            if (validationState == HandleValidationState.LOADING_CHECK) {
                CircularProgressIndicator(Modifier.size(24.dp))
            } else if (validationState == HandleValidationState.VALID) {
                Icon(Icons.Filled.Check, contentDescription = "Handle is valid", tint = colorScheme.primary)
            } else if (isError) {
                Icon(Icons.Filled.Warning, contentDescription = "Input error", tint = colorScheme.error)
            }
        },
        modifier = modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(4.dp))

    Text(
        text = helperText,
        color = helperColor,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    )
}
