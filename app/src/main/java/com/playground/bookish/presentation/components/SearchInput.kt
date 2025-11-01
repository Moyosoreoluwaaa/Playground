package com.playground.bookish.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun SearchInput(
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // This is more of a button than a true input field for this design
    OutlinedTextField(
        value = "",
        onValueChange = { /* No-op, it's a clickable element */ },
        readOnly = true, // Prevents keyboard from showing
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        placeholder = {
            Text("Find your personal book")
        },
        shape = CircleShape,
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Search for a book" }
            .clickable { onSearchClicked() } // Make the whole field clickable
    )
}
