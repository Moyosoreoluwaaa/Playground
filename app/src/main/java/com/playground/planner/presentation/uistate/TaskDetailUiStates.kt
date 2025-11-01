package com.playground.planner.presentation.uistate

import com.playground.planner.domain.model.Comment
import com.playground.planner.domain.model.TaskAction
import com.playground.planner.domain.model.TaskTag
import com.playground.planner.domain.model.TaskTab
import com.playground.planner.domain.model.Update
import com.playground.planner.domain.model.UserMetadata

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val taskId: String,
    val title: String,
    val isCompleted: Boolean,
    val assignee: UserMetadata,
    val dueDateLabel: String,
    val status: TaskTag,
    val priority: TaskTag,
    val description: String,
    val currentTab: TaskTab = TaskTab.COMMENTS,
    val comments: List<Comment> = emptyList(),
    val updates: List<Update> = emptyList(),
    val newCommentText: String = "",
)

sealed class TaskDetailEvent {
    // Toolbar Actions
    data class OnCompletedToggled(val isCompleted: Boolean) : TaskDetailEvent()
    data class OnActionClicked(val action: TaskAction) : TaskDetailEvent()

    // Metadata Editing Actions (Tapping Assignee, Due Date, Status, Priority)
    data object OnAssigneeEditClicked : TaskDetailEvent()
    data object OnDueDateEditClicked : TaskDetailEvent()
    data object OnStatusEditClicked : TaskDetailEvent()
    data object OnPriorityEditClicked : TaskDetailEvent()

    // Tab Selection
    data class OnTabSelected(val tab: TaskTab) : TaskDetailEvent()

    // Comment Input Actions
    data class OnCommentTextChanged(val text: String) : TaskDetailEvent()
    data object OnAttachClicked : TaskDetailEvent()
    data object OnSendClicked : TaskDetailEvent()
}
