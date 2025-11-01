package com.playground.crypto.presentation.tempo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.R
import com.playground.ui.theme.PlaygroundTheme

@Composable
fun TempoWelcomeScreenClass(
    onLoginClicked: () -> Unit,
    onSignInWithGoogleClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder for the top image
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(300.dp)
                .fillMaxSize()
        )
        Text(
            text = "Welcome Back!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Access your personal account by logging in.",
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Input fields (using placeholders)
        TempoLoginInputClass(
            value = "",
            onValueChange = {},
            placeholder = "Email Address or Username",
            keyboardOptions = KeyboardOptions.Default
        )
        TempoLoginInputClass(
            value = "",
            onValueChange = {},
            placeholder = "Enter your password",
            keyboardOptions = KeyboardOptions.Default
        )
        
        TextButton(
            onClick = onForgotPasswordClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
        ) {
            Text("Forgot Password", color = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Log in", color = Color.White)
        }
        
        Button(
            onClick = onSignInWithGoogleClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text("Sign in with Google", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        
        TextButton(onClick = onSignUpClicked) {
            Text("Don't have an account? Sign Up", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TempoWelcomeScreenClassPreview() {
    PlaygroundTheme {
        TempoWelcomeScreenClass(
            onLoginClicked = {},
            onSignInWithGoogleClicked = {},
            onForgotPasswordClicked = {},
            onSignUpClicked = {}
        )
    }
}
