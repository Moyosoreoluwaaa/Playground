package com.playground.healwise.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MaxTitleBlock(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
//            .background(MaxPurpleBlock)
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        // Concatenate title and subtitle into one large block of text
        Text(
            text = "$title\n$subtitle",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 60.sp, // Explicitly large
                lineHeight = 50.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif // Simulating the elegant serif font
            ),
            color = Color.Black // Dark text on light purple
        )
    }
}

