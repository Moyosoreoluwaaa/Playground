package com.playground.crypto.presentation.bookish

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier

@Preview(name = "TopBar", showBackground = true)
@Composable
fun TopBarPreview() {
    MaterialTheme {
        TopBar(
            profileImageUri = "https://example.com/profile.jpg",
            userName = "Emily",
            onProfileClicked = {},
            onNotificationsClicked = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}