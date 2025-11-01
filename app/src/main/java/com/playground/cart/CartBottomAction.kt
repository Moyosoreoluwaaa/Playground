package com.playground.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Enums and Domain Models (Defined Here to be Self-Contained) ---
enum class CartBottomAction { MENU, TEXT_INPUT, PLUS, BARS, TASKS }

data class CartTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val emojiTag: String,
    val personTag: String? = null
)

// --- UiState (Defined Here to be Self-Contained) ---
data class CartDashboardUiState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val activeBottomAction: CartBottomAction = CartBottomAction.TASKS,
    val audioVisualizerData: List<Float> = listOf(0.2f, 0.5f, 0.8f, 0.3f, 0.9f, 0.1f, 0.6f),
    val timerProgress: Float = 0.4f,
    val timerDisplay: String = "08 45 00",
    val tasks: List<CartTask> = listOf(
        CartTask("t1", "Buy Sunlite", false, "🕶️"),
        CartTask("t2", "Get radi", true, "🍄"),
        CartTask("t3", "Go to mouvi", false, "📽️")
    ),
    val docsPreviewCount: Int = 5,
    // RESOLUTION: Added missing fields that were used in the Composable
    val audioTitle: String = "Ai Sumari",
    val audioSubtitle: String = "All Massage"
)

// --- Reusable Components (Bodies stubbed, but structurally correct) ---

@Composable
fun CartWidgetCard(
    title: String,
    onAddClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .heightIn(min = 150.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                onAddClicked?.let {
                    IconButton(onClick = it, modifier = Modifier.size(24.dp)) {
                        // RESOLUTION: Icon is correctly imported and used
                        Icon(Icons.Default.Add, contentDescription = "Add $title")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun CartBottomActionInput(
    activeAction: CartBottomAction,
    onActionClicked: (CartBottomAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [List Icon]
        IconButton(onClick = { onActionClicked(CartBottomAction.MENU) }) {
            Icon(Icons.Default.Menu, contentDescription = "Open Chat Menu")
        }
        Spacer(Modifier.width(8.dp))
        // [Action Pills]
        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { onActionClicked(CartBottomAction.TASKS) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeAction == CartBottomAction.TASKS) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (activeAction == CartBottomAction.TASKS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                shape = CircleShape
            ) { Text("Tasks") }
        }
    }
}

// --- Main Screen Composable ---
@Composable
fun CartDashboardScreen(
    uiState: CartDashboardUiState,
    onPlayAudio: () -> Unit,
    onStartTimer: () -> Unit,
    onTaskToggle: (String, Boolean) -> Unit,
    onBottomActionClicked: (CartBottomAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { /* CartChatsHeader (Reused) */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chats", style = MaterialTheme.typography.headlineLarge)
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        },
        bottomBar = {
            CartBottomActionInput(uiState.activeBottomAction, onBottomActionClicked)
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ai Sumari Card (Spanning two columns conceptually for size)
            item(span = { GridItemSpan(maxLineSpan) }) {
                // RESOLUTION: uiState.audioTitle is now available
                CartWidgetCard(
                    title = uiState.audioTitle,
                    modifier = Modifier.fillMaxWidth(),
                    onAddClicked = null
                ) {
                    // Placeholder for Canvas Visualizer
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier
                            .size(60.dp)
                            .background(Color.Red)) // Visualizer placeholder
                        Spacer(Modifier.width(16.dp))
                        IconButton(onClick = onPlayAudio) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play Audio"
                            )
                        }
                    }
                }
            }

            // Timer Card (Half width)
            item {
                CartWidgetCard(title = "Timer", onAddClicked = null) {
                    // Placeholder for Canvas Timer Ring
                    Column(Modifier.align(Alignment.CenterHorizontally)) {
                        Text(uiState.timerDisplay, style = MaterialTheme.typography.displaySmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onStartTimer) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Start Timer"
                                )
                            }
                            Text("Start Progec(t)", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            // Docs Card (Half width)
            item {
                CartWidgetCard(title = "Docs", onAddClicked = null) {
                    Text("24-27 April", style = MaterialTheme.typography.labelMedium)
                    // Placeholder for AsyncImage preview and Badge
                    Box(Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.LightGray)) {
                        Text(
                            uiState.docsPreviewCount.toString(),
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                }
            }

            // Tasks Card (Half width, stretch vertically with tasks list)
            item {
                CartWidgetCard(title = "Tasks", onAddClicked = {}) {
                    // Using remember to manage a mutable list for a realistic preview interaction
                    val mutableTasks by remember { mutableStateOf(uiState.tasks.toMutableList()) }

                    mutableTasks.forEachIndexed { index, task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(task.emojiTag, Modifier.padding(end = 4.dp))
                            Text(task.title, Modifier.weight(1f))
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { isChecked ->
                                    // Mock state update for preview
                                    mutableTasks[index] = task.copy(isCompleted = isChecked)
                                    onTaskToggle(task.id, isChecked)
                                }
                            )
                        }
                    }
                }
            }

            // Connect Apps Card (Half width)
            item {
                CartWidgetCard(title = "Connect Apps", onAddClicked = {}) {
                    // Placeholder for app icons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(4) { Icon(Icons.Default.Apps, contentDescription = "App Icon $it") }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartDashboardScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE), // Example M3 colors
            onPrimary = Color.White,
            surface = Color.White,
            surfaceContainerHigh = Color(0xFFF0F0F0)
        )
    ) {
        CartDashboardScreen(
            uiState = CartDashboardUiState(),
            onPlayAudio = { /* mock */ },
            onStartTimer = { /* mock */ },
            onTaskToggle = { _, _ -> /* mock */ },
            onBottomActionClicked = { /* mock */ }
        )
    }
}