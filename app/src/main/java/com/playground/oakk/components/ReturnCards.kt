package com.playground.oakk.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 3. Return Cards Composable
@Composable
fun ReturnCards() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        @Composable
        fun ReturnCard(title: String, amount: String) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ColorSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        color = ColorTextMuted
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = amount,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextDark
                    )
                }
            }
        }
        ReturnCard(title = "TODAY'S RETURN", amount = "$7.44")
        ReturnCard(title = "TOTAL MONTH RETURN", amount = "$132.90")
    }
}