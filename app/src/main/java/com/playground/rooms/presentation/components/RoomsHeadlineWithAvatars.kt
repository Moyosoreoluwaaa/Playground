package com.playground.rooms.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playground.R
import com.playground.rooms.presentation.uistate.RoomsAuthUiState

@Composable
fun RoomsHeadlineWithAvatars(
    uiState: RoomsAuthUiState,
    modifier: Modifier = Modifier
) {
    val textStyle = MaterialTheme.typography.displaySmall.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 44.sp
    )

    // Simplification: Breaking the headline into blocks for Compose layout
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "chat ",
            style = textStyle.copy(fontWeight = FontWeight.Normal)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = uiState.avatarUrls.getOrNull(0),
                contentDescription = "Avatar of person 1",
                placeholder = painterResource(id = R.drawable.avatar), // Assumed asset
                error = painterResource(id = R.drawable.broken_image), // Assumed asset
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "rooms",
                style = textStyle,
            )
        }
        Text(
            text = "with the most",
            style = textStyle.copy(fontWeight = FontWeight.Normal)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "valuable",
                style = textStyle
            )
            AsyncImage(
                model = uiState.avatarUrls.getOrNull(1),
                contentDescription = "Avatar of person 2 and 3",
                placeholder = painterResource(id = R.drawable.avatar), // Assumed asset
                error = painterResource(id = R.drawable.broken_image), // Assumed asset
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}
