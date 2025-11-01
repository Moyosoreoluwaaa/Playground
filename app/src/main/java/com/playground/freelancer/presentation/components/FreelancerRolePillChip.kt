package com.playground.freelancer.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.freelancer.domain.model.FreelancerRole

@Composable
fun FreelancerRolePillChip(
    role: FreelancerRole,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Using FilterChip as it's the closest M3 component for this "pill" style
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(role.name) },
        modifier = Modifier.padding(4.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surface,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = null
    )
}
