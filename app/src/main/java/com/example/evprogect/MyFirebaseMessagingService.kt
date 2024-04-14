package com.example.evprogect

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Выведите данные уведомления в лог для отладки
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Проверьте, есть ли уведомление с данными
        remoteMessage.notification?.let {
            Log.d(TAG, "Notification Message Body: ${it.body}")
            // Здесь можно обработать полученное уведомление, например, показать его пользователю
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
