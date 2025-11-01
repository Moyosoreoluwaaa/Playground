package com.playground

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.playground.cart.MediaContentType
import com.playground.cart.SocialChatUpdate
import com.playground.cart.SocialChatsScreenLayout
import com.playground.cart.SocialChatsUiState
import com.playground.cart.SocialSticker
import com.playground.lamme.LammeTheme
import com.playground.lamme.MainAODScreen
import com.playground.ui.theme.PlaygroundTheme
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.loose.mediaplayer.LooseApp
import com.playground.ui.theme.LooseTheme

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            startApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasRequiredPermissions()) {
            startApp()
        } else {
            requestPermissions()
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        setContent {
            LooseTheme {
                PermissionScreen(
                    onRequestPermissions = {
                        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_MEDIA_VIDEO
                            )
                        } else {
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        permissionLauncher.launch(permissions)
                    }
                )
            }
        }
    }

    private fun startApp() {
        setContent {
            LooseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LooseApp()
                }
            }
        }
    }
}

@Composable
fun PermissionScreen(onRequestPermissions: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Storage Permission Required",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loose needs access to your media files to play audio and video.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onRequestPermissions) {
                Text("Grant Permission")
            }
        }
    }
}

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Keep screen on for AOD
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//
//        // Enable edge-to-edge display
//        enableEdgeToEdge()
//
//        // Hide system bars for immersive AOD experience
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//
//        setContent {
//            LammeTheme {
//                MainAODScreen()
//            }
//        }
//    }
//}

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            val mockUpdates = listOf(
//                // Left Avatar (Cookies & Play button)
//                SocialChatUpdate("id_left", "https://picsum.photos/400/400?image=10", MediaContentType.VIDEO, true,
//                    relativePositionX = 0.05f, relativePositionY = 0.15f, sizeFactor = 1.2f, zIndex = 2f,
//                    overlays = listOf(
//                        // Pig sticker: Use a public domain sticker image
//                        SocialSticker("https://cdn.pixabay.com/photo/2013/07/13/11/48/pig-158756_1280.png", relativeOffsetX = -1.2f, relativeOffsetY = 1.2f, sizeFactor = 2.0f),
//                        SocialSticker("https://picsum.photos/50/50?image=66", relativeOffsetX = -0.5f, relativeOffsetY = 1.5f, sizeFactor = 0.5f)
//                    )
//                ),
//                // Central Stack (Stadium image)
//                SocialChatUpdate("id_stack", "https://picsum.photos/400/400?image=20", MediaContentType.STACK, false,
//                    relativePositionX = 0.35f, relativePositionY = 0.05f, sizeFactor = 1.0f, zIndex = 3f,
//                    overlays = emptyList()
//                ),
//                // Bottom Mid Avatar (Lowest Z-Index, possibly behind stack)
//                SocialChatUpdate("id_mid", "url_3", MediaContentType.VIDEO, true,
//                    relativePositionX = 0.3f, relativePositionY = 0.5f, sizeFactor = 0.7f, zIndex = 0f,
//                    overlays = emptyList()
//                ),
//                // Right Avatar (Man in yellow cap)
//                SocialChatUpdate("id_right", "https://picsum.photos/400/400?image=30", MediaContentType.IMAGE, false,
//                    relativePositionX = 0.6f, relativePositionY = 0.25f, sizeFactor = 1.1f, zIndex = 1f,
//                    overlays = listOf(
//                        // "I see" bubble: Use a special URL flag to trigger the text bubble implementation
//                        SocialSticker("bubble_flag/text", relativeOffsetX = 1.0f, relativeOffsetY = -0.5f, sizeFactor = 1.5f),
//                        // Eye sticker: Use another sticker image
//                        SocialSticker("https://cdn.pixabay.com/photo/2016/06/15/07/31/eye-1457811_1280.png", relativeOffsetX = 1.5f, relativeOffsetY = 1.0f, sizeFactor = 1.2f)
//                    )
//                )
//            )
//            val mockUiState = SocialChatsUiState(isLoading = false, updates = mockUpdates)
//            PlaygroundTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//
//                    SocialChatsScreenLayout(
//                        uiState = mockUiState,
//                        onAvatarClicked = { /* */ },
//                        onSearchClicked = { /* */ }
//                    )
//
//                }
//            }
//        }
//    }
//}

// Assume all Lang* classes (LangAppTheme, LangVocabularyMatchScreen, LangMatchWord, etc.)
// and the LangSpacing constant are available in this scope, likely imported from a separate file.
// For example, if the previous code was in 'LangMatchComponents.kt', you'd import them.
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            // Using LangAppTheme instead of the unprovided PlaygroundTheme
//            LangAppTheme {
//                // Prepare the full multi-page data model
//                val pages = remember { buildMockPages() }
//
//                // Initial state setup using the multi-page structure
//                val initialState = remember {
//                    LangVocabularyMatchUiState(
//                        lessonTitle = "German Vocabulary Match",
//                        pages = pages,
//                        pageCount = pages.size,
//                        currentPage = 0,
//                        currentMatches = emptyList(), // Matches start empty for the first page
//                        liveStreak = 8,
//                        livePoints = 120,
//                        liveTime = "0:45",
//                        checkMatchesEnabled = false,
//                    )
//                }
//
//                // Manage state using 'remember'
//                var uiState by remember { mutableStateOf(initialState) }
//
//                // Define the event handler logic
//                val onEvent: (LangVocabularyMatchUiEvent) -> Unit = { event ->
//                    when (event) {
//                        LangVocabularyMatchUiEvent.OnBackClick -> {
//                            // Implement navigation logic (e.g., finish(), navController.popBackStack())
//                        }
//                        LangVocabularyMatchUiEvent.OnCheckMatchesClick -> {
//                            // Implement correctness check and lesson completion logic
//                            // For simplicity, we'll just log or show a message
//                            println("Checking matches on final page: ${uiState.currentMatches}")
//                        }
//                        is LangVocabularyMatchUiEvent.OnPageChange -> {
//                            // Reset current matches and check button state for the new page
//                            uiState = uiState.copy(
//                                currentPage = event.newPageIndex,
//                                currentMatches = emptyList(),
//                                checkMatchesEnabled = false
//                            )
//                        }
//                        is LangVocabularyMatchUiEvent.OnMatchMade -> {
//                            // The LangVocabularyMatchScreen component already updates livePoints/Streak
//                            // We just ensure the matches and check button state are correct
//                            val currentPageData = uiState.pages.getOrNull(uiState.currentPage)
//                            val isComplete = currentPageData != null &&
//                                    event.newMatches.size == currentPageData.foreignWords.size
//
//                            // This update should include the updated livePoints and liveStreak
//                            // which are handled *internally* in LangVocabularyMatchScreen in the example.
//                            // In a real ViewModel, this logic would live here.
//                            uiState = uiState.copy(
//                                currentMatches = event.newMatches,
//                                checkMatchesEnabled = isComplete,
//                                // Mock metric updates
//                                livePoints = uiState.livePoints + 15,
//                                liveStreak = if (event.newMatches.size % 3 == 0) uiState.liveStreak + 1 else uiState.liveStreak
//                            )
//                        }
//                    }
//                }
//
//                LangVocabularyMatchScreen(
//                    state = uiState,
//                    onEvent = onEvent
//                )
//            }
//        }
//    }
//}
//
//// Helper function to create the mock data pages
//private fun buildMockPages(): List<LangVocabularyMatchPage> {
//    val page1Foreign = listOf(
//        LangMatchWord("Hund", "Hund", LangMatchWordType.FOREIGN),
//        LangMatchWord("Katze", "Katze", LangMatchWordType.FOREIGN),
//        LangMatchWord("Essen", "Essen", LangMatchWordType.FOREIGN),
//        LangMatchWord("Trinken", "Trinken", LangMatchWordType.FOREIGN),
//        LangMatchWord("Garten", "Garten", LangMatchWordType.FOREIGN)
//    )
//
//    val page1Translation = listOf(
//        LangMatchWord("Cat", "Katze", LangMatchWordType.TRANSLATION),
//        LangMatchWord("Garden", "Garten", LangMatchWordType.TRANSLATION),
//        LangMatchWord("Food", "Essen", LangMatchWordType.TRANSLATION),
//        LangMatchWord("Drink", "Trinken", LangMatchWordType.TRANSLATION),
//        LangMatchWord("Dog", "Hund", LangMatchWordType.TRANSLATION)
//    ).shuffled()
//
//    val page1CorrectMatches = listOf(
//        LangMatchPair("Hund", "Dog"),
//        LangMatchPair("Katze", "Cat"),
//        LangMatchPair("Essen", "Food"),
//        LangMatchPair("Trinken", "Drink"),
//        LangMatchPair("Garten", "Garden")
//    )
//
//    val page2Foreign = listOf(
//        LangMatchWord("Apfel", "Apfel", LangMatchWordType.FOREIGN),
//        LangMatchWord("Buch", "Buch", LangMatchWordType.FOREIGN),
//        LangMatchWord("Wasser", "Wasser", LangMatchWordType.FOREIGN),
//        LangMatchWord("Sonne", "Sonne", LangMatchWordType.FOREIGN),
//        LangMatchWord("Haus", "Haus", LangMatchWordType.FOREIGN)
//    )
//
//    val page2Translation = listOf(
//        LangMatchWord("Sun", "Sonne", LangMatchWordType.TRANSLATION),
//        LangMatchWord("Water", "Wasser", LangMatchWordType.TRANSLATION),
//        LangMatchWord("Book", "Buch", LangMatchWordType.TRANSLATION),
//        LangMatchWord("House", "Haus", LangMatchWordType.TRANSLATION),
//        LangMatchWord("Apple", "Apfel", LangMatchWordType.TRANSLATION)
//    ).shuffled()
//
//    val page2CorrectMatches = listOf(
//        LangMatchPair("Apfel", "Apple"),
//        LangMatchPair("Buch", "Book"),
//        LangMatchPair("Wasser", "Water"),
//        LangMatchPair("Sonne", "Sun"),
//        LangMatchPair("Haus", "House")
//    )
//
//    return listOf(
//        LangVocabularyMatchPage(page1Foreign, page1Translation, page1CorrectMatches),
//        LangVocabularyMatchPage(page2Foreign, page2Translation, page2CorrectMatches)
//    )
//}
//
////class MainActivity : ComponentActivity() {
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        enableEdgeToEdge()
////        setContent {
////            PlaygroundTheme {
////
////                val foreignWords = listOf(
////                    LangMatchWord("Hund", "Hund", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Katze", "Katze", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Essen", "Essen", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Trinken", "Trinken", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Garten", "Garten", LangMatchWordType.FOREIGN)
////                )
////
////                val translationWords = listOf(
////                    LangMatchWord("Cat", "Katze", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Garden", "Garten", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Food", "Essen", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Drink", "Trinken", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Dog", "Hund", LangMatchWordType.TRANSLATION)
////                ).shuffled()
////
////                // Mock initial state
////                // In MainActivity.kt inside onCreate
////                val mockState = LangVocabularyMatchUiState(
////                    lessonTitle = "Vocabulary Match",      // <-- ADDED EXPLICITLY
////                    instruction = "Drag to connect the matching words", // <-- ADDED EXPLICITLY
////                    foreignWords = foreignWords,
////                    translationWords = translationWords,
////                    currentMatches = emptyList(),
////                    liveStreak = 8,                        // <-- ADDED EXPLICITLY
////                    livePoints = 120,                      // <-- ADDED EXPLICITLY
////                    liveTime = "0:45",                     // <-- ADDED EXPLICITLY
////                    checkMatchesEnabled = false
////                )
////
////                LangAppTheme {
////                    LangVocabularyMatchScreen(
////                        state = mockState,
////                        onEvent = {}
////                    )
////                }
////            }
////        }
////    }
////}
//
//
//
///*
//
// */
//
////class MainActivity : ComponentActivity() {
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        enableEdgeToEdge()
////        setContent {
////            PlaygroundTheme {
////
////                val foreignWords = listOf(
////                    LangMatchWord("Hund", "Hund", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Katze", "Katze", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Essen", "Essen", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Trinken", "Trinken", LangMatchWordType.FOREIGN),
////                    LangMatchWord("Garten", "Garten", LangMatchWordType.FOREIGN)
////                )
////
////                val translationWords = listOf(
////                    LangMatchWord("Cat", "Cat", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Garden", "Garden", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Food", "Food", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Drink", "Drink", LangMatchWordType.TRANSLATION),
////                    LangMatchWord("Dog", "Dog", LangMatchWordType.TRANSLATION)
////                ).shuffled()
////
////                // Mock initial state
////                val mockState = LangVocabularyMatchUiState(
////                    foreignWords = foreignWords,
////                    translationWords = translationWords,
////                    currentMatches = emptyList(),
////                    checkMatchesEnabled = false
////                )
////
////                LangAppTheme {
////                    LangVocabularyMatchScreen(
////                        state = mockState,
////                        onEvent = {}
////                    )
////                }
////            }
////        }
////    }
////}
////
