package com.playground.rooms.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.playground.rooms.domain.model.MainNavTab

@Composable
fun RoomsBottomNavBar(
    currentTab: MainNavTab,
    onTabSelected: (MainNavTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        MainNavTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            MainNavTab.ROOMS -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                            MainNavTab.DMS -> if (isSelected) Icons.Filled.Email else Icons.Outlined.Email
                            MainNavTab.SEARCH -> Icons.Default.Search
                            MainNavTab.PROFILE -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
                        },
                        contentDescription = tab.name,
                    )
                },
                label = { Text(tab.name, fontSize = 10.sp) },
                selected = isSelected,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}
