package com.playground.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel
class CartChatsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CartChatsUiState(isLoading = true))
    val uiState: StateFlow<CartChatsUiState> = _uiState

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            try {
                // TODO: Call repository to fetch stories and chat threads
                val mockStories = listOf(
                    CartStory("s1", "url/story1.jpg", "url/avatar1.jpg"),
                    CartStory("s2", "url/story2.jpg", overlayText = "I see")
                )
                val mockChats = listOf(
                    CartChatThread("c1", false, listOf("url/s_aeri.jpg"), "Somun Ae-Ri", "Don't miss conversations", "00:13", 2),
                    CartChatThread("c3", false, listOf("url/s_aeri.jpg"), "Somun Ae-Ri", "Don't miss conversations", "00:13", 2),
                    CartChatThread("c2", true, listOf("url/p1.jpg", "url/p2.jpg"), "Developer team", "Slava: Background removal", "00:13", 4)
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        stories = mockStories,
                        chatThreads = mockChats
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = "Failed to load chats."
                    )
                }
            }
        }
    }

    fun onChatClicked(chatId: String) {
        // TODO: Navigate to CartChatDetailsScreen
        println("Navigating to chat: $chatId")
    }

    fun onStoryClicked(storyId: String) {
        // TODO: Navigate to CartStoryViewer
        println("Viewing story: $storyId")
    }

    fun onSearchClicked() {
        _uiState.update { it.copy(isSearching = !it.isSearching) }
        // TODO: Implement in-page search logic or navigation
        println("Search clicked. isSearching: ${_uiState.value.isSearching}")
    }

    // Handlers for CartBottomActionInput
    fun onListClicked() {
        // TODO: Open list or menu (e.g., bottom sheet)
        println("Bottom list/menu clicked")
    }

    fun onActionTextClicked() {
        // TODO: Custom action/shortcut for text input
        println("Bottom Text action clicked")
    }

    fun onActionPlusClicked() {
        // TODO: Open attachment or media selection
        println("Bottom Plus action clicked")
    }

    fun onActionBarsClicked() {
        // TODO: Open a filter or sorting dialog
        println("Bottom Bars action clicked")
    }
}