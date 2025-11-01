package com.playground.oakk.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playground.oakk.OakkTheme
import com.playground.oakk.components.*
import com.playground.oakk.models.Product
import com.playground.oakk.models.SpecialAttribute
import com.playground.oakk.models.Transaction
import com.playground.oakk.uistates.TabType
import com.playground.oakk.viewmodels.OakkDashboardViewModel

// Enums would typically be in a separate state file
enum class DashboardTab { DASHBOARD, INVESTMENTS, ANALYTICS }
enum class QuickActionType { TOP_UP, INVEST, SEND_MONEY, PAY_BILLS }

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val userAvatarUrl: String = "",
    val selectedTab: DashboardTab = DashboardTab.DASHBOARD,
    val totalBalance: String = "",
    val monthlyPerformance: String = "",
    val products: List<Product> = emptyList(),
    // Using a list of pairs to maintain order
    val transactions: List<Pair<String, List<Transaction>>> = emptyList()
)

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    viewModel: OakkDashboardViewModel,
    onTabSelected: (DashboardTab) -> Unit,
    onActionClick: (QuickActionType) -> Unit,
    onProductClick: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    onApplyForNewProductClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var selectedTabIndex by remember {
        mutableStateOf(
            DashboardTab.values().indexOf(uiState.selectedTab)
        )
    }
    val oakkUiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.selectedTab) {
        selectedTabIndex = DashboardTab.values().indexOf(uiState.selectedTab)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // --- Header ---
        item {
            DashboardHeader(
                userName = uiState.userName,
                onProfileClick = onProfileClick,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Tab Row
            OakkPillTabRow(
                selectedTab = oakkUiState.selectedTab,
                onTabSelected = viewModel::onTabSelected,
                modifier = Modifier.padding(bottom = 24.dp)
            )
//            Spacer(Modifier.height(24.dp))

            // --- Balance Card ---
            BalanceCard(
                balance = uiState.totalBalance,
                performance = uiState.monthlyPerformance
            )

            // --- Quick Actions ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Top Up",
                    onClick = { onActionClick(QuickActionType.TOP_UP) })
                QuickActionButton(
                    icon = Icons.Default.BarChart,
                    label = "Invest",
                    onClick = { onActionClick(QuickActionType.INVEST) })
                QuickActionButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "Send Money",
                    onClick = { onActionClick(QuickActionType.SEND_MONEY) })
                QuickActionButton(
                    icon = Icons.Default.ReceiptLong,
                    label = "Pay Bills",
                    onClick = { onActionClick(QuickActionType.PAY_BILLS) })
            }

            // --- Products Section ---
            SectionHeader(title = "PRODUCTS")
            Spacer(Modifier.height(8.dp))

        }

        items(uiState.products, key = { it.id }) { product ->
            ProductRowItem(product = product, onClick = { onProductClick(product.id) })
        }
        item {
            Button(
                onClick = onApplyForNewProductClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Apply for a new product")
            }
        }

        // --- Transactions Section ---
        uiState.transactions.forEach { (month, transactionsInMonth) ->
            item {
                SectionHeader(title = month.uppercase())
                Spacer(Modifier.height(8.dp))
            }
            items(transactionsInMonth, key = { it.id }) { transaction ->
                TransactionRowItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) })
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    userName: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Welcome back,\n$userName",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier.size(48.dp)
        ) {
            // In a real app, this would be an AsyncImage
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
fun DashboardScreenPreview(
    viewModel: OakkDashboardViewModel = viewModel()
) {
    val mockProducts = listOf(
        Product(
            id = "1",
            name = "Alinma You Card",
            imageUrl = "https://placehold.co/112x112/1D1D1B/F5F5F3?text=A",
            balance = "SAR 87,000.86"
        ),
        Product(
            id = "2",
            name = "Alinma Green",
            imageUrl = "https://placehold.co/112x112/6B8E23/F5F5F3?text=A",
            balance = "$ 67,000.00",
            specialAttribute = SpecialAttribute.Points(10000)
        )
    )
    val mockTransactions = listOf(
        "January" to listOf(
            Transaction(
                id = "t1",
                merchantName = "Play Station Store",
                merchantLogoUrl = "https://placehold.co/96x96/000000/FFFFFF?text=PS",
                amount = "SAR -120.00",
                type = "Purchase",
                date = "15 Jan"
            ),
            Transaction(
                id = "t2",
                merchantName = "Starbucks",
                merchantLogoUrl = "https://placehold.co/96x96/036635/FFFFFF?text=SB",
                amount = "SAR -25.50",
                type = "Purchase",
                date = "14 Jan"
            )
        )
    )
    val mockUiState = DashboardUiState(
        userName = "Alexandr Kosov",
        totalBalance = "SAR 804,660.00",
        monthlyPerformance = "Going up 13% this month, good job",
        products = mockProducts,
        transactions = mockTransactions
    )
    OakkTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardScreen(
                uiState = mockUiState,
                viewModel,
                onTabSelected = {},
                onActionClick = {},
                onProductClick = {},
                onTransactionClick = {},
                onApplyForNewProductClick = {},
                onProfileClick = {},
            )
        }
    }
}
