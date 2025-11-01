package com.playground.invest.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.invest.presentation.uistate.InvestDashboardUiState
import com.playground.invest.presentation.uistate.InvestProduct
import com.playground.invest.presentation.uistate.InvestQuickAction
import com.playground.invest.presentation.uistate.InvestTransaction
import com.playground.invest.presentation.uistate.QuickAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestDashboardScreen(uiState: InvestDashboardUiState) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Welcome back,")
                        Text(
                            text = uiState.userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle profile click */ }) {
                        Icon(
                            imageVector = Icons.Default.List, // Placeholder
                            contentDescription = "Profile and settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Total Balance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ){
                            Text("Total Balance", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.totalBalance,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = uiState.balanceChangePercentage,
                                    color = Color.Green, // Assuming green for positive change
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "View details"
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                uiState.quickActions.forEach { action ->
                                    InvestQuickActionButton(action = action)
                                }
                            }
                        }
                        Button(
                            onClick = { /* TODO: Handle click */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black, // Explicitly black as per design
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Apply for new products")
                        }
                    }
                }
            }

            item {
                // "Products" section
                Column {
                    Text(
                        text = "Products",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.products) { product ->
                            InvestProductCard(product = product)
                        }
                    }
                }
            }

            item {
                // "Transactions" Section
                Column {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // "January" as a subtitle, could be dynamic
                            Text(
                                text = "January",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(16.dp))
                            // LazyColumn for transactions within a Card
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                uiState.recentTransactions.forEach { transaction ->
                                    InvestTransactionRow(transaction = transaction)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvestQuickActionButton(action: InvestQuickAction) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Icon button with a circular background
        IconButton(
            onClick = { /* TODO: Handle action click */ },
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                modifier = Modifier.size(40.dp)
            )
        }
        Text(text = action.label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun InvestProductCard(product: InvestProduct) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = "${product.title} image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = product.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.price,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Green // Assuming the price is a green accent color
            )
        }
    }
}

@Composable
fun InvestTransactionRow(transaction: InvestTransaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = transaction.imageUrl,
            contentDescription = "${transaction.title} icon",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transaction.subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = transaction.amount,
            color = if (transaction.isDebit) Color.Red else Color.Green,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun QuickActionButton(action: QuickAction) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { /* TODO: Handle action click */ },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                modifier = Modifier.size(40.dp)
            )
        }
        Text(text = action.label, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true, name = "Invest Dashboard Screen Preview")
@Composable
fun InvestDashboardScreenPreview() {
    MaterialTheme { // Use your custom theme here
        val fakeUiState = InvestDashboardUiState(
            quickActions = listOf(
                InvestQuickAction.INVEST,
                InvestQuickAction.SEND_MONEY,
                InvestQuickAction.PAY_BILLS
            ),
            products = listOf(
                InvestProduct(
                    id = 1,
                    imageUrl = "https://example.com/product_a.png",
                    title = "My Green",
                    price = "SAR 1,000"
                ),
                InvestProduct(
                    id = 2,
                    imageUrl = "https://example.com/product_b.png",
                    title = "My Blue",
                    price = "SAR 500"
                ),
                InvestProduct(
                    id = 3,
                    imageUrl = "https://example.com/product_c.png",
                    title = "My Gold",
                    price = "SAR 2,500"
                )
            ),
            recentTransactions = listOf(
                InvestTransaction(
                    id = 1,
                    imageUrl = "https://example.com/playstation.png",
                    title = "Playstation",
                    subtitle = "Sarah J.",
                    amount = "SAR 450.00",
                    isDebit = true
                ),
                InvestTransaction(
                    id = 2,
                    imageUrl = "https://example.com/spotify.png",
                    title = "Spotify",
                    subtitle = "Subscription",
                    amount = "SAR 50.00",
                    isDebit = true
                )
            )
        )
        InvestDashboardScreen(uiState = fakeUiState)
    }
}
