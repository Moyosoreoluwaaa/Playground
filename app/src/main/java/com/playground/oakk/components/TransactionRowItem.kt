package com.playground.oakk.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playground.oakk.OakkTheme
import com.playground.oakk.models.Transaction

/**
 * A row item that displays information about a single transaction.
 *
 * @param transaction The transaction data to display.
 * @param onClick The callback to be invoked when the item is clicked.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun TransactionRowItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding( 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(transaction.merchantLogoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${transaction.merchantName} logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchantName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = transaction.amount,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                 Text(
                    text = transaction.type,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun TransactionRowItemPreview() {
    val sampleTransaction = Transaction(
        id = "t1",
        merchantName = "Play Station Store",
        merchantLogoUrl = "https://placehold.co/96x96/000000/FFFFFF?text=PS",
        amount = "SAR -120.00",
        type = "Purchase",
        date = "15 Jan"
    )
    OakkTheme {
        TransactionRowItem(transaction = sampleTransaction, onClick = {})
    }
}
