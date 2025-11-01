package com.playground.layer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.layer.domain.model.BottomNavDestination
import com.playground.layer.domain.model.BottomNavItem

@Composable
fun CustomBottomNavBar(
    selectedDestination: BottomNavDestination,
    onDestinationSelected: (BottomNavDestination) -> Unit
) {
    val items = listOf(
        BottomNavItem(BottomNavDestination.SWAP, "Swap", Icons.Filled.SwapHoriz),
        BottomNavItem(BottomNavDestination.ASSETS, "Pettes", Icons.Filled.Description),
        BottomNavItem(BottomNavDestination.ACCOUNT, "Acumt", Icons.Filled.AccountCircle),
        BottomNavItem(BottomNavDestination.SETTING, "Setting", Icons.Filled.Settings),
        BottomNavItem(BottomNavDestination.ASSETS, "Pettes", Icons.Filled.Description)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEachIndexed { index, item ->
            if (index == 2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = { onDestinationSelected(item.destination) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else {
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = selectedDestination == item.destination,
                    onClick = { onDestinationSelected(item.destination) }
                )
            }
        }
    }
}