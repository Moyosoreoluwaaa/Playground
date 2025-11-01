package com.playground.oakk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 4. Cashback Card Composable
@Composable
fun CashbackCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CASHBACK",
                fontSize = 12.sp,
                color = ColorTextMuted
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "1,000.00",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorTextDark
                )
                Text(
                    text = " points",
                    fontSize = 14.sp,
                    color = ColorTextDark,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress of the cashback received",
                    fontSize = 12.sp,
                    color = ColorTextMuted
                )
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Info",
                    tint = ColorTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))

            // Segmented Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
//                    .clip(RoundedCornerShape(3.dp))
            ) {
                // Approximating the percentage based on the image: 15/20/45/20
                Box(modifier = Modifier.weight(0.15f).fillMaxHeight().background(ColorProgress1))
                Box(modifier = Modifier.weight(0.20f).fillMaxHeight().background(ColorProgress2))
                Box(modifier = Modifier.weight(0.45f).fillMaxHeight().background(ColorProgress3))
                Box(modifier = Modifier.weight(0.20f).fillMaxHeight().background(ColorProgress4))
            }
        }
    }
}