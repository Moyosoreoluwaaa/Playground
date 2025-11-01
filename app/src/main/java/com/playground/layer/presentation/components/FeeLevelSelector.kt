package com.playground.layer.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.layer.domain.model.FeeLevel

@Composable
fun FeeLevelSelector(
    currentFeeLevel: FeeLevel,
    onFeeLevelChange: (FeeLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Slow / Avg",
            color = if (currentFeeLevel == FeeLevel.SLOW_AVG) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = currentFeeLevel == FeeLevel.FAST,
            onCheckedChange = { isChecked -> onFeeLevelChange(if (isChecked) FeeLevel.FAST else FeeLevel.SLOW_AVG) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray,
                uncheckedThumbColor = Color.White
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Fast",
            color = if (currentFeeLevel == FeeLevel.FAST) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}