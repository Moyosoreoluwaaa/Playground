package com.playground.diet.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaNavRoute
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun NonaBottomNavItem(
    route: NonaNavRoute,
    isSelected: Boolean,
    badgeCount: Int? = null,
    onClick: (NonaNavRoute) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color.Transparent,
        animationSpec = spring(),
        label = "NavBackgroundColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Black.copy(alpha = 0.6f),
        animationSpec = spring(),
        label = "NavContentColor"
    )

    val shape = RoundedCornerShape(percent = 50)

    // The clickable area is the pill itself for the selected item, or the whole area for the unselected.
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(shape)
            .background(backgroundColor)
            .clickable { onClick(route) },
        contentAlignment = Alignment.Center
    ) {

        val content = @Composable {
            Icon(
                imageVector = route.icon,
                contentDescription = route.name,
                tint = contentColor
            )
        }

        if (badgeCount != null && badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                    ) {
                        Text(text = badgeCount.toString(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            ) {
                content()
            }
        } else {
            content()
        }
    }
}
