package com.playground.crypto.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.background

data class ChatUiState(
    val isLoading: Boolean = false,
    val title: String = "New Chat",
    val botAvatarUrl: String = "https://via.placeholder.com/150",
    val chatMessages: List<ChatMessage> = emptyList(),
    val isBotGenerating: Boolean = false
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatUiState,
    onSendMessage: (message: String) -> Unit,
    onStopGenerating: () -> Unit,
    onBackClick: () -> Unit
) {
    var inputMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = state.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement chat menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Chat options"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.isBotGenerating) {
                    Button(
                        onClick = onStopGenerating,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            contentDescription = "Stop generating"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Stop generating")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO: Implement attachment */ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Attach file"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
//                        colors = TextFieldDefaults.textFieldColors(
//                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent
//                        ),
                        placeholder = { Text(text = "Type your question here...") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                onSendMessage(inputMessage)
                                inputMessage = ""
                            }
                        },
                        enabled = inputMessage.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send message",
                            tint = if (inputMessage.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.chatMessages) { message ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (message.isFromUser) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Avatar",
                                modifier = Modifier.size(40.dp).padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        } else {
                            AsyncImage(
                                model = state.botAvatarUrl,
                                contentDescription = "Bot Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Card(
                            modifier = Modifier.width(250.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = message.text,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF1E272E),
            onBackground = Color(0xFFFFFFFF),
            surface = Color(0xFF1E272E),
            onSurface = Color(0xFFB0D9C3),
            primary = Color(0xFF4CAF50),
            onPrimary = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFF2F3C47),
            onSurfaceVariant = Color(0xFFCFD8DC),
            secondary = Color(0xFF00796B),
            tertiary = Color(0xFF00B0FF),
        )
    ) {
        val mockState = ChatUiState(
            title = "Job Finder UX",
            botAvatarUrl = "https://via.placeholder.com/150",
            chatMessages = listOf(
                ChatMessage(id = "1", text = "I've implemented a user-friendly job search UI...", isFromUser = false),
                ChatMessage(id = "2", text = "That's great! What did you use for the tech stack?", isFromUser = true),
                ChatMessage(id = "3", text = "I used Jetpack Compose with Material 3 for the UI and a Firestore backend for data storage.", isFromUser = false)
            ),
            isBotGenerating = false
        )
        ChatScreen(
            state = mockState,
            onSendMessage = {},
            onStopGenerating = {},
            onBackClick = {}
        )
    }
}
