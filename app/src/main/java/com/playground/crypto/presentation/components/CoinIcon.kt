package com.playground.crypto.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playground.R

// Note: Requires the Coil dependency: "io.coil-kt:coil-compose:..."
@Composable
fun CoinIcon(
    iconUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Add a placeholder drawable
        error = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
private fun CoinIconPreview() {
    // This preview uses a placeholder since it cannot fetch a real URL.
    CoinIcon(
        iconUrl = "",
        contentDescription = "Bitcoin logo",
        modifier = Modifier.padding(16.dp)
    )
}
