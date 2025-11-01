package com.playground.planner.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.planner.data.source.MockTaskDetailUiState
import com.playground.planner.domain.model.TaskAction
import com.playground.planner.domain.model.TaskTab
import com.playground.planner.presentation.components.CommentInputBar
import com.playground.planner.presentation.components.CommentItem
import com.playground.planner.presentation.components.CommentTabs
import com.playground.planner.presentation.components.TaskDetailToolbar
import com.playground.planner.presentation.components.TaskMetadataRow
import com.playground.planner.presentation.components.TaskPillChip
import com.playground.planner.presentation.uistate.TaskDetailEvent
import com.playground.planner.presentation.uistate.TaskDetailUiState

// region Main Screen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onEvent: (TaskDetailEvent) -> Unit = {},
    onCloseClicked: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Persistent Comment Input Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(16.dp)
                    .imePadding() // Adjusts for the software keyboard
            ) {
                CommentInputBar(
                    uiState = uiState,
                    onCommentTextChanged = { onEvent(TaskDetailEvent.OnCommentTextChanged(it)) },
                    onAttachClicked = { onEvent(TaskDetailEvent.OnAttachClicked) },
                    onSendClicked = { onEvent(TaskDetailEvent.OnSendClicked) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // 1. TOOLBAR
            TaskDetailToolbar(
                isCompleted = uiState.isCompleted,
                onCompletedToggled = { onEvent(TaskDetailEvent.OnCompletedToggled(it)) },
                onCloseClicked = onCloseClicked
            )

            // 2. TASK TITLE
            Text(
                uiState.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // 3. METADATA SECTION
            // Assignee
            TaskMetadataRow(
                label = "Assignee",
                onRowClicked = { onEvent(TaskDetailEvent.OnAssigneeEditClicked) } // <-- COMPLETED PARAMETER USAGE
            ) {
                AsyncImage(
                    model = uiState.assignee.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(8.dp))
                Text(uiState.assignee.name, style = MaterialTheme.typography.bodyLarge)
            }

            // Due Date
            TaskMetadataRow(
                label = "Due Date",
                onRowClicked = { onEvent(TaskDetailEvent.OnDueDateEditClicked) } // <-- COMPLETED PARAMETER USAGE
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(uiState.dueDateLabel, style = MaterialTheme.typography.bodyLarge)
            }

            // Projects / Status / Priority
            // Note: The main Project Row itself may not be editable, but Status/Priority are.
            // We'll make the Status and Priority *sub-rows* clickable.
            TaskMetadataRow(label = "Projects") {
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onEvent(TaskDetailEvent.OnStatusEditClicked) } // <-- COMPLETED PARAMETER USAGE (Status)
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            "Status",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        TaskPillChip(tag = uiState.status)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onEvent(TaskDetailEvent.OnPriorityEditClicked) } // <-- COMPLETED PARAMETER USAGE (Priority)
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ListAlt,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            "Priority",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        TaskPillChip(tag = uiState.priority)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // 4. DESCRIPTION
            Text(
                "Description",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    uiState.description,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Spacer(Modifier.height(24.dp))

            // 5. TAB CONTROLS
            val pagerState = rememberPagerState(
                pageCount = { 2 },
                initialPage = TaskTab.entries.indexOf(uiState.currentTab)
            )

            CommentTabs(
                currentTab = uiState.currentTab,
                onTabSelected = { newTab ->
                    onEvent(TaskDetailEvent.OnTabSelected(newTab))
                    // Manual sync for smooth transition
                }
            )

            // 6. TAB CONTENT AREA (Horizontal Pager)
            HorizontalPager(state = pagerState) { page ->
                when (TaskTab.entries[page]) {
                    TaskTab.COMMENTS -> {
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 300.dp) // Constraint height for nested scroll (simulated)
                                .fillMaxWidth()
                        ) {
                            items(uiState.comments) { comment ->
                                CommentItem(comment)
                            }
                        }
                    }

                    TaskTab.UPDATES -> {
                        Box(
                            modifier = Modifier
                                .heightIn(min = 100.dp)
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Updates history goes here.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420",
    name = "Task Detail Modal Preview"
)
@Composable
private fun TaskDetailScreenPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        TaskDetailScreen(uiState = MockTaskDetailUiState, onCloseClicked = {})
    }
}
