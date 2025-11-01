package com.playground.crypto.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme

data class WelcomeUiState(
    val title: String = "Explore infinite capabilities of writing",
    val features: List<FeatureItem> = listOf(
        FeatureItem("Remembers what user said earlier in the conversation", Icons.Default.Info),
        FeatureItem("Allows user to provide follow-up corrections", Icons.Default.List),
        FeatureItem("Trained to decline inappropriate requests", Icons.Default.Warning)
    )
)

data class FeatureItem(
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun WelcomeScreen(
    state: WelcomeUiState,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section: Logo and Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "App Logo",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mindmate",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                TextButton(onClick = onSkipClick) {
                    Text(text = "Skip", color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Middle section: Title and features
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = state.title,
                    fontSize = 40.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.onSurface
                )
                state.features.forEach { feature ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Icon(
                            imageVector = feature.icon,
                            contentDescription = null, // decorative
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = feature.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Bottom section: Login and Signup buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(.5f)
                ) {
                    Text(text = "Login")
                }
                Button(
                    onClick = onSignupClick,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(text = "Sign up")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = androidx.compose.ui.graphics.Color(0xFF232C2B),
            onBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            surface = androidx.compose.ui.graphics.Color(0xFF232C2B),
            onSurface = androidx.compose.ui.graphics.Color(0xFFB0D9C3),
            primary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
            onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            secondary = androidx.compose.ui.graphics.Color(0xFF00796B),
            tertiary = androidx.compose.ui.graphics.Color(0xFF00B0FF),
            surfaceVariant = androidx.compose.ui.graphics.Color(0xFF455A64),
            onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFCFD8DC)
        )
    ) {
        WelcomeScreen(
            state = WelcomeUiState(),
            onLoginClick = { /* no-op */ },
            onSignupClick = { /* no-op */ },
            onSkipClick = { /* no-op */ }
        )
    }
}
