package com.playground.fcm

import android.util.Log
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM token: $token")
        
        // Store token persistently
        val prefs = getSharedPreferences("FCM_PREFS", MODE_PRIVATE)
        prefs.edit { putString(FCM_TOKEN_KEY, token) }
        
        // Optional: Send to your server
        // sendTokenToServer(token)
        
        // Notify UI if using a shared Flow (see ViewModel below)
        TokenRepository.updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received: ${remoteMessage.notification?.title}")
        // Handle foreground notifications here (e.g., show in Compose Snackbar)
    }

    companion object {
        private const val TAG = "FCMService"
        const val FCM_TOKEN_KEY = "fcm_token"
    }
}