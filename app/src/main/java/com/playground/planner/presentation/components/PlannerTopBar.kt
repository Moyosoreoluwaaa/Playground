package com.playground.planner.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTopBar(
    projectName: String,
    onMenuClicked: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
    onNotificationsClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    notificationCount: Int = 0,
    avatarUrl: String? = null
) {
    // ... (PlannerTopBar implementation from previous step - Unchanged)
    TopAppBar(
        title = { Text(projectName, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(Default.Menu, contentDescription = "Open Navigation Drawer")
            }
        },
        actions = {
            IconButton(onClick = onSearchClicked) {
                Icon(Default.Search, contentDescription = "Search Tasks")
            }

            BadgedBox(
                badge = {
                    if (notificationCount > 0) Badge { Text("$notificationCount") }
                }
            ) {
                IconButton(onClick = onNotificationsClicked) {
                    Icon(Default.Notifications, contentDescription = "Notifications")
                }
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(32.dp),
                onClick = onProfileClicked
            ) {
                val painter = rememberAsyncImagePainter(model = avatarUrl)
                val isImageLoadedOrLoading = painter.state is AsyncImagePainter.State.Success

                if (!isImageLoadedOrLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Default.Person,
                            contentDescription = "Error loading image",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Image(
                    painter = painter,
                    contentDescription = "User Profile",
                    modifier = Modifier.fillMaxSize(),
                    alpha = if (isImageLoadedOrLoading) 1f else 0f
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
