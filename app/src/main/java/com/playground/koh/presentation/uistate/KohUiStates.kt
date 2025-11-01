package com.playground.koh.presentation.uistate

import androidx.compose.runtime.Immutable
import com.playground.koh.domain.model.Category

@Immutable
data class KohHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categories: List<Category> = emptyList(),
    val newInImageUrl: String = ""
)

sealed class KohHomeEvent {
    data class CategorySelected(val categoryId: String) : KohHomeEvent()
    data class FavoriteToggled(val categoryId: String, val isFavorite: Boolean) : KohHomeEvent()
    object NewInClicked : KohHomeEvent()
}

@Immutable
data class KohLoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class KohLoginEvent {
    data class EmailChanged(val email: String) : KohLoginEvent()
    data class PasswordChanged(val password: String) : KohLoginEvent()
    object LoginClicked : KohLoginEvent()
    object ForgotPasswordClicked : KohLoginEvent()
    object FacebookLoginClicked : KohLoginEvent()
}
