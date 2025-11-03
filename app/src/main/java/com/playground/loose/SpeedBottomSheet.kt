package com.playground.loose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedBottomSheet(
    initialSpeed: Float,
    onDismiss: () -> Unit,
    onSpeedSelected: (Float) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "Playback Speed", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 12.dp))
            val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)
            speeds.forEach { speed ->
                ListItem(
                    headlineContent = { Text("${speed}x") },
                    trailingContent = {
                        if (speed == initialSpeed) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = "Selected")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSpeedSelected(speed)
                            onDismiss()
                        }
                )
            }
        }
    }
}
