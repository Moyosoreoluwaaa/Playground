package com.playground.fcm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FcmViewModel(application: Application) : AndroidViewModel(application) {

    val token = TokenRepository.token
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TokenRepository.getStoredToken(application)
        )

    fun fetchToken() {
        viewModelScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                Log.d(TAG, "Fetched FCM token: $fcmToken")
                TokenRepository.updateToken(fcmToken)
            } catch (e: Exception) {
                Log.w(TAG, "Fetching FCM token failed", e)
            }
        }
    }

    companion object {
        private const val TAG = "FcmViewModel"
    }
}