package com.playground.planner.data.source

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import com.playground.planner.domain.model.Comment
import com.playground.planner.domain.model.PlannerPriority
import com.playground.planner.domain.model.PlannerTask
import com.playground.planner.domain.model.PlannerTaskSection
import com.playground.planner.domain.model.TaskTag
import com.playground.planner.domain.model.UserMetadata
import com.playground.planner.presentation.uistate.PlannerUiState
import com.playground.planner.presentation.uistate.TaskDetailUiState

val MockUser = UserMetadata("u1", "Jenifer Anniston", "https://i.pravatar.cc/150?img=47")
val MockCommenter1 = UserMetadata("u2", "John Smith", "https://i.pravatar.cc/150?img=50")
val MockCommenter2 = UserMetadata("u3", "Anita", "https://i.pravatar.cc/150?img=18")

val MockComments = listOf(
    Comment(
        "c1",
        MockCommenter1,
        "17th Feb 2024",
        "Hi 👋 I'll do that task now, you can start working on another task!"
    ),
    Comment("c2", MockCommenter2, "Just Now", "Hello!")
)

val MockTaskDetailUiState = TaskDetailUiState(
    taskId = "t1",
    title = "Schedule Me An Appointment With My Endocrinologist",
    isCompleted = false,
    assignee = MockUser,
    dueDateLabel = "Jul 16 - 24",
    status = TaskTag("In Progress", Color(0xFF9C27B0)), // M3 Tertiary container mock
    priority = TaskTag("Low", Color(0xFF6A65D1)),       // Muted Purple mock
    description = "Schedule and attend an appointment! 🥵",
    comments = MockComments,
    newCommentText = ""
)

val MockPlannerUiState = PlannerUiState(
    taskSections = listOf(
        PlannerTaskSection(
            title = "TODO",
            tasks = listOf(
                PlannerTask(
                    id = "t1",
                    title = "Schedule Me An Appointment With My Endocrinologist",
                    priority = PlannerPriority.HIGH,
                    statusTag = "15M - Online",
                    timeRemaining = "15 Days left",
                    typeIcon = Icons.Default.CalendarToday,
                    linkCount = 17,
                    commentCount = 6,
                    isOnline = true
                ),
                PlannerTask(
                    id = "t2",
                    title = "Help DStudio Get More Customers",
                    priority = PlannerPriority.LOW,
                    statusTag = "Remote",
                    timeRemaining = "15 Days left",
                    typeIcon = Icons.Default.Person,
                    linkCount = 9,
                    commentCount = 13
                )
            )
        )
    )
)