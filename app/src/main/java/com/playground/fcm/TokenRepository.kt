package com.playground.fcm

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object TokenRepository {
    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    fun updateToken(newToken: String) {
        _token.value = newToken
    }

    fun getStoredToken(context: Context): String? {
        val prefs = context.getSharedPreferences("FCM_PREFS", Context.MODE_PRIVATE)
        return prefs.getString(MyFirebaseMessagingService.FCM_TOKEN_KEY, null)
    }
}