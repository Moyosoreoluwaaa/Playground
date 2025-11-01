package com.playground.crypto.presentation

import android.content.res.Configuration
import android.graphics.PointF
import android.graphics.Rect
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playground.R
import com.playground.crypto.domain.model.Coin
import com.playground.crypto.domain.model.Currency
import com.playground.crypto.domain.model.WatchlistFilter
import com.playground.crypto.presentation.uistate.HomeUiState
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 *
 */

@Composable
fun PriceChangeText(change: Double, modifier: Modifier = Modifier) {
    val isPositive = change >= 0
    val color =
        if (isPositive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val icon = if (isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
    val sign = if (isPositive) "+" else ""
    val formattedChange = "%.2f".format(change)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (isPositive) "Price increased" else "Price decreased",
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "$sign$formattedChange%",
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PriceChangeTextPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        PriceChangeText(change = 0.96)
        PriceChangeText(change = -0.44)
    }
}

@Composable
fun PillTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "PillTabBackgroundColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "PillTabContentColor"
    )

    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PillTabPreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PillTab(text = "Hot", isSelected = true, onClick = {})
        PillTab(text = "Market Cap", isSelected = false, onClick = {})
        PillTab(text = "24H Change", isSelected = false, onClick = {})
    }
}

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

@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}


@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    SectionHeader(
        text = "Watchlist",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun CoinListItem(
    coin: Coin,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onCoinClick(coin.id) }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            coin.iconUrl?.let { CoinIcon(iconUrl = it, contentDescription = "${coin.name} logo") }
            Column {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$%,.2f".format(coin.currentPrice),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            PriceChangeText(change = coin.priceChange24h)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CoinListItemPreview() {
    val btc = Coin("bitcoin", "BTC", "Bitcoin", "", 98257.81, -0.44)
    val bnb = Coin("binancecoin", "BNB", "Binance", "", 703.57, 0.96)
    Column(modifier = Modifier.padding(16.dp)) {
        CoinListItem(coin = btc, onCoinClick = {})
        CoinListItem(coin = bnb, onCoinClick = {})
    }
}

@Composable
fun BalanceSummaryCard(
    totalBalance: String,
    dailyChange: Double,
    chartData: List<Float>,
    onDepositClick: () -> Unit,
    onP2PClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = totalBalance,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    PriceChangeText(change = dailyChange)
                }
                // Sparkline Chart
                SparklineChart(
                    data = chartData,
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDepositClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Deposit")
                }
                Button(
                    onClick = onP2PClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("P2P Trading")
                }
            }
        }
    }
}

@Composable
private fun SparklineChart(data: List<Float>, modifier: Modifier = Modifier) {
    val graphColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val path = Path()
        val maxValue = data.maxOrNull() ?: 0f
        val minValue = data.minOrNull() ?: 0f
        val range = maxValue - minValue

        data.forEachIndexed { index, value ->
            val x = size.width * (index.toFloat() / (data.size - 1))
            val y = size.height * (1 - ((value - minValue) / range))

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BalanceSummaryCardPreview() {
    BalanceSummaryCard(
        totalBalance = "$3,462.10",
        dailyChange = 85.28,
        chartData = listOf(5f, 4f, 6f, 8f, 7f, 9f, 8f),
        onDepositClick = {},
        onP2PClick = {}
    )
}

@Composable
fun CurrencyInputCard(
    cardTitle: String,
    selectedCurrency: Currency,
    amount: String,
    balance: String,
    onAmountChange: (String) -> Unit,
    onCurrencySelectClick: () -> Unit,
    onMaxClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInputEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                MaterialTheme.shapes.large
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cardTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onCurrencySelectClick) {
                CoinIcon(
                    iconUrl = selectedCurrency.iconUrl,
                    contentDescription = "${selectedCurrency.name} logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedCurrency.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select currency"
                )
            }
        }
        BasicTextField(
            value = amount,
            onValueChange = onAmountChange,
            enabled = isInputEnabled,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = if (isInputEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (amount.isEmpty() && isInputEnabled) {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Balance: $balance",
                style = MaterialTheme.typography.labelMedium
            )
            if (isInputEnabled) {
                TextButton(onClick = onMaxClick, modifier = Modifier.height(32.dp)) {
                    Text("Max")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CurrencyInputCardPreview() {
    val btc = Currency("BTC", "Bitcoin", "")
    val eth = Currency("ETH", "Ethereum", "")
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CurrencyInputCard(
            cardTitle = "Swap from",
            selectedCurrency = btc,
            amount = "0.1705",
            balance = "1.28819212",
            onAmountChange = {},
            onCurrencySelectClick = {},
            onMaxClick = {},
            isInputEnabled = true
        )
        CurrencyInputCard(
            cardTitle = "To",
            selectedCurrency = eth,
            amount = "4.21",
            balance = "10.5",
            onAmountChange = {},
            onCurrencySelectClick = {},
            onMaxClick = {},
            isInputEnabled = false
        )
    }
}

@Composable
fun InteractiveLineChart(
    data: List<PointF>,
    modifier: Modifier = Modifier,
) {
    var touchX by remember { mutableFloatStateOf(-1f) }
    val path = remember(data) { Path() }
    val lineAndIndicatorColor = MaterialTheme.colorScheme.primary
    val lineAndIndicatorSecondColor = MaterialTheme.colorScheme.primary

    // Memoize the paint object for performance
    val textPaint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 40f
            color = android.graphics.Color.WHITE // Using Android color for the native canvas
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        touchX = offset.x.coerceIn(0f, size.width.toFloat())
                    },
                    onDragEnd = { touchX = -1f },
                    onDragCancel = { touchX = -1f },
                    onDrag = { change, _ ->
                        touchX = change.position.x.coerceIn(0f, size.width.toFloat())
                    }
                )
            }
    ) {
        if (data.size < 2) return@Canvas // Need at least 2 points to draw a line

        val minX = data.minOf { it.x }
        val maxX = data.maxOf { it.x }
        val minY = data.minOf { it.y }
        val maxY = data.maxOf { it.y }
        val xRange = (maxX - minX).takeIf { it > 0 } ?: 1f
        val yRange = (maxY - minY).takeIf { it > 0 } ?: 1f

        // Function to transform data points to canvas coordinates
        fun PointF.toCanvasPoint(): PointF {
            val canvasX = (this.x - minX) / xRange * size.width
            val canvasY = size.height - ((this.y - minY) / yRange * size.height)
            return PointF(canvasX, canvasY)
        }

        // Build the path
        path.reset()
        data.forEachIndexed { index, point ->
            val canvasPoint = point.toCanvasPoint()
            if (index == 0) {
                path.moveTo(canvasPoint.x, canvasPoint.y)
            } else {
                path.lineTo(canvasPoint.x, canvasPoint.y)
            }
        }

        // Draw the line graph
        drawPath(
            path,
            color = lineAndIndicatorColor,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // If user is interacting with the chart
        if (touchX >= 0f) {
            // Find the closest data point to the touch position
            val dataIndex =
                ((touchX / size.width) * (data.size - 1)).roundToInt().coerceIn(0, data.size - 1)
            val closestDataPoint = data[dataIndex]
            val canvasPoint = closestDataPoint.toCanvasPoint()

            // Draw vertical indicator line
            drawLine(
                color = lineAndIndicatorColor.copy(alpha = 0.5f),
                start = Offset(canvasPoint.x, 0f),
                end = Offset(canvasPoint.x, size.height),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            // Draw circle on the path
            drawCircle(
                color = lineAndIndicatorColor,
                radius = 12f,
                center = Offset(canvasPoint.x, canvasPoint.y)
            )
            drawCircle(
                color = lineAndIndicatorSecondColor,
                radius = 8f,
                center = Offset(canvasPoint.x, canvasPoint.y)
            )

            // Draw tooltip
            val tooltipText = "$%.2f".format(closestDataPoint.y)
            val textBounds = Rect()
            textPaint.getTextBounds(tooltipText, 0, tooltipText.length, textBounds)

            val tooltipWidth = textBounds.width() + 40f
            val tooltipHeight = textBounds.height() + 30f
            val tooltipCornerRadius = CornerRadius(16f, 16f)
            val tooltipTop = 0f
            val tooltipLeft =
                (canvasPoint.x - tooltipWidth / 2).coerceIn(0f, size.width - tooltipWidth)

            // **FIX 1: Using correct drawRoundRect overload**
            drawRoundRect(
                color = lineAndIndicatorColor,
                topLeft = Offset(tooltipLeft, tooltipTop),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = tooltipCornerRadius
            )

            // **FIX 2: Using drawContext.canvas.nativeCanvas**
            drawContext.canvas.nativeCanvas.drawText(
                tooltipText,
                tooltipLeft + tooltipWidth / 2,
                tooltipTop + tooltipHeight / 2 + textBounds.height() / 2,
                textPaint
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun InteractiveLineChartPreview() {
    val sampleData = remember {
        (0..30).map {
            val x = it.toFloat()
            val y = Random.nextDouble(1200.0, 1250.0).toFloat()
            PointF(x, y)
        }
    }
    Box(modifier = Modifier.padding(16.dp)) {
        InteractiveLineChart(data = sampleData)
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onCoinClick: (String) -> Unit,
    onFilterChange: (WatchlistFilter) -> Unit,
    onMenuClick: () -> Unit,
    onDepositClick: () -> Unit,
    onP2PClick: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopAppBar(onMenuClick = onMenuClick)
        },
        bottomBar = {
            HomeBottomNavigation()
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Spacer to add padding from the top
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Balance Summary Card
            item {
                if (uiState.isLoading) {
                    ShimmerBox(height = 180.dp, cornerRadius = 16.dp)
                } else {
                    BalanceSummaryCard(
                        totalBalance = uiState.totalBalance,
                        dailyChange = uiState.dailyChange,
                        chartData = uiState.balanceChartData,
                        onDepositClick = onDepositClick,
                        onP2PClick = onP2PClick
                    )
                }
            }

            // Promo Card
            item {
                if (uiState.isLoading) {
                    ShimmerBox(height = 100.dp, cornerRadius = 16.dp)
                } else {
                    PromoCard(
                        title = "Refer and Earn",
                        subtitle = "Earn up to 1,000 USDT in Trading",
                        onInviteClick = { /* TODO */ }
                    )
                }
            }

            // Watchlist Section
            stickyHeader {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    SectionHeader(text = "Watchlist", modifier = Modifier.padding(top = 8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    WatchlistFilters(
                        selectedFilter = uiState.selectedFilter,
                        onFilterChange = onFilterChange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (uiState.isLoading) {
                items(5) {
                    ShimmerCoinListItem()
                }
            } else if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(uiState.watchlist, key = { it.id }) { coin ->
                    CoinListItem(coin = coin, onCoinClick = onCoinClick)
                }
            }

            // Spacer for bottom padding
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(onMenuClick: () -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Exchange", "Wallet")

    TopAppBar(
        title = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun HomeBottomNavigation() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Discover", "", "Asset", "Profile")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Search,
        null, // Placeholder for FAB
        Icons.Default.AccountBalanceWallet,
        Icons.Default.Person
    )

    BottomAppBar(
        actions = {
            items.forEachIndexed { index, screen ->
                if (index == 2) {
                    // This is the position for the FAB, so we leave it empty
                    // to create space. A better approach for complex layouts might
                    // use BottomAppBar with a FAB cutout.
                    Box(modifier = Modifier.weight(1f))
                } else {
                    NavigationBarItem(
                        icon = { Icon(icons[index]!!, contentDescription = screen) },
                        label = { Text(screen) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Handle central action */ },
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Default.SwapHoriz, "Asset Actions")
            }
        }
    )
}

@Composable
private fun WatchlistFilters(
    selectedFilter: WatchlistFilter,
    onFilterChange: (WatchlistFilter) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(WatchlistFilter.values()) { filter ->
            PillTab(
                text = filter.name.replace('_', ' '),
                isSelected = selectedFilter == filter,
                onClick = { onFilterChange(filter) }
            )
        }
    }
}

@Composable
fun PromoCard(title: String, subtitle: String, onInviteClick: () -> Unit) {
    Card(
        onClick = onInviteClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Go to promotion",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ShimmerBox(height: Dp, cornerRadius: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.LightGray.copy(alpha = 0.9f),
                        Color.LightGray.copy(alpha = 0.4f),
                        Color.LightGray.copy(alpha = 0.9f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    )
}

@Composable
private fun ShimmerCoinListItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(height = 40.dp, cornerRadius = 20.dp)
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerBox(height = 20.dp, cornerRadius = 4.dp)
            ShimmerBox(height = 16.dp, cornerRadius = 4.dp)
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenContentPreview() {
    val mockWatchlist = listOf(
        Coin("bitcoin", "BTC", "Bitcoin", "", 98257.81, -0.44),
        Coin("binancecoin", "BNB", "Binance", "", 703.57, 0.96),
        Coin("ethereum", "ETH", "Ethereum", "", 3456.78, 1.23)
    )
    val mockUiState = HomeUiState(
        isLoading = false,
        totalBalance = "$3,462.10",
        dailyChange = 85.28,
        balanceChartData = listOf(5f, 4f, 6f, 8f, 7f, 9f, 8f),
        selectedFilter = WatchlistFilter.HOT,
        watchlist = mockWatchlist
    )
    MaterialTheme { // Wrap in Theme to apply colors
        HomeScreen(
            uiState = mockUiState,
            onCoinClick = {},
            onFilterChange = {},
            onMenuClick = {},
            onDepositClick = {},
            onP2PClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    MaterialTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = true),
            onCoinClick = {},
            onFilterChange = {},
            onMenuClick = {},
            onDepositClick = {},
            onP2PClick = {}
        )
    }
}