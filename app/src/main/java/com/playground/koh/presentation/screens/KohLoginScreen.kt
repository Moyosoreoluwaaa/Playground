package com.playground.koh.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.R
import com.playground.koh.presentation.components.KohEmailTextField
import com.playground.koh.presentation.components.KohPasswordTextField
import com.playground.koh.presentation.uistate.KohLoginEvent
import com.playground.koh.presentation.uistate.KohLoginUiState

@Composable
fun KohLoginScreen(
    uiState: KohLoginUiState,
    onEvent: (KohLoginEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.login_bg),
                contentScale = ContentScale.Crop
            )
            .background(Color.Black.copy(alpha = 0.2f)) // Optional overlay for text visibility
    ) {

        // Login card in the center
        KohLoginCard(
            modifier = Modifier.align(Alignment.BottomCenter),
            uiState = uiState,
            onEvent = onEvent
        )
    }
}

@Composable
private fun KohLoginCard(
    modifier: Modifier = Modifier,
    uiState: KohLoginUiState,
    onEvent: (KohLoginEvent) -> Unit
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .paint(
                painter = painterResource(id = R.drawable.input_bg),
                contentScale = ContentScale.Crop
            ),
        color = Color.Transparent, // Makes the Surface background transparent
    ) {

        // We use a Box to apply the gradient brush as a background
        Box(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    // Facebook Login button
                    OutlinedButton(
                        onClick = { onEvent(KohLoginEvent.FacebookLoginClicked) },
                        modifier = Modifier
                            .width(125.dp)
                            .height(40.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Black,
                                    Color.Black
                                )
                            )
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "Facebook",
                            modifier = Modifier.size(8.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Facebook", color = Color.White)
                    }

                    // Error message
                    uiState.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                KohEmailTextField(value = "ann.smith@gmail.com", onValueChange = {})
                Spacer(Modifier.height(16.dp))

                KohPasswordTextField(value = "password123", onValueChange = {})

                // Forgot password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onEvent(KohLoginEvent.ForgotPasswordClicked) }) {
                        Text(
                            "I forgot",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Main Login button
                Button(
                    onClick = { onEvent(KohLoginEvent.LoginClicked) },
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(50)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Log In",
                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))


            }
        }
    }
}


// KohLoginScreen.kt

@Preview(showBackground = true)
@Composable
fun KohLoginScreenPreview() {
    MaterialTheme {
        KohLoginScreen(
            uiState = KohLoginUiState(
                email = "ann.smith@gmail.com",
                password = "password",
                isLoading = false,
                errorMessage = null
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KohLoginScreenLoadingPreview() {
    MaterialTheme {
        KohLoginScreen(
            uiState = KohLoginUiState(
                email = "ann.smith@gmail.com",
                password = "password",
                isLoading = true,
                errorMessage = null
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KohLoginScreenErrorPreview() {
    MaterialTheme {
        KohLoginScreen(
            uiState = KohLoginUiState(
                email = "ann.smith@gmail.com",
                password = "password",
                isLoading = false,
                errorMessage = "Invalid credentials. Please try again."
            ),
            onEvent = {}
        )
    }
}
