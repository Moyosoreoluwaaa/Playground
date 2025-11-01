package com.playground.rooms.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.NotificationSetting
import com.playground.rooms.domain.model.NotificationType

@Composable
fun RoomsNotificationToggleItem(
    setting: NotificationSetting,
    onToggleChanged: (NotificationType, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(setting.title) },
            supportingContent = {
                Text(
                    text = setting.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Switch(
                    checked = setting.isEnabled,
                    onCheckedChange = { isChecked ->
                        onToggleChanged(setting.type, isChecked)
                    }
                )
            },
            modifier = Modifier.fillMaxWidth().clickable {
                // Toggling the switch via the entire row click
                onToggleChanged(setting.type, !setting.isEnabled)
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
